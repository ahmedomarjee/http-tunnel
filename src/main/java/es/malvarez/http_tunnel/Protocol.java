package es.malvarez.http_tunnel;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public enum Protocol {

    HTTP,
    HTTPS;

    public static Protocol forName(String name) {
        Protocol result = null;
        for (Protocol protocol : values()) {
            if (protocol.name().equalsIgnoreCase(name)) {
                result = protocol;
                break;
            }
        }
        return result;
    }
}
