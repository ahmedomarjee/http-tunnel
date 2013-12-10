package es.malvarez.http_tunnel;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class NetTunnelFactory implements TunnelFactory {

    public TunnelRequest buildRequest() {
        return new NetTunnelRequest();
    }
}
