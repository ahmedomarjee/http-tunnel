package es.malvarez.http_tunnel;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public enum Method {

    DELETE("DELETE"),
    HEAD("HEAD"),
    GET("GET"),
    OPTIONS("OPTIONS"),
    POST("POST"),
    PUT("PUT"),
    TRACE("TRACE");

    private final String name;

    private Method(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Method forRequest(HttpServletRequest request) {
        Method result = null;
        for (Method method : values()) {
            if (method.name.equals(request.getMethod().toUpperCase())) {
                result = method;
                break;
            }
        }
        if (result == null) {
            throw new IllegalArgumentException(String.format("Method %s not supported, allowed : %s", request.getMethod(), Arrays.toString(values())));
        }
        return result;
    }
}
