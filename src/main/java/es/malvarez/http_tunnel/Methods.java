package es.malvarez.http_tunnel;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Http methods
 */
public enum Methods {


    DELETE("DELETE"),
    HEAD("HEAD"),
    GET("GET"),
    OPTIONS("OPTIONS"),
    POST("POST"),
    PUT("PUT"),
    TRACE("TRACE");

    /**
     * Name of the method.
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param name name of the method.
     */
    private Methods(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the method of the request.
     *
     * @param request request.
     * @return method.
     */
    public static Methods forRequest(HttpServletRequest request) {
        Methods result = null;
        for (Methods method : values()) {
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
