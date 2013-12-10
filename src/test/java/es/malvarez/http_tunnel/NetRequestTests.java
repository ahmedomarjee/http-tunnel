package es.malvarez.http_tunnel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NetRequest}
 */
public class NetRequestTests {

    @Test
    public void testExecute() throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(Methods.GET.getName());
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }
        });
        NetAddress address = new NetAddress("http://www.google.es");

        NetRequest netRequest = new NetRequest(request, response, address);
        NetResponse netResponse = netRequest.execute();
        netResponse.flush();

        assertThat(output.toByteArray(), notNullValue());
    }

    @Test
    public void testHeadersParsedRight() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(Methods.GET.getName());
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList(Header.LOCATION.getName())));
        when(request.getHeaders(Header.LOCATION.getName())).thenReturn(Collections.enumeration(Arrays.asList("www.google.es", "www.yahoo.es")));
        HttpServletResponse response = mock(HttpServletResponse.class);
        NetAddress address = new NetAddress("http://www.google.es");

        NetRequest netRequest = new NetRequest(request, response, address);
        assertThat(netRequest.getHeaders().get(Header.LOCATION.getName()), equalTo("www.google.es, www.yahoo.es"));
    }
}
