package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectPledgingForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ProjectPledgingSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private ProjectPledgingForm pledgingForm;

    private RemoteWebDriver webDriver;


    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }

    @Then("^the notification message \"([^\"]*)\" is displayed on the project pledging form$")
    public void the_notification_message_is_displayed_on_the_project_pledging_form(String expectedNotification) {
        PageFactory.initElements(webDriver, pledgingForm);
        assertThat(pledgingForm.getNotificationMessage(), is(expectedNotification));
    }

    @And("^the project pledging form is disabled$")
    public void the_project_pledging_form_is_disabled() throws Throwable {
        assertThat(pledgingForm.isSliderEnabled(), is(false));
        assertThat(pledgingForm.getAmountInputField().isEnabled(), is(false));
        assertThat(pledgingForm.getPledgingButton().isEnabled(), is(false));
    }

    @And("^the user budget \"([^\"]*)\" is displayed$")
    public void the_user_budget_is_displayed(String budget) throws Throwable {
        assertThat(pledgingForm.getUserBudget(), is(budget));
    }
}
