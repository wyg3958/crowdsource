package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MailConfig;
import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.config.security.SecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
@Import({MongoDBConfig.class, SecurityConfig.class, MailConfig.class})
@EnableAutoConfiguration
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
public class CrowdSource {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(CrowdSource.class, args);
    }

    @PostConstruct
    public void printActiveProfiles() {

        final String[] activeProfiles = environment.getActiveProfiles();
        log.info("Starting CrowdSource");
        log.info("Active profiles: {}", StringUtils.join(activeProfiles, ", "));
    }
}
