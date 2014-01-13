package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.IOUtils;

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

    public void parse(Response response) throws IOException {
        this.connection.connect();
        try {
            response.setStatusCode(connection.getResponseCode());
            response.setStatusMessage(connection.getResponseMessage());
            for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                if (entry.getKey() != null) {
                    response.addHeader(entry.getKey(), entry.getValue());
                }
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(
                        connection.getContentLength() <= 0 ? IOUtils.DEFAULT_BUFFER_SIZE : connection.getContentLength()
                );
                IOUtils.copy(connection.getInputStream(), baos);
                response.setData(baos.toByteArray());
            } catch (IOException e) {
                response.setData(new byte[0]);
            }
        } finally {
            connection.disconnect();
        }
    }

}
