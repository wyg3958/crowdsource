package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.AppProfiles;
import de.axelspringer.ideas.crowdsource.config.MailConfig;
import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.config.security.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SpringBootApplication
@Import({MongoDBConfig.class, SecurityConfig.class, MailConfig.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
@Slf4j
public class CrowdSource extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String applicationUrl;

    public static void main(String[] args) {
        SpringApplication.run(CrowdSource.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        if (!environment.acceptsProfiles(AppProfiles.CONS, AppProfiles.DEV)) {
            registry.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    final String forwardedHeader = request.getHeader("X-FORWARDED-PROTO");
                    if (StringUtils.isBlank(forwardedHeader) || !"HTTPS".equalsIgnoreCase(forwardedHeader)) {
                        log.warn("redirecting non-https request with header: {}", forwardedHeader);
                        response.sendRedirect(applicationUrl);
                        return false;
                    }
                    return true;
                }
            });
        }
        super.addInterceptors(registry);
    }
}
