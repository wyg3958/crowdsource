package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.IndexPage;
import de.axelspringer.ideas.crowdsource.testsupport.util.HostUtils;
import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class IndexIT {

    @Value("${de.axelspringer.ideas.crowdsource.test.server.port}")
    private String serverPort;

    @Value("${de.axelspringer.ideas.crowdsource.test.phantomjs.binary}")
    private String phantomBinaryPath;

    @Value("${de.axelspringer.ideas.crowdsource.test.chrome.binary}")
    private String chromeBinaryPath;

    private WebDriver webDriver;

    @Before
    public void initDriver() {
        webDriver = WebDriverUtils.provideDriver(phantomBinaryPath, chromeBinaryPath);
    }

    @After
    public void closeDriver() {
        WebDriverUtils.closeWebDriver(webDriver);
    }

    @Test
    public void testIndexPage() {
        final String applicationHost = HostUtils.getApplicationHost();

        webDriver.get("http://" + applicationHost + ":" + serverPort + "/index.html");
        final IndexPage indexPage = PageFactory.initElements(webDriver, IndexPage.class);
        assertEquals("AS CrowdSource says hi", indexPage.helloText());
    }
}
