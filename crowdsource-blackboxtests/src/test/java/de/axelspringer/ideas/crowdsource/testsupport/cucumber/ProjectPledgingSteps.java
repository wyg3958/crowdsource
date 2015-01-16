package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectPledgingForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.CrowdSourceClient;
import org.joda.time.DateTime;
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

    @Autowired
    private CrowdSourceClient crowdSourceClient;

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

    @And("^there is (a|no) financing round active$")
    public void there_is_a_financing_round_active(String active) throws Throwable {
        boolean requireActiveFinancingRound = "a".equals(active);

        CrowdSourceClient.AuthToken authToken = crowdSourceClient.authorizeWithAdminUser();

        FinancingRound activeFinanceRound = crowdSourceClient.getActiveFinanceRound();
        if (activeFinanceRound != null) {
            crowdSourceClient.stopFinancingRound(activeFinanceRound.getId(), authToken);
        }

        if (requireActiveFinancingRound) {
            FinancingRound financingRound = new FinancingRound();
            financingRound.setEndDate(DateTime.now().plusDays(10));
            financingRound.setBudget(100000);
            crowdSourceClient.startFinancingRound(financingRound, authToken);
        }
    }

    @When("^a financing round is being activated in the meantime$")
    public void a_financing_round_is_being_activated_in_the_meantime() throws Throwable {
        there_is_a_financing_round_active("a");
    }
}
