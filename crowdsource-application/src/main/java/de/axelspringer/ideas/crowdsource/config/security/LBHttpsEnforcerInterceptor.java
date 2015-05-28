package de.axelspringer.ideas.crowdsource.config.security;

import de.axelspringer.ideas.crowdsource.config.AppProfiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ensures https by redirecting http requests to https://{@link #applicationUrl}
 */
@Slf4j
@Component
@Profile(AppProfiles.NOT + AppProfiles.ALLOW_HTTP)
public class LBHttpsEnforcerInterceptor extends HandlerInterceptorAdapter {

    public static final String X_FORWARDED_PROTO_HEADER = "X-FORWARDED-PROTO";

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String applicationUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String forwardedHeader = request.getHeader(X_FORWARDED_PROTO_HEADER);
        if (StringUtils.isBlank(forwardedHeader) || !"HTTPS".equalsIgnoreCase(forwardedHeader)) {
            log.debug("redirecting non-https request with header: {}", forwardedHeader);
            response.sendRedirect(applicationUrl);
            return false;
        }
        return true;
    }
}
