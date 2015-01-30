package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.config.mail.MailSenderConfig;
import de.axelspringer.ideas.crowdsource.config.mail.MailTemplateConfig;
import de.axelspringer.ideas.crowdsource.config.security.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Import({MongoDBConfig.class, SecurityConfig.class, MailSenderConfig.class, MailTemplateConfig.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
@Slf4j
public class CrowdSource extends WebMvcConfigurerAdapter {

    @Autowired(required = false)
    private List<HandlerInterceptorAdapter> handlerInterceptorAdapters = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(CrowdSource.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        handlerInterceptorAdapters.forEach(registry::addInterceptor);
        super.addInterceptors(registry);
    }
}
