package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class NetTunnelResponse implements TunnelResponse {

    private final HttpURLConnection connection;

    public NetTunnelResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    public Response get() throws IOException {
        this.connection.connect();
        try {
            Response.Builder builder = Response.builder();
            builder.statusCode(connection.getResponseCode());
            builder.statusMessage(connection.getResponseMessage());
            builder.headers(connection.getHeaderFields());
            ByteArrayOutputStream baos = new ByteArrayOutputStream(
                    connection.getContentLength() <= 0 ? IOUtils.DEFAULT_BUFFER_SIZE : connection.getContentLength()
            );
            IOUtils.copy(connection.getInputStream(), baos);
            builder.data(baos.toByteArray());
            return builder.build();
        } finally {
            connection.disconnect();
        }
    }

}
