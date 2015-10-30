package de.asideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.pageobjects.ConfirmationView;
import de.asideas.crowdsource.testsupport.pageobjects.LoginForm;
import de.asideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.asideas.crowdsource.testsupport.pageobjects.RegistrationForm;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.asideas.crowdsource.testsupport.util.CrowdSourceClient;
import de.asideas.crowdsource.testsupport.util.MailServerClient;
import de.asideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class RegistrationSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private NavigationBar navigationBar;

    @Autowired
    private RegistrationForm registrationForm;

    @Autowired
    private ConfirmationView confirmationView;

    @Autowired
    private LoginForm loginForm;

    @Autowired
    private ActivationSteps activationSteps;

    @Autowired
    private MailServerClient mailServerClient;

    @Autowired
    private CrowdSourceClient crowdSourceClient;

    private WebDriver webDriver;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }

    @Before("@ClearMailServer")
    public void initMailServer() {
        mailServerClient.clearMails();
    }

    @Given("^the user's email address is already registered but not activated$")
    public void the_user_s_email_address_is_already_registered_but_not_activated() throws Throwable {
        crowdSourceClient.registerUser(activationSteps.getGeneratedEmailName());
    }

    @Given("^a user is on the registration page$")
    public void a_user_is_on_the_registration_page() throws Throwable {
        webDriver.get(urlProvider.applicationUrl());

        PageFactory.initElements(webDriver, navigationBar);
        navigationBar.clickSignup();

        registrationForm.waitForPageLoad();
    }

    @When("^the user enters his email address$")
    public void the_user_enters_a_not_registered_email_address() throws Throwable {
        PageFactory.initElements(webDriver, registrationForm);
        registrationForm.setEmailText(activationSteps.getGeneratedEmailName());
    }

    @When("^the user enters his email address in different case$")
    public void the_user_enters_his_email_address_in_different_case() throws Throwable {
        PageFactory.initElements(webDriver, registrationForm);

        final char[] chars = activationSteps.getGeneratedEmailName().toCharArray();
        // invert case of first letter
        chars[0] = Character.isUpperCase(chars[0]) ? Character.toLowerCase(chars[0]) : Character.toUpperCase(chars[0]);
        registrationForm.setEmailText(new String(chars));
    }

    @And("^the user accepts the terms of service$")
    public void the_user_accepts_the_terms_of_service() throws Throwable {
        PageFactory.initElements(webDriver, registrationForm);
        registrationForm.checkAcceptTosCheckbox();
    }

    @And("^submits the registration form$")
    public void submits_the_registration_form() throws Throwable {
        PageFactory.initElements(webDriver, registrationForm);
        registrationForm.submit();
    }

    @Then("^a registration success message is shown that includes the user's email$")
    public void a_registration_success_message_is_shown_that_includes_the_user_s_email() throws Throwable {
        PageFactory.initElements(webDriver, confirmationView);
        assertThat(confirmationView.getHeadline(), is("Registrierung erfolgreich"));
        assertThat(confirmationView.getConfirmedEmailAddress(), is(equalToIgnoringCase(activationSteps.getGeneratedEmail())));
    }

    @Then("^the validation error '([^']+)' is displayed on the email field$")
    public void the_validation_error_is_displayed_on_the_email_field(String errorText) throws Throwable {
        PageFactory.initElements(webDriver, registrationForm);
        assertThat(registrationForm.getEmailFieldErrorText(), is(errorText));
    }

    @And("^he tries to log in with the email address he used in the registration and an imaginary password$")
    public void he_tries_to_log_in_with_the_email_address_he_used_in_the_registration_and_an_imaginary_password() throws Throwable {
        PageFactory.initElements(webDriver, loginForm);

        loginForm.waitForPageLoad();
        loginForm.login(activationSteps.getGeneratedEmailName(), "xxx");
    }
}
