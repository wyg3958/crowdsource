package de.asideas.crowdsource;

import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.asideas.crowdsource.testsupport.util.UrlProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest
@SpringApplicationConfiguration(classes = {CrowdSourceExample.class, CrowdSourceTestConfig.class})
public class IndexIT {

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private SeleniumWait wait;

    private WebDriver webDriver;

    @Before
    public void initDriver() {
        webDriver = webDriverProvider.provideDriver();
    }

    @After
    public void closeDriver() {
        WebDriverProvider.closeWebDriver();
    }

    @Test
    public void testIndexPage() {
        webDriver.get(urlProvider.applicationUrl() + "/");
        wait.until(driver -> "CrowdSource - Projekte".equals(driver.getTitle()));
    }
}
