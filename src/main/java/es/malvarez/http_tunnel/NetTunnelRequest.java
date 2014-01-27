package es.malvarez.http_tunnel;


import es.malvarez.http_tunnel.util.IOUtils;

import javax.net.ssl.*;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
        try {
            HttpURLConnection connection = enableAllCertificates(getConnection(destination));
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error getting SSL connection factory", e);
        } catch (KeyManagementException e) {
            throw new RuntimeException("Error with the key management", e);
        }
    }

    protected HttpURLConnection getConnection(URL address) throws IOException {
        URLConnection connection = address.openConnection();
        if (!(connection instanceof HttpURLConnection)) {
            throw new IllegalArgumentException(String.format("Connection of type %s, expected : %s", connection.getClass(), HttpURLConnection.class));
        }
        return (HttpURLConnection) connection;
    }

    protected HttpURLConnection enableAllCertificates(HttpURLConnection connection) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection secure = (HttpsURLConnection) connection;
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[]{new TrustAllTrustManager()}, new SecureRandom());
            secure.setSSLSocketFactory(context.getSocketFactory());

            secure.setHostnameVerifier(new EmptyHostnameVerifier());
        }
        return connection;
    }

    protected void setOutputData(File data, HttpURLConnection connection) throws IOException {
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(IOUtils.DEFAULT_BUFFER_SIZE);
        IOUtils.copy(new FileInputStream(data), connection.getOutputStream());
    }

    protected static class TrustAllTrustManager implements X509TrustManager {

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }

    protected static class EmptyHostnameVerifier implements HostnameVerifier {

        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
