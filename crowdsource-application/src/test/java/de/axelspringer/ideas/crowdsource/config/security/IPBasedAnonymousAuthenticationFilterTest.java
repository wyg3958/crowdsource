package de.axelspringer.ideas.crowdsource.config.security;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IPBasedAnonymousAuthenticationFilterTest {

    private IPBasedAnonymousAuthenticationFilter ipBasedAnonymousAuthenticationFilter = new IPBasedAnonymousAuthenticationFilter();

    @Test
    public void testCreateAuthenticationTrustingEveryone() throws Exception {

        trust("*");
        final HttpServletRequest request = mockRequest("some_ip");
        final Authentication authentication = ipBasedAnonymousAuthenticationFilter.createAuthentication(request);
        assertTrue(trustedAnonymousGranted(authentication));
    }

    @Test
    public void testCreateAuthenticationNonTrustedIp() throws Exception {

        trust("127.0.0.1");
        final HttpServletRequest request = mockRequest("10.5.4.8");
        final Authentication authentication = ipBasedAnonymousAuthenticationFilter.createAuthentication(request);
        assertFalse(trustedAnonymousGranted(authentication));
    }

    @Test
    public void testCreateAuthenticationTrustedIp() throws Exception {

        trust("10.5.4.8");
        final HttpServletRequest request = mockRequest("10.5.4.8");
        final Authentication authentication = ipBasedAnonymousAuthenticationFilter.createAuthentication(request);
        assertTrue(trustedAnonymousGranted(authentication));
    }

    @Test
    public void testCreateAuthenticationTrustedIpAsForwardedIp() throws Exception {

        trust("10.5.4.8");
        final HttpServletRequest request = mockRequest("foo_bar_ip");
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.5.4.8");
        final Authentication authentication = ipBasedAnonymousAuthenticationFilter.createAuthentication(request);
        assertTrue(trustedAnonymousGranted(authentication));
    }

    @Test
    public void testCreateAuthenticationTrustedIpAsForwardedIps() throws Exception {

        trust("10.5.4.8");
        final HttpServletRequest request = mockRequest("foo_bar_ip");
        when(request.getHeader("X-Forwarded-For")).thenReturn("foo, bar, 10.5.4.8");
        final Authentication authentication = ipBasedAnonymousAuthenticationFilter.createAuthentication(request);
        assertTrue(trustedAnonymousGranted(authentication));
    }

    @Test
    public void testCreateAuthenticationTrustedIps() throws Exception {

        trust("10.5.4.8, 10.5.4.9");
        assertTrue(trustedAnonymousGranted(ipBasedAnonymousAuthenticationFilter.createAuthentication(mockRequest("10.5.4.8"))));
        assertTrue(trustedAnonymousGranted(ipBasedAnonymousAuthenticationFilter.createAuthentication(mockRequest("10.5.4.9"))));
    }

    @Test
    public void testOneWildcardIps() {

        trust("10.5.4.*");
        assertTrue(trustedAnonymousGranted(ipBasedAnonymousAuthenticationFilter.createAuthentication(mockRequest("10.5.4.8"))));
        assertFalse(trustedAnonymousGranted(ipBasedAnonymousAuthenticationFilter.createAuthentication(mockRequest("10.5.5.8"))));
    }

    @Test
    public void testTwoWildcardIps() {

        trust("10.5.*.*");
        assertTrue(trustedAnonymousGranted(ipBasedAnonymousAuthenticationFilter.createAuthentication(mockRequest("10.5.4.8"))));
        assertTrue(trustedAnonymousGranted(ipBasedAnonymousAuthenticationFilter.createAuthentication(mockRequest("10.5.5.8"))));
    }

    private boolean trustedAnonymousGranted(Authentication authentication) {
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (Roles.ROLE_TRUSTED_ANONYMOUS.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private HttpServletRequest mockRequest(String ip) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn(ip);
        return request;
    }

    private void trust(String ips) {
        ReflectionTestUtils.setField(ipBasedAnonymousAuthenticationFilter, "trustedIps", ips);
    }
}