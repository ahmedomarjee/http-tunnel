package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.IOUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class TunnelServlet extends HttpServlet {


    protected static final String DESTINATION_PARAM = "destination";
    protected static final BitSet ASCII_QUERY_CHARS;

    static {
        char[] c_unreserved = "_-!.~'()*".toCharArray();
        char[] c_punct = ",;:$&+=".toCharArray();
        char[] c_reserved = "?/[]@".toCharArray();

        ASCII_QUERY_CHARS = new BitSet(128);
        for (char c = 'a'; c <= 'z'; c++) ASCII_QUERY_CHARS.set((int) c);
        for (char c = 'A'; c <= 'Z'; c++) ASCII_QUERY_CHARS.set((int) c);
        for (char c = '0'; c <= '9'; c++) ASCII_QUERY_CHARS.set((int) c);
        for (char c : c_unreserved) ASCII_QUERY_CHARS.set((int) c);
        for (char c : c_punct) ASCII_QUERY_CHARS.set((int) c);
        for (char c : c_reserved) ASCII_QUERY_CHARS.set((int) c);

        ASCII_QUERY_CHARS.set((int) '%');
    }

    public static final Logger log = Logger.getLogger(TunnelServlet.class.getName());


    private TunnelFactory tunnelFactory;

    private URL destination;


    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.tunnelFactory = buildTransportFactory(config);
        this.destination = buildTunnelDestination(config);
        this.internalInit(config);
    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Request request = parseRequest(wrapRequest(servletRequest));
            service(request, reWriteURL(servletRequest), wrapResponse(servletResponse));
        } catch (Throwable e) {
            log.log(Level.SEVERE, "Error tunneling response", e);
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error tunneling the request");
        }
    }

    protected void service(Request request, URL url, HttpServletResponse servletResponse) throws IOException {
        Response response = this.tunnelFactory.buildRequest().execute(request, url).get();
        if (response.getStatusCode() >= HttpServletResponse.SC_MULTIPLE_CHOICES && response.getStatusCode() < HttpServletResponse.SC_NOT_MODIFIED) {
            handleRedirect(request, response, servletResponse);
        } else {
            handleResponse(response, servletResponse);
        }
    }

    protected void handleRedirect(Request request, Response response, HttpServletResponse servletResponse) throws IOException {
        List<String> locations = request.getHeaders().get(Header.LOCATION.getName());
        if (locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException(String.format("Response code %s without Location header", response.getStatusCode()));
        }
        String location = locations.get(0);
        if (location.startsWith(destination.toExternalForm())) {
            service(request, new URL(location), servletResponse);
        } else {
            servletResponse.sendRedirect(location);
        }
    }

    @SuppressWarnings("deprecation")
    protected void handleResponse(Response response, HttpServletResponse servletResponse) throws IOException {
        servletResponse.setStatus(response.getStatusCode(), response.getStatusMessage());
        for (Map.Entry<String, List<String>> headerEntry : Header.filter(response.getHeaders(), HeaderType.END_TO_END).entrySet()) {
            servletResponse.addHeader(headerEntry.getKey(), Header.toString(headerEntry.getValue()));
        }
        for (Map.Entry<String, String> cookieEntry : response.getCookies().entrySet()) {
            servletResponse.addCookie(new Cookie(cookieEntry.getKey(), cookieEntry.getValue()));
        }
        IOUtils.copy(new ByteArrayInputStream(response.getData()), servletResponse.getOutputStream());
    }

    /**
     * Builds the transport factory to use in the servlet.
     *
     * @param config servlet configuration.
     * @return transport factory to use.
     */
    @SuppressWarnings("UnusedParameters")
    protected TunnelFactory buildTransportFactory(ServletConfig config) throws ServletException {
        return new NetTunnelFactory();
    }

    /**
     * Builds the tunnel configuration to use in the servlet
     *
     * @param config servlet configuration
     * @return tunnel configuration.
     */
    protected URL buildTunnelDestination(ServletConfig config) throws ServletException {
        String destinationUrl = config.getInitParameter(DESTINATION_PARAM);
        if (destinationUrl == null || destinationUrl.isEmpty()) {
            throw new ServletException(String.format("Parameter %s is not defined", DESTINATION_PARAM));
        }
        try {
            return new URL(destinationUrl);
        } catch (MalformedURLException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Tries to rewrite the URL
     *
     * @param request request.
     */
    protected URL reWriteURL(HttpServletRequest request) throws IOException {
        StringBuilder uri = new StringBuilder(500);
        uri.append(destination.toExternalForm());
        if (request.getPathInfo() != null) {
            uri.append(encodeUriQuery(request.getPathInfo()));
        }
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            int fragIdx = queryString.indexOf('#');
            String queryNoFrag = (fragIdx < 0 ? queryString : queryString.substring(0, fragIdx));
            uri.append(encodeUriQuery(queryNoFrag));
            if (fragIdx >= 0) {
                uri.append('#');
                uri.append(encodeUriQuery(queryString.substring(fragIdx + 1)));
            }
        }
        return new URL(uri.toString());
    }

    protected Request parseRequest(HttpServletRequest request) throws IOException, URISyntaxException {
        Request.Builder builder = Request.builder();
        builder.method(request.getMethod());
        Map<String, List<String>> headers = Header.filter(parseHeaders(request), HeaderType.END_TO_END);
        if (headers.get(Header.HOST.getName()) != null) {
            Host host = Host.extractHost(destination.toURI());
            headers.put(
                    Header.HOST.getName(),
                    Arrays.asList(
                            host.getPort() != -1 ? String.format("%s:%s", host.getHostname(), host.getPort()) : host.getHostname()
                    )
            );
        }
        builder.headers(headers);
        Map<String, String> cookies = new HashMap<String, String>(request.getCookies().length);
        for (Cookie cookie : request.getCookies()) {
            cookies.put(cookie.getName(), cookie.getValue());
        }
        builder.cookies(cookies);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                request.getContentLength() <= 0 ? IOUtils.DEFAULT_BUFFER_SIZE : request.getContentLength()
        );
        IOUtils.copy(request.getInputStream(), baos);
        builder.data(baos.toByteArray());
        return builder.build();
    }

    protected Map<String, List<String>> parseHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>(Header.values().length);
        for (Enumeration headerNames = request.getHeaderNames(); headerNames.hasMoreElements(); ) {
            String headerName = (String) headerNames.nextElement();
            Header header = Header.forName(headerName);
            @SuppressWarnings("unchecked")
            List<String> headerValue = (List<String>) Collections.list(request.getHeaders(headerName));
            headers.put(header.getName() == null ? headerName : header.getName(), headerValue);
        }
        return headers;
    }

    protected static CharSequence encodeUriQuery(CharSequence in) {
        StringBuilder outBuf = null;
        Formatter formatter = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (ASCII_QUERY_CHARS.get((int) c)) {
                    escape = false;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {
                escape = false;
            }
            if (!escape) {
                if (outBuf != null)
                    outBuf.append(c);
            } else {
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                formatter.format("%%%02X", (int) c);//TODO
            }
        }
        return outBuf != null ? outBuf : in;
    }

    protected HttpServletRequest wrapRequest(HttpServletRequest request) {
        return request;
    }

    protected HttpServletResponse wrapResponse(HttpServletResponse response) {
        return response;
    }

    @SuppressWarnings("UnusedParameters")
    protected void internalInit(ServletConfig config) throws ServletException {

    }

    public URL getDestination() {
        return destination;
    }

    public TunnelFactory getTunnelFactory() {
        return tunnelFactory;
    }
}

