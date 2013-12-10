package es.malvarez.http_tunnel;


import es.malvarez.http_tunnel.util.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Request implementation on top of {@link java.net.HttpURLConnection}
 */
public class NetRequest extends BaseRequest {


    /**
     * Default size for the header map.
     */
    protected static final int DEFAULT_HEADER_CAPACITY = 32;

    /**
     * Original request.
     */
    protected final HttpServletRequest request;

    /**
     * Original resopnse.
     */
    protected final HttpServletResponse response;

    /**
     * Headers to use.
     */
    protected final Map<String, String> headers;

    /**
     * Destination.
     */
    protected URL destination;

    /**
     * Constructor.
     *
     * @param request  request.
     * @param response response.
     */
    protected NetRequest(HttpServletRequest request, HttpServletResponse response, URL destination) {
        this.request = request;
        this.response = response;
        if (Protocol.forName(destination.getProtocol()) == null) {
            throw new IllegalArgumentException(String.format("Illegal protocol, supported: %s", Protocol.values()));
        }
        this.destination = destination;
        this.headers = new HashMap<String, String>(DEFAULT_HEADER_CAPACITY);
        this.initializeHeaders(request);
    }

    /**
     * Request.
     *
     * @param request request.
     */
    protected void initializeHeaders(HttpServletRequest request) {
        for (Enumeration headerNames = request.getHeaderNames(); headerNames.hasMoreElements(); ) {
            String headerName = (String) headerNames.nextElement();
            Header header = Header.forName(headerName);
            if (header.getType() == HeaderType.END_TO_END) {
                StringBuilder builder = new StringBuilder();
                for (Enumeration headerValues = request.getHeaders(headerName); headerValues.hasMoreElements(); ) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(headerValues.nextElement());
                }
                this.headers.put(header.getName(headerName), builder.toString());
            }
        }

        String forwardedFor = headers.get(Header.X_FORWARDED_FOR.getName());
        forwardedFor = forwardedFor == null ? request.getRemoteAddr() : String.format("%s, %s", forwardedFor, request.getRemoteAddr());
        this.headers.put(Header.X_FORWARDED_FOR.getName(), forwardedFor);
    }

    /**
     * Returns the header list.
     *
     * @return header list.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public NetResponse execute() throws IOException {
        HttpURLConnection connection = getConnection(this.destination);
        connection.setRequestMethod(this.request.getMethod());
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        for (Map.Entry<String, String> headerEntry : getHeaders().entrySet()) {
            connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }
        if (request.getHeader(Header.CONTENT_LENGTH.getName()) != null || request.getHeader(Header.TRANSFER_ENCODING.getName()) != null) {
            setInputData(request, connection);
        }
        connection.connect();
        return new NetResponse(this.response, connection);
    }

    /**
     * This method opens a new connection.
     *
     * @param address address.
     * @return connection.
     */
    protected HttpURLConnection getConnection(URL address) throws IOException {
        URLConnection connection = address.openConnection();
        if (!(connection instanceof HttpURLConnection)) {
            throw new IllegalArgumentException(String.format("Connection of type %s, expected : %s", connection.getClass(), HttpURLConnection.class));
        }
        return (HttpURLConnection) connection;
    }

    /**
     * Add the data for the connection.
     *
     * @param request    request.
     * @param connection connection.
     */
    protected void setInputData(HttpServletRequest request, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        IOUtils.copy(request.getInputStream(), connection.getOutputStream());
    }
}
