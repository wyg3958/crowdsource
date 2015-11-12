package de.asideas.crowdsource;

import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.asideas.crowdsource.testsupport.util.UrlProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest
@SpringApplicationConfiguration(classes = {CrowdSourceExample.class, CrowdSourceTestConfig.class})
public class IndexIT {

    public static final String DEFAULT_TEST_PROPERTY_SOURCE = "testPropsSource";

    private MockPropertySource testPropertySource;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private SeleniumWait wait;

    private WebDriver webDriver;

    @Autowired
    ConfigurableEnvironment env;

    @Before
    public void initDriver() {
        webDriver = webDriverProvider.provideDriver();

        testPropertySource = new MockPropertySource(DEFAULT_TEST_PROPERTY_SOURCE);
        // Overlay properties, set by tests.
        if (env.getPropertySources().contains(DEFAULT_TEST_PROPERTY_SOURCE)) {
            env.getPropertySources().replace(DEFAULT_TEST_PROPERTY_SOURCE, testPropertySource);
        } else {
            env.getPropertySources().addFirst(testPropertySource);
        }
    }

    @After
    public void closeDriver() {
        WebDriverProvider.closeWebDriver();
    }

    @Test
    public void testIndexPage() {
        webDriver.get(urlProvider.applicationUrl() + "/");
        wait.until(driver -> "CrowdSource - Projekte".equals(driver.getTitle()));

        List<WebElement> trackingParentTag = webDriver.findElements(By.id("piwiktracking"));
        assertThat(trackingParentTag, hasSize(0));
    }

    @Test
    public void testIndexPageWithTrackingEnabled() {
        String expectedTrackingUrl = "http://trackingrulz.crowdsource.de";
        String expectedSiteId = "1234567890987654321";
        testPropertySource.withProperty("de.asideas.crowdsource.tracking.piwik.trackurl", expectedTrackingUrl)
                    .withProperty("de.asideas.crowdsource.tracking.piwik.siteid", expectedSiteId);

        webDriver.get(urlProvider.applicationUrl() + "/");
        wait.until(driver -> "CrowdSource - Projekte".equals(driver.getTitle()));

        List<WebElement> trackingParentTag = webDriver.findElements(By.id("piwiktracking"));

        assertThat(trackingParentTag, hasSize(1));
        assertThat(trackingParentTag.get(0).getTagName(), is("script"));

        String trackingJs = trackingParentTag.get(0).getAttribute("innerHTML");

        assertThat(trackingJs, containsString(expectedTrackingUrl));
        assertThat(trackingJs, containsString(expectedSiteId));
    }

    @Test
    public void testIndexPageWithJiraIssueCollectorEnabled() {
        String jicTestUrl = "http://jirarulz.crowdsource.de/";
        testPropertySource.withProperty("de.asideas.crowdsource.jira.issuecollector.url", jicTestUrl);

        webDriver.get(urlProvider.applicationUrl() + "/");
        wait.until(driver -> "CrowdSource - Projekte".equals(driver.getTitle()));

        List<WebElement> jicParentTag = webDriver.findElements(By.id("jiraissuecollector"));

        assertThat(jicParentTag, hasSize(1));
        assertThat(jicParentTag.get(0).getTagName(), is("script"));

        assertThat(jicParentTag.get(0).getAttribute("src"), is(jicTestUrl));
    }

}
