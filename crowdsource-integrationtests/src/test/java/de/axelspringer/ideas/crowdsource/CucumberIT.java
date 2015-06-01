package de.axelspringer.ideas.crowdsource;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"de.axelspringer.ideas.crowdsource.testsupport"},
        features = "classpath:features",
        strict = true,
        format = {"pretty", "html:target/cucumber", "json:target/cucumber/cucumber.json"})
public class CucumberIT {

    private static ConfigurableApplicationContext EXAMPLE_APP;

    @BeforeClass
    public static void initApp() {
        EXAMPLE_APP = SpringApplication.run(CrowdSourceExample.class, "--spring.profiles.active=ALLOW_HTTP,CREATE_USERS");
    }

    @AfterClass
    public static void closeApp() {
        EXAMPLE_APP.stop();
    }

    @After
    public static void tearDown() {
        WebDriverProvider.closeWebDriver();
    }
}
