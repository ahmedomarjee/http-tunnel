package es.malvarez.http_tunnel;

import java.io.IOException;
import java.net.URL;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public interface TunnelRequest {

    /**
     * Method that executes the request.
     */
    TunnelResponse execute(Request request, URL destination) throws IOException;
}
