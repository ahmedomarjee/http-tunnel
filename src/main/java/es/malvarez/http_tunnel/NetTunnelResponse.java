package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.FileUtils;
import es.malvarez.http_tunnel.util.IOUtils;

import java.io.*;
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
                File file = FileUtils.createTempFile("response");
                IOUtils.copy(connection.getInputStream(), new FileOutputStream(file));
                response.setData(new FileInputStream(file));
                response.setDataLength(file.length());
            } catch (IOException e) {
                response.setData(new ByteArrayInputStream(new byte[0]));
                response.setDataLength(0L);
            }
        } finally {
            connection.disconnect();
        }
    }

}
