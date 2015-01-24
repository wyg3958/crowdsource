package de.axelspringer.ideas.crowdsource.config.security;

import de.axelspringer.ideas.crowdsource.config.ProductionCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@Conditional(ProductionCondition.class)
public class LBHttpsEnforcerInterceptor extends HandlerInterceptorAdapter {

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String applicationUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String forwardedHeader = request.getHeader("X-FORWARDED-PROTO");
        if (StringUtils.isBlank(forwardedHeader) || !"HTTPS".equalsIgnoreCase(forwardedHeader)) {
            log.debug("redirecting non-https request with header: {}", forwardedHeader);
            response.sendRedirect(applicationUrl);
            return false;
        }
        return true;
    }
}
