package de.axelspringer.ideas.crowdsource;

import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.IndexPage;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@Ignore("hello view was removed. Once we have a proper index page, adapt this test")
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

    @After
    public void closeDriver() {
        WebDriverProvider.closeWebDriver();
    }

    @Test
    public void testIndexPage() {
        
        webDriver.get(urlProvider.applicationUrl() + "/index.html");
        final IndexPage indexPage = PageFactory.initElements(webDriver, IndexPage.class);
        assertEquals("AS CrowdSource says hi", indexPage.helloText());
    }
}
