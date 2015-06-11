package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.config.mail.MailSenderConfig;
import de.axelspringer.ideas.crowdsource.config.mail.MailTemplateConfig;
import de.axelspringer.ideas.crowdsource.config.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import({MongoDBConfig.class, SecurityConfig.class, MailSenderConfig.class, MailTemplateConfig.class})
@ComponentScan(basePackages = "de.axelspringer.ideas.crowdsource", excludeFilters = @ComponentScan.Filter(Configuration.class))
public class CrowdSource extends WebMvcConfigurerAdapter {

    @Autowired(required = false)
    private List<HandlerInterceptorAdapter> handlerInterceptorAdapters = new ArrayList<>();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        handlerInterceptorAdapters.forEach(registry::addInterceptor);
        super.addInterceptors(registry);
    }
}
