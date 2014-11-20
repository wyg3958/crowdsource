package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.config.MongoDBConfig;
import de.axelspringer.ideas.crowdsource.config.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MongoDBConfig.class, SecurityConfig.class})
@EnableAutoConfiguration
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
public class CrowdSourceConfig {

    public static void main(String[] args) {
        SpringApplication.run(CrowdSourceConfig.class, args);
    }
}
