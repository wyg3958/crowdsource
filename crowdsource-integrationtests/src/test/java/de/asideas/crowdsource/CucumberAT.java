package de.asideas.crowdsource;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"de.asideas.crowdsource.testsupport"},
        features = "classpath:features",
        strict = true,
        format = {"pretty", "html:target/cucumber", "json:target/cucumber/cucumber.json"})
public class CucumberAT {

    private static ConfigurableApplicationContext EXAMPLE_APP;

    @BeforeClass
    public static void initApp() {
        EXAMPLE_APP = SpringApplication.run(CrowdSourceExample.class);
    }

    @AfterClass
    public static void closeApp() {
        if (EXAMPLE_APP != null) {
            EXAMPLE_APP.stop();
        }
    }

    @After
    public static void tearDown() {
        WebDriverProvider.closeWebDriver();
    }
}
