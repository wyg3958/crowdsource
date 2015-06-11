package de.axelspringer.ideas.crowdsource.testsupport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
@PropertySources({
        @PropertySource(value = "classpath:application.properties"),
})
public class CrowdSourceTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(Boolean.FALSE);
        propertySourcesPlaceholderConfigurer.setLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:*.properties"));
        return propertySourcesPlaceholderConfigurer;
    }
}
