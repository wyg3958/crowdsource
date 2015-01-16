package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.ActivationForm;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.LoginForm;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.RegistrationConfirmationView;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.RegistrationForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.MailServerClient;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmailValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

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
    private ActivationForm activationForm;

    @Autowired
    private RegistrationConfirmationView registrationConfirmationView;

    @Autowired
    private LoginForm loginForm;

    @Autowired
    private MailServerClient mailServerClient;

    private WebDriver webDriver;

    private String emailName;

    private String activationLink;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();

        emailName = "registrationTest+" + RandomStringUtils.randomAlphanumeric(10);
    }

    @Before("@ClearMailServer")
    public void initMailServer() {
        mailServerClient.clearMails();
    }

    @Given("^the user's email address is already registered but not activated$")
    public void the_user_s_email_address_is_already_registered_but_not_activated() throws Throwable {
        // create a user via the REST API
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail(emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN);
        userRegistration.setTermsOfServiceAccepted(true);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(urlProvider.applicationUrl() + "/user", userRegistration, Void.class);
    }

    @Given("^the user's email address is already registered and activated$")
    public void the_user_s_email_address_is_already_registered_and_activated() throws Throwable {
        // TODO: Replace this once we can activate accounts via the REST interface
        emailName = "crowdsource";
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
        registrationForm.setEmailText(emailName);
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
        PageFactory.initElements(webDriver, registrationConfirmationView);
        assertEquals("User email address not found in confirmation page.", emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN, registrationConfirmationView.getConfirmedEmailAddress());
    }

    @Then("^the validation error '([^']+)' is displayed on the email field$")
    public void the_validation_error_is_displayed_on_the_email_field(String errorText) throws Throwable {
        PageFactory.initElements(webDriver, registrationForm);
        assertThat(registrationForm.getEmailFieldErrorText(), is(errorText));
    }

    @Then("^the user receives (\\d+) activation mails$")
    public void the_user_receives_activation_mail_s(int mailCount) throws Throwable {

        // wait for mails to be available in mail server
        mailServerClient.waitForMails(mailCount, 5000);

        assertThat(mailServerClient.messages(), hasSize(mailCount));
        // check last received mail
        final MailServerClient.Message message = mailServerClient.messages().get(mailCount - 1);
        assertThat(message.from, is(UserActivationService.FROM_ADDRESS));
        assertThat(message.to, is(emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN));
        assertThat(message.subject, is(UserActivationService.REGISTRATION_SUBJECT));
        assertThat(message.message, startsWith(UserActivationService.MAIL_CONTENT));
    }

    @When("^the user clicks the email's activation link$")
    public void the_user_clicks_the_email_s_activation_link() throws Throwable {

        mailServerClient.waitForMails(1, 5000);

        final MailServerClient.Message message = mailServerClient.messages().get(0);
        String messageContent = message.message;
        String link = messageContent.substring(messageContent.indexOf("http")).trim();
        activationLink = link;
        webDriver.get(link);
    }

    @When("^the user changes the activation token in the URL$")
    public void the_user_changes_the_activation_token_in_the_URL() throws Throwable {
        activationLink = activationLink + "somethingToMakeTheActivationTokenInvalid";
        webDriver.get(activationLink);
    }

    @And("^the user enters a valid password twice on activation page$")
    public void the_user_enters_a_valid_password_twice_on_activation_page() throws Throwable {
        PageFactory.initElements(webDriver, activationForm);
        final String mySecretPassword = "1234567!";
        activationForm.setPasswordText(mySecretPassword);
        activationForm.setRepeatPasswordText(mySecretPassword);
    }

    @And("^the user submits the activation form$")
    public void the_user_submits_the_activation_form() throws Throwable {
        PageFactory.initElements(webDriver, activationForm);
        activationForm.submit();
    }

    @Given("^the user's email address is already activated$")
    public void the_user_s_email_address_is_already_activated() throws Throwable {
        the_user_s_email_address_is_already_registered_but_not_activated();
        the_user_clicks_the_email_s_activation_link();
        the_user_enters_a_valid_password_twice_on_activation_page();
        the_user_submits_the_activation_form();
    }

    @When("^the user clicks the email's activation link for the second time$")
    public void the_user_clicks_the_email_s_activation_link_for_the_second_time() throws Throwable {
        webDriver.get(activationLink);
    }

    @Then("^the validation error '([^']+)' is displayed$")
    public void the_validation_error_is_displayed(String errorText) throws Throwable {
        PageFactory.initElements(webDriver, activationForm);
        assertThat(activationForm.getErrorText(), containsString(errorText));
    }

    @And("^he tries to log in with the email address he used in the registration and an imaginary password$")
    public void he_tries_to_log_in_with_the_email_address_he_used_in_the_registration_and_an_imaginary_password() throws Throwable {
        PageFactory.initElements(webDriver, loginForm);

        loginForm.waitForPageLoad();
        loginForm.login(emailName, "xxx");
    }
}
