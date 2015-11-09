package de.asideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.asideas.crowdsource.model.presentation.FinancingRound;
import de.asideas.crowdsource.model.presentation.Pledge;
import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.pageobjects.project.ProjectPledgingForm;
import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.asideas.crowdsource.testsupport.util.CrowdSourceClient;
import org.joda.time.DateTime;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
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

    @Autowired
    private ProjectDetailSteps projectDetailSteps;

    @Autowired
    private SeleniumWait wait;

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
        pledgingForm.waitUntilANotificationOrEerrorMessageIsDisplayed();
        assertThat(pledgingForm.getNotificationMessage(), is(expectedNotification));
    }

    @Then("^the error message \"([^\"]*)\" is displayed on the project pledging form$")
    public void the_error_message_is_displayed_on_the_project_pledging_form(String expectedErrorMessage) throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);
        assertThat(pledgingForm.getErrorMessage(), is(expectedErrorMessage));
    }

    @And("^the project pledging form is (enabled|disabled)$")
    public void the_project_pledging_form_is_disabled(String enabledString) throws Throwable {
        boolean enabled = "enabled".equals(enabledString);

        assertThat(pledgingForm.isSliderEnabled(), is(enabled));
        assertThat(pledgingForm.getAmountInputField().isEnabled(), is(enabled));
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

    @When("^the user raises his desired pledge amount via the slider$")
    public void the_user_raises_desired_pledge_amount_via_the_slider() throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);

        keepAmountsBeforeChange();
        int amountBeforeChange = pledgingForm.getAmountFromInputField();

        pledgingForm.moveSliderBy(500); //pixels

        // Timing issue; After setting the slider it takes some millis until the input field is updated
        wait.until(d -> pledgingForm.getAmountFromInputField() > 0);
        int amountFromInputField = pledgingForm.getAmountFromInputField();
        assertThat(amountBeforeChange, is(lessThan(amountFromInputField)));

        pledgeAmount = amountFromInputField;
    }

    @When("^the user reduces his desired pledge amount via the slider$")
    public void the_user_reduces_desired_pledge_amount_via_the_slider() throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);

        keepAmountsBeforeChange();
        int amountBeforeChange = pledgingForm.getAmountFromInputField();

        pledgingForm.moveSliderBy(-50); //pixels

        int amountFromInputField = pledgingForm.getAmountFromInputField();
        assertThat(amountBeforeChange, is(greaterThan(amountFromInputField)));

        pledgeAmount = pledgingForm.getAmountFromInputField() - amountBeforeChange;
    }

    @When("^the user sets his desired pledge amount as high as the remaining amount of the project goal$")
    public void the_user_sets_his_desired_pledge_amount_as_high_as_the_remaining_amount_of_the_project_goal() throws Throwable {
        PageFactory.initElements(webDriver, pledgingForm);

        keepAmountsBeforeChange();
        pledgeAmount = pledgingForm.getPledgeGoalAmount() - pledgedAmountBeforeChange;

        if (pledgeAmount > budgetBeforeChange) {
            // if this happens, then the financing round was started with a too low budget
            throw new IllegalStateException("User budget (" + budgetBeforeChange + ") is too low to fully pledge the project");
        }

        pledgingForm.setAmountInputValue(pledgeAmount);
    }

    @When("^the user enters (\\d+) as his desired pledge amount$")
    public void the_user_enters_as_his_desired_pledge_amount(int pledgeAmount) throws Throwable {
        keepAmountsBeforeChange();

        PageFactory.initElements(webDriver, pledgingForm);
        pledgingForm.setAmountInputValue(pledgeAmount);

        this.pledgeAmount = pledgingForm.getPledgedAmount() - pledgedAmountBeforeChange;
    }

    @Then("^the displayed budget and financing infos are updated$")
    public void the_displayed_budget_and_financing_infos_are_updated() throws Throwable {
        final int expectedUsrBudget = budgetBeforeChange - pledgeAmount;
        assertThat(pledgingForm.getUserBudget(), is(expectedUsrBudget));
        assertThat(pledgingForm.getPledgedAmount(), is(pledgedAmountBeforeChange + pledgeAmount));
    }

    @When("^the user submits the pledging form$")
    public void the_user_submits_the_pledging_form() throws Throwable {
        pledgingForm.submitForm();
        pledgingForm.waitUntilANotificationOrEerrorMessageIsDisplayed();
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

    @When("^a financing round is being deactivated in the meantime$")
    public void a_financing_round_is_being_deactivated_in_the_meantime() throws Throwable {
        prepareFinancingRound(false);
    }

    @And("^another user pledges the same project with (\\d+) in the meantime$")
    public void another_user_pledges_the_project_with_in_the_meantime(int pledgeAmount) throws Throwable {
        pledgeProjectViaApi(pledgeAmount, true);
    }

    @And("^the project is pledged with and amount of (\\d+)$")
    public void the_project_is_pledged_with_and_amount_of(int amount) throws Throwable {
        the_user_enters_as_his_desired_pledge_amount(amount);
        the_user_submits_the_pledging_form();
        the_notification_message_is_displayed_on_the_project_pledging_form("Deine Finanzierung war erfolgreich.");
    }

    private void prepareFinancingRound(boolean requireActiveFinancingRound) {
        CrowdSourceClient.AuthToken authToken = crowdSourceClient.authorizeWithAdminUser();

        FinancingRound activeFinanceRound = crowdSourceClient.getActiveFinanceRound();
        if (activeFinanceRound != null) {
            crowdSourceClient.stopFinancingRound(activeFinanceRound.getId(), authToken);
        }

        if (requireActiveFinancingRound) {
            FinancingRound financingRound = new FinancingRound();
            financingRound.setEndDate(DateTime.now().plusDays(5));
            financingRound.setBudget(100000);
            crowdSourceClient.startFinancingRound(financingRound, authToken);
        }
    }

    private void pledgeProjectViaApi(int pledgeAmount, boolean asAdmin) {
        CrowdSourceClient.AuthToken authToken =
                asAdmin ?
                crowdSourceClient.authorizeWithAdminUser() :
                crowdSourceClient.authorizeWithDefaultUser();

        Pledge pledge = new Pledge(pledgeAmount);
        crowdSourceClient.pledgeProject(projectDetailSteps.getCreatedProject(), pledge, authToken);
    }

    private void keepAmountsBeforeChange() {
        budgetBeforeChange = pledgingForm.getUserBudget();
        pledgedAmountBeforeChange = pledgingForm.getPledgedAmount();
    }
}
