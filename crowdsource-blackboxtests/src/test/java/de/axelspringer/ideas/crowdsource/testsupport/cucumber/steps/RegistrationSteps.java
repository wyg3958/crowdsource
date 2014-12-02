package de.axelspringer.ideas.crowdsource.testsupport.cucumber.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.User;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.RegistrationConfirmationView;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.RegistrationForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmailValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
    private RegistrationConfirmationView registrationConfirmationView;

    private WebDriver webDriver;

    private String emailName;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
        emailName = "registrationTest+" + RandomStringUtils.randomAlphanumeric(10);
    }

    @Given("^the user's email address is already registered but not activated$")
    public void the_user_s_email_address_is_already_registered_but_not_activated() throws Throwable {
        // create a user via the REST API
        User user = new User();
        user.setEmail(emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN);
        user.setTermsOfServiceAccepted(true);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(urlProvider.applicationUrl() + "/user", user, Void.class);
    }

    @Given("^the user's email address is already registered and activated$")
    public void the_user_s_email_address_is_already_registered_and_activated() throws Throwable {
        // TODO: Replace this once we can activate accounts via the REST interface
        emailName = "crowdsource";
    }

    @Given("^a user is on the registration page$")
    public void a_user_visits_the_registration_page() throws Throwable {
        webDriver.get(urlProvider.applicationUrl());

        PageFactory.initElements(webDriver, navigationBar);
        navigationBar.clickSignup();
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
}
