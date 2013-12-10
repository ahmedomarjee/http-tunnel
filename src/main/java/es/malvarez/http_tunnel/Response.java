package es.malvarez.http_tunnel;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class Response {

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, List<String>> headers;
    private final Map<String, String> cookies;
    private final byte[] data;

    public Response(int statusCode, String statusMessage, Map<String, List<String>> headers, Map<String, String> cookies, byte[] data) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.cookies = cookies;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public byte[] getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int statusCode = HttpServletResponse.SC_OK;
        private String statusMessage = "";
        private Map<String, List<String>> headers = Collections.emptyMap();
        private Map<String, String> cookies = Collections.emptyMap();
        private byte[] data = new byte[0];

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder cookies(Map<String, String> cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder data(byte[] data) {
            this.data = data;
            return this;
        }

        public Response build() {
            return new Response(statusCode, statusMessage, headers, cookies, data);
        }
    }
}
