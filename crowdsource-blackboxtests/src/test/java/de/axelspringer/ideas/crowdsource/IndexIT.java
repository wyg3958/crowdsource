package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class IndexIT {

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private WebDriverProvider webDriverProvider;

    private WebDriver webDriver;

    @Before
    public void initDriver() {
        webDriver = webDriverProvider.provideDriver();
    }

    public void closeDriver() {
        WebDriverProvider.closeWebDriver();
    }

    @Test
    public void testIndexPage() {

        webDriver.get(urlProvider.applicationUrl() + "/index.html");
        assertEquals("AS CrowdSource", webDriver.getTitle());
    }
}
