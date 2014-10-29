package de.axelspringer.ideas.crowdsource.testsupport;

import de.axelspringer.ideas.crowdsource.CrowdSourceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan
@Import(CrowdSourceConfig.class)
@PropertySource("/de/axelspringer/ideas/crowdsource/test.properties")
public class CrowdSourceTestConfig {
}
