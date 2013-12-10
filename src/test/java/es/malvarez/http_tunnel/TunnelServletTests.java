package es.malvarez.http_tunnel;

import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.Test;
import org.mockito.internal.matchers.NotNull;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the tunnel servlet.
 */
public class TunnelServletTests {


    @Test
    public void testCorrectInit() throws ServletException {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameter(TunnelServlet.DESTINATION_PARAM)).thenReturn("http://www.google.es");

        TunnelServlet servlet = new TunnelServlet();
        servlet.init(config);

        assertThat(servlet.getDestination(), notNullValue());
    }

    @Test(expected = ServletException.class)
    public void testInitWithoutDestination() throws ServletException {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameter(TunnelServlet.DESTINATION_PARAM)).thenReturn(null);

        TunnelServlet servlet = new TunnelServlet();
        servlet.init(config);

        assertThat(servlet.getDestination(), notNullValue());
    }

    @Test(expected = ServletException.class)
    public void testInitWithWrongDestination() throws ServletException {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameter(TunnelServlet.DESTINATION_PARAM)).thenReturn("abc");

        TunnelServlet servlet = new TunnelServlet();
        servlet.init(config);

        assertThat(servlet.getDestination(), notNullValue());
    }


}
