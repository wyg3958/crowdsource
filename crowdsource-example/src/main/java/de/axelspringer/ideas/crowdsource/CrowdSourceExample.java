package de.axelspringer.ideas.crowdsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CrowdSource.class)
public class CrowdSourceExample {

    public static void main(String[] args) {
        SpringApplication.run(CrowdSourceExample.class, args);
    }
}
