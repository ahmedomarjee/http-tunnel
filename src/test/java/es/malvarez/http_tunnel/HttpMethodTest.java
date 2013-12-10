package es.malvarez.http_tunnel;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link Method} enumeration
 */
public class HttpMethodTest {

    @Test
    public void testValidMethod() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        assertThat(Method.forRequest(request), equalTo(Method.GET));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMethod() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("INVALID");

        Method.forRequest(request);
    }
}
