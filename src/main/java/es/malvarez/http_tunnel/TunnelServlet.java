package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.FileUtils;
import es.malvarez.http_tunnel.util.IOUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class TunnelServlet extends HttpServlet {

    protected static final String URL_SEPARATOR = "/";
    protected static final String DESTINATION_PARAM = "destination";
    protected static final String DESTINATION_ON_FORBIDDEN_PARAM = "onForbidden";
    protected static final BitSet ASCII_QUERY_CHARS;
    protected static final String SET_COOKIE_HEADER = "Set-Cookie";
    protected static final String FORWARDED_COOKIE_PREFIX = "X-Forwarded-Cookie-";
    protected static final String _METHOD_PARAM = "_method";

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

    protected ServletContext servletContext;

    protected TunnelFactory tunnelFactory;

    protected URL destination;
    protected URL destinationOnForbidden;

    protected Map<String, List<String>> defaultHeaders;

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.servletContext = config.getServletContext();
        this.tunnelFactory = buildTransportFactory(config);
        this.destination = buildTunnelDestination(config);
        this.destinationOnForbidden = buildTunnelDestinationOnForbidden(config);
        this.defaultHeaders = buildDefaultHeaders(config);
        this.internalInit(config);
    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        FileUtils.init();
        try {
            Request request = parseRequest(wrapRequest(servletRequest));
            Response response = new Response();
            service(reWriteURL(servletRequest), request, response, wrapResponse(servletResponse));
        } catch (Throwable e) {
            servletContext.log("Error tunneling response", e);
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error tunneling the request");
        } finally {
            FileUtils.destroy();
        }
    }

    protected void service(URL url, Request request, Response response, HttpServletResponse servletResponse) throws IOException {
        filterRequestCustomCookies(request);
        this.tunnelFactory.buildRequest().execute(request, url).parse(response);
        filterResponseCustomCookies(request, response);
        if (response.getStatusCode() >= HttpServletResponse.SC_MULTIPLE_CHOICES && response.getStatusCode() < HttpServletResponse.SC_NOT_MODIFIED) {
            handleRedirect(request, response, servletResponse);
        } else if (response.getStatusCode() == HttpServletResponse.SC_FORBIDDEN) {
            handleRedirect(destinationOnForbidden.toExternalForm(), request, response, servletResponse);
        } else {
            handleResponse(response, servletResponse);
        }
    }

    protected void handleRedirect(Request request, Response response, HttpServletResponse servletResponse) throws IOException {
        List<String> locations = response.getHeaders().get(Header.LOCATION.getName());
        if (locations == null || locations.isEmpty()) {
            throw new IllegalArgumentException(String.format("Response code %s without Location header", response.getStatusCode()));
        }
        String location = locations.get(0);
        handleRedirect(location, request, response, servletResponse);
    }

    protected void handleRedirect(String location, Request request, Response response, HttpServletResponse servletResponse) throws IOException {
        if (location.startsWith(destination.toExternalForm())) {
            // Mutate the request to a GET after the POST
            request.setMethod(Method.GET.getName());
            request.setData(null);
            request.removeHeader(Header.CONTENT_LENGTH.getName());
            request.removeHeader(Header.TRANSFER_ENCODING.getName());
            service(new URL(location), request, response, servletResponse);
        } else {
            servletResponse.sendRedirect(location);
        }
    }

    @SuppressWarnings("deprecation")
    protected void handleResponse(Response response, HttpServletResponse servletResponse) throws IOException {
        response.addHeader(Header.CONTENT_LENGTH.getName(), Arrays.asList(Long.toString(response.getDataLength())));
        servletResponse.setStatus(response.getStatusCode(), response.getStatusMessage());
        for (Map.Entry<String, List<String>> headerEntry : Header.filter(response.getHeaders(), HeaderType.END_TO_END).entrySet()) {
            Header header = Header.forName(headerEntry.getKey());
            if (header != Header.LOCATION) {
                servletResponse.addHeader(headerEntry.getKey(), Header.toString(headerEntry.getValue()));
            }
        }
        for (Cookie cookieEntry : response.getCookies().values()) {
            servletResponse.addCookie(new Cookie(cookieEntry.getName(), cookieEntry.getValue()));
        }
        IOUtils.copy(response.getData(), servletResponse.getOutputStream());
    }

    protected void filterRequestCustomCookies(Request request) throws IOException {
        List<Cookie> customCookie = new LinkedList<Cookie>();
        for (Cookie cookie : request.getCookies().values()) {
            if (cookie.getName().startsWith(FORWARDED_COOKIE_PREFIX)) {
                customCookie.add(cookie);
            }
        }
        for (Cookie cookie : customCookie) {
            request.removeCookie(cookie.getName());
            Cookie newCookie = new Cookie(cookie.getName().replace(FORWARDED_COOKIE_PREFIX, ""), cookie.getValue());
            request.addCookie(newCookie);
        }
    }

    protected void filterResponseCustomCookies(Request request, Response response) throws IOException {
        List<String> cookies = response.removeHeader(SET_COOKIE_HEADER);
        if (cookies != null) {
            for (String cookieValue : cookies) {
                Cookie requestCookie = parseCookie(cookieValue);
                request.addCookie(requestCookie);

                Cookie responseCookie = new Cookie(String.format("%s%s", FORWARDED_COOKIE_PREFIX, requestCookie.getName()), requestCookie.getValue());
                response.addCookie(responseCookie);
            }
        }
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
    protected URL buildTunnelDestinationOnForbidden(ServletConfig config) throws ServletException {
        String destinationUrl = config.getInitParameter(DESTINATION_ON_FORBIDDEN_PARAM);
        if (destinationUrl == null || destinationUrl.isEmpty()) {
            throw new ServletException(String.format("Parameter %s is not defined", DESTINATION_ON_FORBIDDEN_PARAM));
        }
        try {
            return new URL(destinationUrl);
        } catch (MalformedURLException e) {
            throw new ServletException(e);
        }
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
     * Builds the default header list.
     */
    protected Map<String, List<String>> buildDefaultHeaders(ServletConfig config) throws ServletException {
        Map<String, List<String>> defaultHeaders = new HashMap<String, List<String>>(Header.values().length);
        for (Header header : Header.values()) {
            if (config.getInitParameter(header.getName()) != null) {
                defaultHeaders.put(header.getName(), Arrays.asList(config.getInitParameter(header.getName())));
            }
        }
        return defaultHeaders;
    }

    /**
     * Tries to rewrite the URL
     *
     * @param request request.
     */
    protected URL reWriteURL(HttpServletRequest request) throws IOException {
        StringBuilder uri = new StringBuilder(500);
        uri.append(destination);
        if (request.getPathInfo() != null) {
            String pathInfo = request.getPathInfo();
            uri.append(encodeUriQuery(pathInfo.substring(pathInfo.startsWith(URL_SEPARATOR) ? 1 : 0)));
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

    protected Request parseRequest(HttpServletRequest servletRequest) throws IOException, URISyntaxException {
        Request request = new Request();
        request.setContextPath(servletRequest.getContextPath());
        request.setMethod(servletRequest.getMethod());
        Map<String, List<String>> headers = Header.filter(parseHeaders(servletRequest), HeaderType.END_TO_END);
        if (headers.get(Header.HOST.getName()) != null) {
            Host host = Host.extractHost(destination.toURI());
            headers.put(
                    Header.HOST.getName(),
                    Arrays.asList(
                            host.getPort() != -1 ? String.format("%s:%s", host.getHostname(), host.getPort()) : host.getHostname()
                    )
            );
        }
        List<String> forwardedFor = headers.get(Header.X_FORWARDED_FOR.getName());
        if (forwardedFor == null) {
            forwardedFor = new LinkedList<String>();
        }
        forwardedFor.add(servletRequest.getRemoteAddr());
        headers.put(Header.X_FORWARDED_FOR.getName(), forwardedFor);
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }

        if (servletRequest.getCookies() != null) {
            for (Cookie cookie : servletRequest.getCookies()) {
                request.addCookie(cookie);
            }
        }

        File file = FileUtils.createTempFile("request");
        IOUtils.copy(servletRequest.getInputStream(), new FileOutputStream(file));
        request.setData(new FileInputStream(file));
        request.setDataLength(file.length());
        if (servletRequest.getParameter(_METHOD_PARAM) != null) {
            request.setMethod(servletRequest.getParameter(_METHOD_PARAM));
        }
        return request;
    }

    protected Map<String, List<String>> parseHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>(Header.values().length);
        headers.putAll(defaultHeaders);
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

    protected Cookie parseCookie(String cookie) {
        List<String> cookieParts = Arrays.asList(cookie.split(";"));
        String[] nameAndValue = cookieParts.get(0).split("=");
        Cookie realCookie = new Cookie(nameAndValue[0].trim(), nameAndValue[1].trim());
        for (int i = 1; i < cookieParts.size(); i++) {
            String part = cookieParts.get(i);
            if (part.startsWith("Path=")) {
                realCookie.setPath(part.trim().replace("Path=", ""));
            }
        }
        return realCookie;
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

    public URL getDestinationOnForbidden() {
        return destinationOnForbidden;
    }

    public TunnelFactory getTunnelFactory() {
        return tunnelFactory;
    }
}

