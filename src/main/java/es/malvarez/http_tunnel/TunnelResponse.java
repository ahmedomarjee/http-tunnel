package es.malvarez.http_tunnel;

import java.io.IOException;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public interface TunnelResponse {

    /**
     * Flushes the response.
     */
    void parse(Response response) throws IOException;
}
