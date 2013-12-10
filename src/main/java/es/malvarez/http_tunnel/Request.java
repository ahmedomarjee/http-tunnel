package es.malvarez.http_tunnel;

import java.io.IOException;

/**
 * Wrapper around the data from a HTTP request.
 */
public interface Request {

    /**
     * Method that executes the request.
     */
    Response execute() throws IOException;
}
