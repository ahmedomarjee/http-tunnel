package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Response implementation on top of {@link java.net.HttpURLConnection}
 */
public class NetResponse extends BaseResponse {

    /**
     * Delegated response to flush to.
     */
    private final HttpServletResponse response;

    /**
     * Connection to use.
     */
    private final HttpURLConnection connection;

    /**
     * Constructor.
     *
     * @param response   original response.
     * @param connection connection to use.
     */
    public NetResponse(HttpServletResponse response, HttpURLConnection connection) {
        this.response = response;
        this.connection = connection;
    }

    @Override
    public void flush() throws IOException {
        IOUtils.copy(this.connection.getInputStream(), response.getOutputStream());
        response.flushBuffer();
        this.connection.disconnect();
    }
}
