package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MailConfig;
import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.config.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({MongoDBConfig.class, SecurityConfig.class, MailConfig.class})
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
public class CrowdSource {

    public static void main(String[] args) {
        SpringApplication.run(CrowdSource.class, args);
    }
}
