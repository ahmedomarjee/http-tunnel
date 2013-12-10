package es.malvarez.http_tunnel;

import java.io.IOException;

/**
 * HTTP servlet response wrapper.
 */
public interface Response {

    /**
     * Flushes the response.
     */
    void flush() throws IOException;
}
