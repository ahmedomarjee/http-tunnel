package es.malvarez.http_tunnel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

/**
 * Factory for getting the transport factory.
 */
public interface TransportFactory {

    /**
     * Gets the request with the configured tunnel.
     *
     * @param request     request.
     * @param response    response.
     * @param destination destination for the tunnel.
     * @return request.
     */
    Request buildRequest(HttpServletRequest request, HttpServletResponse response, URL destination);
}
