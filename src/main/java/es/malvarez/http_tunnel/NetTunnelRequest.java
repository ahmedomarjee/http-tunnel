package es.malvarez.http_tunnel;


import es.malvarez.http_tunnel.util.IOUtils;

import javax.servlet.http.Cookie;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class NetTunnelRequest implements TunnelRequest {

    public NetTunnelResponse execute(Request data, URL destination) throws IOException {
        if (Protocol.forName(destination.getProtocol()) == null) {
            throw new IllegalArgumentException(String.format("Illegal protocol, supported: %s", Protocol.values()));
        }
        HttpURLConnection connection = getConnection(destination);
        connection.setRequestMethod(data.getMethod());
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        for (Map.Entry<String, List<String>> headerEntry : data.getHeaders().entrySet()) {
            connection.setRequestProperty(headerEntry.getKey(), Header.toString(headerEntry.getValue()));
        }
        List<String> cookies = new LinkedList<String>();
        for (Cookie cookie : data.getCookies().values()) {
            cookies.add(String.format("%s=%s", cookie.getName(), cookie.getValue()));
        }
        if (!cookies.isEmpty()) {
            connection.setRequestProperty("Cookie", Header.toString(cookies));
        }
        if (data.getHeaders().get(Header.CONTENT_LENGTH.getName()) != null || data.getHeaders().get(Header.TRANSFER_ENCODING.getName()) != null) {
            setOutputData(data.getData(), connection);
        }
        connection.connect();
        return new NetTunnelResponse(connection);
    }

    protected HttpURLConnection getConnection(URL address) throws IOException {
        URLConnection connection = address.openConnection();
        if (!(connection instanceof HttpURLConnection)) {
            throw new IllegalArgumentException(String.format("Connection of type %s, expected : %s", connection.getClass(), HttpURLConnection.class));
        }
        return (HttpURLConnection) connection;
    }

    protected void setOutputData(byte[] data, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        IOUtils.copy(new ByteArrayInputStream(data), connection.getOutputStream());
    }
}
