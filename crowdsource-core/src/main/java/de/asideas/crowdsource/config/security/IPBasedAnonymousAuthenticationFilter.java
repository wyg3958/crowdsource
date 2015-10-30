package de.asideas.crowdsource.config.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(IPBasedAnonymousAuthenticationFilter.class);

    @Value("${de.asideas.crowdsource.trustedips:*}")
    private String trustedIps;

    public IPBasedAnonymousAuthenticationFilter() {
        super("ANONYMOUS");
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {

        final ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.ROLE_UNTRUSTED_ANONYMOUS));

        if (ipWhiteListed(request.getRemoteAddr()) || forwardedForTrusted(request)) {
            authorities.add(new SimpleGrantedAuthority(Roles.ROLE_TRUSTED_ANONYMOUS));
        }
        return new AnonymousAuthenticationToken("ANONYMOUS", "ANONYMOUS", authorities);
    }

    private boolean forwardedForTrusted(HttpServletRequest request) {

        final String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(forwardedFor)) {
            return false;
        }
        for (String forwardedForEntry : forwardedFor.split(",")) {
            if (ipWhiteListed(forwardedForEntry.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean ipWhiteListed(String ip) {

        if ("*".equals(trustedIps)) {
            return true;
        }
        for (String trustedPattern : trustedIps.split(",")) {
            if (ipMatches(ip, trustedPattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean ipMatches(String ip, String trustedPattern) {

        String[] ipSegments = ip.split("\\.");
        String[] trustedSegments = trustedPattern.split("\\.");

        if (ipSegments.length != 4 || trustedSegments.length != 4) {
            LOG.error("invalid segment length for either ip: {} or trusted pattern: {}", ip, trustedPattern);
            return false;
        }

        return "*".equals(trustedSegments[0]) || ipSegments[0].equals(trustedSegments[0])
                && "*".equals(trustedSegments[1]) || ipSegments[1].equals(trustedSegments[1])
                && "*".equals(trustedSegments[2]) || ipSegments[2].equals(trustedSegments[2])
                && "*".equals(trustedSegments[3]) || ipSegments[3].equals(trustedSegments[3]);
    }
}
