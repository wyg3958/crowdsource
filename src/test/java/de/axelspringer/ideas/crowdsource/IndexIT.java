package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.IndexPage;
import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrowdSourceTestConfig.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class IndexIT {

    // local tomcat port, will be injected by spring after starting the webapp
    @Value("${local.server.port}")
    private String serverPort;

    @Autowired
    private WebDriverUtils webDriverUtils;

    private WebDriver webDriver;

    @Before
    public void initDriver() {
        webDriver = webDriverUtils.provideDriver();
    }

    @After
    public void closeDriver() {
        webDriver.quit();
    }

    @Test
    public void testIndexPage() {
        webDriver.get("http://localhost:" + serverPort);
        final IndexPage indexPage = PageFactory.initElements(webDriver, IndexPage.class);
        assertEquals("AS CrowdSource", indexPage.exampleElementText());
    }
}
