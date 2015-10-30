package de.asideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.Then;
import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class AdminAreaSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Then("^the admin-section in the footer is not visible$")
    public void the_admin_section_in_the_footer_is_not_visible() throws Throwable {
        assertFalse(adminAreaVisible());
    }

    @Then("^the admin-section in the footer is visible$")
    public void the_admin_section_in_the_footer_is_visible() throws Throwable {
        assertTrue(adminAreaVisible());
    }

    private boolean adminAreaVisible() {
        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        return webDriver.findElementsByClassName("admin").size() > 0;
    }
}
