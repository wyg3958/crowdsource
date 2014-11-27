package de.axelspringer.ideas.crowdsource.testsupport.cucumber.stepdefinitions;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.Registration;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.RegistrationConfirmation;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;


@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class RegistrationStepDefs {

    @Autowired
    private WebDriverProvider webDriverProvider;


    @Autowired
    private UrlProvider urlProvider;

    private WebDriver webDriver;

    private String emailName;

    @Before
    public void init(){
        webDriver = webDriverProvider.provideDriver();
        emailName = "registrationTest" + RandomStringUtils.random(5);
    }

    @After
    public void shutdown(){
        webDriver.close();
    }

    @Given("^a user is on the registration page$")
    public void a_user_visits_the_registration_page() throws Throwable {
        webDriver.get(urlProvider.applicationUrl());
        final NavigationBar navigationBar = PageFactory.initElements(webDriver, NavigationBar.class);
        navigationBar.clickSignup();
    }


    @When("^the user enters a not registered email address$")
    public void the_user_enters_a_not_registered_email_address() throws Throwable {
        final Registration registration = PageFactory.initElements(webDriver, Registration.class);
        registration.setEmailText(emailName);
    }

    @And("^the user accepts the terms of service$")
    public void the_user_accepts_the_terms_of_service() throws Throwable {
        final Registration registration = PageFactory.initElements(webDriver, Registration.class);
        registration.checkAcceptTosCheckbox();
    }

    @And("^submits the registration form$")
    public void submits_the_registration_form() throws Throwable {
        final Registration registration = PageFactory.initElements(webDriver, Registration.class);
        registration.submit();
    }

    @Then("^a registration success message is shown that includes the user's email$")
    public void a_registration_success_message_is_shown_that_includes_the_user_s_email() throws Throwable {
        final RegistrationConfirmation confirmation = PageFactory.initElements(webDriver, RegistrationConfirmation.class);
        Assert.assertEquals("User email address not found in confirmation page.", emailName + "@axelspringer.de", confirmation.getConfirmationMessage());
    }


//    @And("^submits the registration form with a new email address$")
//    public void submits_the_registration_form_with_a_new_email_address() throws Throwable {
//
//        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(urlProvider.applicationUrl());
//        uriBuilder.path("/user");
//
//        final User request = new User();
//        request.setEmail(emailAddress);
//        request.setTermsOfServiceAccepted(true);
//
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.postForObject(uriBuilder.build().toUriString(), request, Void.class);
//    }
//
//
//    private String generateUniqueEmailAddress() {
//
//        Date now = new Date();
//        return "cucumbertest+" + now.getTime() + "@axelspringer.de";
//    }
//

}
