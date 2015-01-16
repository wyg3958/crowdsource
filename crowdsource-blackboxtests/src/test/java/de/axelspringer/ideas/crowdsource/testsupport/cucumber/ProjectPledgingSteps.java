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
import static org.hamcrest.Matchers.lessThan;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ProjectPledgingSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private ProjectPledgingForm pledgingForm;

    @Autowired
    private CrowdSourceClient crowdSourceClient;

    private RemoteWebDriver webDriver;
    private int budgetBeforeChange;
    private int pledgedAmountBeforeChange;
    private int pledgeAmount;


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

    @And("^the user budget (\\d+) is displayed$")
    public void the_user_budget_is_displayed(int budget) throws Throwable {
        assertThat(pledgingForm.getUserBudget(), is(budget));
    }

    @Then("^there is no notification message$")
    public void there_is_no_notification_message() throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);
        assertThat(pledgingForm.getNotificationMessage(), is(""));
    }

    @When("^the user sets his desired pledge amount via the slider$")
    public void the_user_sets_his_desired_pledge_amount_via_the_slider() throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);

        budgetBeforeChange = pledgingForm.getUserBudget();
        pledgedAmountBeforeChange = pledgingForm.getPledgedAmount();
        int amountBeforeChange = pledgingForm.getAmountFromInputField();

        pledgingForm.moveSliderBy(500); //pixels

        int amountFromInputField = pledgingForm.getAmountFromInputField();
        assertThat(amountBeforeChange, is(lessThan(amountFromInputField)));

        pledgeAmount = amountFromInputField;
    }

    @When("^the user sets his desired pledge amount as high as the remaining amount of the project goal$")
    public void the_user_sets_his_desired_pledge_amount_as_high_as_the_remaining_amount_of_the_project_goal() throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);

        budgetBeforeChange = pledgingForm.getUserBudget();
        pledgedAmountBeforeChange = pledgingForm.getPledgedAmount();

        pledgeAmount = pledgingForm.getPledgeGoalAmount() - pledgedAmountBeforeChange;
        if (pledgeAmount > budgetBeforeChange) {
            // if this happens, then the financing round was started with a too low budget
            throw new IllegalStateException("User budget (" + budgetBeforeChange + ") is too low to fully pledge the project");
        }

        pledgingForm.setAmountInputValue(pledgeAmount);
    }

    @Then("^the displayed budget and financing infos are updated$")
    public void the_displayed_budget_and_financing_infos_are_updated() throws Throwable {
        assertThat(pledgingForm.getUserBudget(), is(budgetBeforeChange - pledgeAmount));
        assertThat(pledgingForm.getPledgedAmount(), is(pledgedAmountBeforeChange + pledgeAmount));
    }

    @When("^the user submits the pledging form$")
    public void the_user_submits_the_pledging_form() throws Throwable {
        pledgingForm.submitForm();
        pledgingForm.waitUntilANotificationMessageIsDisplayed();
    }

    @And("^there is (a|no) financing round active$")
    public void there_is_a_financing_round_active(String active) throws Throwable {
        boolean requireActiveFinancingRound = "a".equals(active);
        prepareFinancingRound(requireActiveFinancingRound);
    }

    @When("^a financing round is being activated in the meantime$")
    public void a_financing_round_is_being_activated_in_the_meantime() throws Throwable {
        prepareFinancingRound(true);
    }

    private void prepareFinancingRound(boolean requireActiveFinancingRound) {
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
}
