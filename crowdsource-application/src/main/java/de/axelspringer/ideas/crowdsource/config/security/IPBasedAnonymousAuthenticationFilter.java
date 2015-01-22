package de.axelspringer.ideas.crowdsource.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Component
public class IPBasedAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

    @Value("${de.axelspringer.ideas.crowdsource.trustedips:145.243.200.0}")
    private String trustedIps;

    public IPBasedAnonymousAuthenticationFilter() {
        super("ANONYMOUS");
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {

        final ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.ROLE_UNTRUSTED_ANONYMOUS));

        if (ipWhiteListed(request.getRemoteAddr())) {
            authorities.add(new SimpleGrantedAuthority(Roles.ROLE_TRUSTED_ANONYMOUS));
        }
        return new AnonymousAuthenticationToken("ANONYMOUS", "ANONYMOUS", authorities);
    }

    private boolean ipWhiteListed(String remoteAddr) {

        if ("*".equals(trustedIps)) {
            return true;
        }
        for (String ip : trustedIps.split(",")) {
            if (ip.trim().equals(remoteAddr)) {
                return true;
            }
        }
        return false;
    }
}
