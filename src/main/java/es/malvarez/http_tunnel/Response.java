package es.malvarez.http_tunnel;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class Response {

    private int statusCode;
    private String statusMessage;
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private Map<String, Cookie> cookies = new HashMap<String, Cookie>();
    private byte[] data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void addHeader(String header, List<String> value) {
        this.headers.put(header, value);
    }

    public List<String> removeHeader(String header) {
        return this.headers.remove(header);
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void addCookie(Cookie cookie) {
        this.cookies.put(cookie.getName(), cookie);
    }

    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    public Cookie removeCookie(String cookie) {
        return this.cookies.remove(cookie);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
