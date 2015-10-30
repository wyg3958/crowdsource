package de.asideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.Then;
import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.pageobjects.ErrorPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ErrorSteps {

    @Autowired
    private ErrorPage errorPage;

    @Then("^the Not Found error page is displayed$")
    public void the_Not_Found_error_page_is_displayed() {
        errorPage.waitForNotFoundPageLoad();
    }
}
