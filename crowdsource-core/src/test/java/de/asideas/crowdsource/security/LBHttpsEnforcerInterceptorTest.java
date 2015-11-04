package de.asideas.crowdsource.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class LBHttpsEnforcerInterceptorTest {

    private static final String APPLICATION_URL = "https://foo.bar";

    private LBHttpsEnforcerInterceptor lbHttpsEnforcerInterceptor;
    private MockHttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        lbHttpsEnforcerInterceptor = new LBHttpsEnforcerInterceptor();

        request = new MockHttpServletRequest();
        response = mock(HttpServletResponse.class);

        Whitebox.setInternalState(lbHttpsEnforcerInterceptor, "applicationUrl", APPLICATION_URL);
    }

    @Test
    public void shouldRedirectIfNoHeaderWasSet() throws Exception {
        boolean shouldContinue = lbHttpsEnforcerInterceptor.preHandle(request, response, null);

        assertThat(shouldContinue, is(false));
        verify(response).sendRedirect(APPLICATION_URL);
    }

    @Test
    public void shouldRedirectIfHeaderWasEmpty() throws Exception {
        request.addHeader(LBHttpsEnforcerInterceptor.X_FORWARDED_PROTO_HEADER, "");
        boolean shouldContinue = lbHttpsEnforcerInterceptor.preHandle(request, response, null);

        assertThat(shouldContinue, is(false));
        verify(response).sendRedirect(APPLICATION_URL);
    }

    @Test
    public void shouldNotRedirectIfHeaderWasHttps() throws Exception {
        request.addHeader(LBHttpsEnforcerInterceptor.X_FORWARDED_PROTO_HEADER, "HTTPS");
        boolean shouldContinue = lbHttpsEnforcerInterceptor.preHandle(request, response, null);

        assertThat(shouldContinue, is(true));
        verify(response, never()).sendRedirect(any());
    }
}