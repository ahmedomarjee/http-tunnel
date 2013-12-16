package es.malvarez.http_tunnel;

import es.malvarez.http_tunnel.util.CaseInsensitiveMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * HTTP Header data.
 */
public enum Header {

    /**
     * RFC 2616 (HTTP/1.1) Section 14.1
     */
    ACCEPT("Accept"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.2
     */
    ACCEPT_CHARSET("Accept-Charset"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.3
     */
    ACCEPT_ENCODING("Accept-Encoding"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.4
     */
    ACCEPT_LANGUAGE("Accept-Language"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.5
     */
    ACCEPT_RANGES("Accept-Ranges"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.6
     */
    AGE("Age"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.1, RFC 2616 (HTTP/1.1) Section 14.7
     */
    ALLOW("Allow"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.2, RFC 2616 (HTTP/1.1) Section 14.8
     */
    AUTHORIZATION("Authorization"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.9
     */
    CACHE_CONTROL("Cache-Control"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.10
     */
    CONNECTION("Connection", HeaderType.HOP_BY_HOP),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.3, RFC 2616 (HTTP/1.1) Section 14.11
     */
    CONTENT_ENCODING("Content-Encoding"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.12
     */
    CONTENT_LANGUAGE("Content-Language"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.4, RFC 2616 (HTTP/1.1) Section 14.13
     */
    CONTENT_LENGTH("Content-Length"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.14
     */
    CONTENT_LOCATION("Content-Location"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.15
     */
    CONTENT_MD5("Content-MD5"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.16
     */
    CONTENT_RANGE("Content-Range"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.5, RFC 2616 (HTTP/1.1) Section 14.17
     */
    CONTENT_TYPE("Content-Type"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.6, RFC 2616 (HTTP/1.1) Section 14.18
     */
    DATE("Date"),

    /**
     * RFC 2518 (WevDAV) Section 9.1
     */
    DAV("Dav"),

    /**
     * RFC 2518 (WevDAV) Section 9.2
     */
    DEPTH("Depth"),

    /**
     * RFC 2518 (WevDAV) Section 9.3
     */
    DESTINATION("Destination"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.19
     */
    ETAG("ETag"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.20
     */
    EXPECT("Expect"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.7, RFC 2616 (HTTP/1.1) Section 14.21
     */
    EXPIRES("Expires"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.8, RFC 2616 (HTTP/1.1) Section 14.22
     */
    FROM("From"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.23
     */
    HOST("Host"),

    /**
     * RFC 2518 (WevDAV) Section 9.4
     */
    IF("If"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.24
     */
    IF_MATCH("If-Match"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.9, RFC 2616 (HTTP/1.1) Section 14.25
     */
    IF_MODIFIED_SINCE("If-Modified-Since"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.26
     */
    IF_NONE_MATCH("If-None-Match"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.27
     */
    IF_RANGE("If-Range"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.28
     */
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),

    /**
     * RFC 2068 (HTTP/1.1) Section 19.7.1
     */
    KEEP_ALIVE("Keep-Alive", HeaderType.HOP_BY_HOP),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.10, RFC 2616 (HTTP/1.1) Section 14.29
     */
    LAST_MODIFIED("Last-Modified"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.11, RFC 2616 (HTTP/1.1) Section 14.30
     */
    LOCATION("Location"),

    /**
     * RFC 2518 (WevDAV) Section 9.5
     */
    LOCK_TOKEN("Lock-Token"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.31
     */
    MAX_FORWARDS("Max-Forwards"),

    /**
     * RFC 2518 (WevDAV) Section 9.6
     */
    OVERWRITE("Overwrite"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.12, RFC 2616 (HTTP/1.1) Section 14.32
     */
    PRAGMA("Pragma"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.33
     */
    PROXY_AUTHENTICATE("Proxy-Authenticate", HeaderType.HOP_BY_HOP),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.34
     */
    PROXY_AUTHORIZATION("Proxy-Authorization", HeaderType.HOP_BY_HOP),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.35
     */
    RANGE("Range"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.13, RFC 2616 (HTTP/1.1) Section 14.36
     */
    REFERER("Referer"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.37
     */
    RETRY_AFTER("Retry-After"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.14, RFC 2616 (HTTP/1.1) Section 14.38
     */
    SERVER("Server"),

    /**
     * RFC 2518 (WevDAV) Section 9.7
     */
    STATUS_URI("Status-URI"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.39
     */
    TE("TE", HeaderType.HOP_BY_HOP),

    /**
     * RFC 2518 (WevDAV) Section 9.8
     */
    TIMEOUT("Timeout"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.40
     */
    TRAILER("Trailer", HeaderType.HOP_BY_HOP),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.41
     */
    TRANSFER_ENCODING("Transfer-Encoding", HeaderType.HOP_BY_HOP),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.42
     */
    UPGRADE("Upgrade", HeaderType.HOP_BY_HOP),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.15, RFC 2616 (HTTP/1.1) Section 14.43
     */
    USER_AGENT("User-Agent"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.44
     */
    VARY("Vary"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.45
     */
    VIA("Via"),

    /**
     * RFC 2616 (HTTP/1.1) Section 14.46
     */
    WARNING("Warning"),

    /**
     * RFC 1945 (HTTP/1.0) Section 10.16, RFC 2616 (HTTP/1.1) Section 14.47
     */
    WWW_AUTHENTICATE("WWW-Authenticate"),

    /**
     * Forwarded for non standard header
     */
    X_FORWARDED_FOR("X-Forwarded-For"),

    /**
     * Last resort for non specific headers.
     */
    UNKNOWN(null);

    private static final Map<String, Header> VALUES = new CaseInsensitiveMap<Header>(new HashMap<String, Header>(values().length));

    static {
        for (Header header : values()) {
            VALUES.put(header.name, header);
        }
    }

    private final String name;
    private final HeaderType type;

    private Header(String name) {
        this(name, HeaderType.END_TO_END);
    }

    private Header(String name, HeaderType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public HeaderType getType() {
        return type;
    }

    public static Header forName(String name) {
        Header header = VALUES.get(name);
        return header == null ? Header.UNKNOWN : header;
    }

    public static Map<String, List<String>> filter(Map<String, List<String>> headers, HeaderType... type) {
        EnumSet<HeaderType> typesToInclude = EnumSet.copyOf(Arrays.asList(type));
        Map<String, List<String>> result = new HashMap<String, List<String>>(headers.size());
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (typesToInclude.contains(forName(entry.getKey()).getType())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static String toString(List<String> headerValue) {
        if (headerValue == null || headerValue.isEmpty()) {
            return "";
        }
        if (headerValue.size() == 1) {
            return headerValue.get(0);
        }
        StringBuilder builder = new StringBuilder();
        for (String value : headerValue) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
        return builder.toString();
    }
}
