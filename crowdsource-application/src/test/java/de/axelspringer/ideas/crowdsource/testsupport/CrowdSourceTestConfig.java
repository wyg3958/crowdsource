package de.axelspringer.ideas.crowdsource.testsupport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
@PropertySource("/de/axelspringer/ideas/crowdsource/test.properties")
public class CrowdSourceTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
