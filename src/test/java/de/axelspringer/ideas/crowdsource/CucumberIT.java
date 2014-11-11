package de.axelspringer.ideas.crowdsource;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.Properties;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"de.axelspringer.ideas.crowdsource.testsupport"},
        features = "classpath:features",
        strict = true,
        format = {"pretty", "html:target/cucumber", "json:target/cucumber/cucumber.json"})
public class CucumberIT {

    public static WebDriver DRIVER;

    public static String APP_URL;

    @BeforeClass
    public static void init() throws IOException {
        // load properties
        Properties properties = new Properties();

        properties.load(CucumberIT.class.getResourceAsStream("/de/axelspringer/ideas/crowdsource/test.properties"));
        String phantomJsBinaryPath = properties.getProperty("de.axelspringer.ideas.crowdsource.test.phantomjs.binary");
        String chromeBinaryPath = properties.getProperty("de.axelspringer.ideas.crowdsource.test.chrome.binary");

        final String dockerHost = System.getProperty("dockerhost");
        final String serverPort = properties.getProperty("de.axelspringer.ideas.crowdsource.test.server.port");

        if (StringUtils.isNoneEmpty(dockerHost)) {
            APP_URL = "http://" + dockerHost + ":" + serverPort;
            System.out.println("Using external docker host: " + dockerHost);
        } else {
            APP_URL = "http://127.0.0.1:" + serverPort;
            System.out.println("Using default docker host.");
        }

        DRIVER = WebDriverUtils.provideDriver(phantomJsBinaryPath, chromeBinaryPath);
    }

    @AfterClass
    public static void tearDown() {
        WebDriverUtils.closeWebDriver(DRIVER);
    }


}
