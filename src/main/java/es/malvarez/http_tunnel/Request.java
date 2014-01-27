package es.malvarez.http_tunnel;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class Request {

    private String contextPath;
    private String method;
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private Map<String, Cookie> cookies = new HashMap<String, Cookie>();
    private File data;
    private boolean ajax;
    private boolean logout;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void addHeader(String header, List<String> value) {
        this.headers.put(header, value);
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public List<String> removeHeader(String header) {
        return this.headers.remove(header);
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

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }

    public Long getDataLength() {
        return data == null ? 0L : data.length();
    }

    public boolean isAjax() {
        return ajax;
    }

    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    public boolean isLogout() {
        return logout;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }
}
