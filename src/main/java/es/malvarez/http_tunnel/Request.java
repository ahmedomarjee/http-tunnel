package es.malvarez.http_tunnel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class Request {

    private final String method;
    private final Map<String, List<String>> headers;
    private final Map<String, String> cookies;
    private final byte[] data;

    private Request(String method, Map<String, List<String>> headers, Map<String, String> cookies, byte[] data) {
        this.method = method;
        this.headers = Collections.unmodifiableMap(headers);
        this.cookies = Collections.unmodifiableMap(cookies);
        this.data = data;
    }

    public String getMethod() {
        return method;
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
        private String method = Method.GET.getName();
        private Map<String, List<String>> headers = Collections.emptyMap();
        private Map<String, String> cookies = Collections.emptyMap();
        private byte[] data = new byte[0];

        public Builder method(String method) {
            this.method = method;
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

        public Request build() {
            return new Request(method, headers, cookies, data);
        }
    }


}
