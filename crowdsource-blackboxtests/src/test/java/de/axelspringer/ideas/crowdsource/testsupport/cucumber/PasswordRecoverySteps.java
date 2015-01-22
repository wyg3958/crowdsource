package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.ConfirmationView;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.PasswordRecoveryForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class PasswordRecoverySteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private PasswordRecoveryForm passwordRecoveryForm;

    @Autowired
    private ConfirmationView confirmationView;

    @Autowired
    private ActivationSteps activationSteps;

    private WebDriver webDriver;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }

    @Given("^the user is on the password recovery page$")
    public void the_user_is_on_the_password_recovery_page() throws Throwable {
        passwordRecoveryForm.open();
    }

    @Then("^the password recovery form is displayed$")
    public void the_password_recovery_form_is_displayed() {
        passwordRecoveryForm.waitForPageLoad();
    }

    @When("^the user enters his email address in the password recovery form$")
    public void the_user_enters_his_email_address_in_the_password_recovery_form() throws Throwable {
        PageFactory.initElements(webDriver, passwordRecoveryForm);
        passwordRecoveryForm.setEmailText(activationSteps.getGeneratedEmailName());
    }

    @And("^submits the password recovery form$")
    public void submits_the_password_recovery_form() throws Throwable {
        PageFactory.initElements(webDriver, passwordRecoveryForm);
        passwordRecoveryForm.submitForm();
    }

    @Then("^a password recovery success message is shown that includes the user's email$")
    public void a_password_recovery_success_message_is_shown_that_includes_the_user_s_email() throws Throwable {
        confirmationView.waitForPageLoad();

        PageFactory.initElements(webDriver, confirmationView);
        assertThat(confirmationView.getHeadline(), is("Passwort vergessen"));
        assertThat(confirmationView.getConfirmedEmailAddress(), is(activationSteps.getGeneratedEmail()));
    }

    @And("^the user requests a password recovery$")
    public void the_user_requests_a_password_recovery() throws Throwable {
        the_user_is_on_the_password_recovery_page();
        the_user_enters_his_email_address_in_the_password_recovery_form();
        submits_the_password_recovery_form();
    }
}
