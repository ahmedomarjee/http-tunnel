package es.malvarez.http_tunnel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public interface TunnelFactory {

    TunnelRequest buildRequest();
}
