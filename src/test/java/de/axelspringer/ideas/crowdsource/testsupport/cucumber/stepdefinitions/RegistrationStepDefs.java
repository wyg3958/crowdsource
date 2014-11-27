package de.axelspringer.ideas.crowdsource.testsupport.cucumber.stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import de.axelspringer.ideas.crowdsource.model.presentation.User;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class RegistrationStepDefs {

    @Autowired
    private UrlProvider urlProvider;

    private String emailAddress;

    @Given("^a user is on the registration page$")
    public void a_user_visits_the_registration_page() throws Throwable {

        this.emailAddress = generateUniqueEmailAddress();
    }

    @And("^submits the registration form with a new email address$")
    public void submits_the_registration_form_with_a_new_email_address() throws Throwable {

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(urlProvider.applicationUrl());
        uriBuilder.path("/user");

        final User request = new User();
        request.setEmail(emailAddress);
        request.setTermsOfServiceAccepted(true);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(uriBuilder.build().toUriString(), request, Void.class);
    }


    private String generateUniqueEmailAddress() {

        Date now = new Date();
        return "cucumbertest+" + now.getTime() + "@axelspringer.de";
    }
}
