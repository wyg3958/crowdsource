package de.axelspringer.ideas.crowdsource.config.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class IPBasedAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

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
        return new AnonymousAuthenticationToken("ANONYMOUS", Roles.ROLE_TRUSTED_ANONYMOUS, authorities);
    }

    private boolean ipWhiteListed(String remoteAddr) {

        // TODO
        return "".equals(remoteAddr) || "".equals(remoteAddr);
    }
}
