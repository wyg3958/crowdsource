package de.axelspringer.ideas.crowdsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
public class CrowdSourceConfig {

    public static void main(String[] args) {
        SpringApplication.run(CrowdSourceConfig.class, args);
    }
}
