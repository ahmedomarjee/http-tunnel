package es.malvarez.http_tunnel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;

/**
 * Factory based on {@link java.net.HttpURLConnection}
 */
public class NetTransportFactory implements TransportFactory {

    @Override
    public Request buildRequest(HttpServletRequest request, HttpServletResponse response, Address destination) {
        if (!(destination instanceof NetAddress)) {
            throw new IllegalArgumentException(String.format("Adress %s is not valid, expected %s", destination.getClass(), NetAddress.class));
        }
        return new NetRequest(request, response, (NetAddress) destination);
    }

    @Override
    public Address buildAddress(String url) {
        try {
            return new NetAddress(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Wrong url %s", url), e);
        }
    }
}
