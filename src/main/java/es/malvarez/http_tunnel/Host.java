package es.malvarez.http_tunnel;

import java.net.URI;

/**
 * http_tunnel
 *
 * @author malvarez
 */
public class Host {

    protected final String hostname;
    protected final int port;
    protected final String schemeName;

    private Host(String hostname, int port, String schemeName) {
        this.hostname = hostname;
        this.port = port;
        this.schemeName = schemeName;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public static Host extractHost(final URI uri) {
        if (uri == null) {
            return null;
        }
        Host target = null;
        if (uri.isAbsolute()) {
            int port = uri.getPort();
            String host = uri.getHost();
            if (host == null) {
                host = uri.getAuthority();
                if (host != null) {
                    final int at = host.indexOf('@');
                    if (at >= 0) {
                        if (host.length() > at + 1) {
                            host = host.substring(at + 1);
                        } else {
                            host = null;
                        }
                    }
                    if (host != null) {
                        final int colon = host.indexOf(':');
                        if (colon >= 0) {
                            final int pos = colon + 1;
                            int len = 0;
                            for (int i = pos; i < host.length(); i++) {
                                if (Character.isDigit(host.charAt(i))) {
                                    len++;
                                } else {
                                    break;
                                }
                            }
                            if (len > 0) {
                                try {
                                    port = Integer.parseInt(host.substring(pos, pos + len));
                                } catch (final NumberFormatException ex) {
                                }
                            }
                            host = host.substring(0, colon);
                        }
                    }
                }
            }
            final String scheme = uri.getScheme();
            if (host != null) {
                target = new Host(host, port, scheme);
            }
        }
        return target;
    }
}
