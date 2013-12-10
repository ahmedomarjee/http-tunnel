package es.malvarez.http_tunnel;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of a java servlet capable of tunneling request to another server.
 */
public class TunnelServlet extends HttpServlet {

    /**
     * Parameter for the destination.
     */
    public static final String DESTINATION_PARAM = "destination";

    /**
     * Transport factory.
     */
    private TransportFactory transportFactory;

    /**
     * Host for the tunnel
     */
    private URL destination;

    /**
     * Initializes the transport factory and the tunnel configuration.
     *
     * @param config configuration.
     * @throws ServletException in case of error.
     */
    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.transportFactory = buildTransportFactory(config);
        this.destination = buildTunnelDestination(config);
        this.internalInit(config);
    }

    /**
     * Tunnels the HTTP request.
     *
     * @param req  request.
     * @param resp response.
     * @throws ServletException in case of exception.
     * @throws IOException      in case of IO exception.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        @SuppressWarnings("unchecked")
        Request request = this.transportFactory.buildRequest(wrapRequest(req), wrapResponse(resp), reWriteURL(req));
        Response response = request.execute();
        response.flush();
    }

    /**
     * Builds the transport factory to use in the servlet.
     *
     * @param config servlet configuration.
     * @return transport factory to use.
     */
    @SuppressWarnings("UnusedParameters")
    protected TransportFactory buildTransportFactory(ServletConfig config) throws ServletException {
        return new NetTransportFactory();
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
        StringBuilder uri = new StringBuilder(destination.toExternalForm());
        if (request.getPathInfo() != null) {
            uri.append(request.getPathInfo());
        }
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            int fragmentIndex = queryString.indexOf('#');
            String queryNoFrag = (fragmentIndex < 0 ? queryString : queryString.substring(0, fragmentIndex));
            uri.append(queryNoFrag);
            if (fragmentIndex >= 0) {
                uri.append('#');
                uri.append(queryString.substring(fragmentIndex + 1));
            }
        }
        return new URL(uri.toString());
    }

    /**
     * Wraps the request to perform several optimizations.
     *
     * @param request request.
     * @return wrapped request.
     */
    protected HttpServletRequest wrapRequest(HttpServletRequest request) {
        return request;
    }

    /**
     * Wraps the response to perform several optimizations.
     *
     * @param response response.
     * @return wrapped response.
     */
    protected HttpServletResponse wrapResponse(HttpServletResponse response) {
        return response;
    }

    /**
     * For subclasses
     *
     * @param config configuration
     */
    @SuppressWarnings("UnusedParameters")
    protected void internalInit(ServletConfig config) throws ServletException {

    }

    /**
     * Destination for the tunnel.
     *
     * @return destination.
     */
    public URL getDestination() {
        return destination;
    }

    /**
     * Transport factory for the tunnel.
     *
     * @return transport factory.
     */
    public TransportFactory getTransportFactory() {
        return transportFactory;
    }
}

