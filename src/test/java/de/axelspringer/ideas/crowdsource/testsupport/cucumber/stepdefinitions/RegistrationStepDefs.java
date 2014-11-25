package de.axelspringer.ideas.crowdsource.testsupport.cucumber.stepdefinitions;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.subethamail.wiser.Wiser;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.regex.Pattern;

import static de.axelspringer.ideas.crowdsource.testsupport.util.MatchesPattern.matchesPattern;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

public class RegistrationStepDefs {

    public static final String MAIL_MESSAGE_CONTENT = "Hier du klicken auf link: ";
    public static final String SENDER_ADDRESS = "crowdsource@asideas.de";

    @Autowired
    private UrlProvider urlProvider;

    private Wiser wiser;

    private String registrationLink;

    private String emailAddress;

    @Before("@WithMailServerEnabled")
    public void startMailServer() {
        wiser = new Wiser();
        wiser.setPort(10025);
        wiser.start();
    }

    @After("@WithMailServerEnabled")
    public void stopMailServer() {
        if (wiser != null) {
            wiser.stop();
        }
    }

    @Given("^a user is on the registration page$")
    public void a_user_visits_the_registration_page() throws Throwable {
        this.emailAddress = generateUniqueEmailAddress();
    }

    @And("^submits the registration form with a new email address$")
    public void submits_the_registration_form_with_a_new_email_address() throws Throwable {
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(urlProvider.applicationUrl());
        uriBuilder.path("/user");
        uriBuilder.queryParam("email", emailAddress);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(uriBuilder.build().toUriString(), null, Void.class);
    }

    @Then("^an email is sent to the given email address$")
    public void an_email_is_sent_to_the_give_email_address() throws Throwable {
        assertThat(wiser.getMessages(), hasSize(1));

        MimeMessage message = wiser.getMessages().get(0).getMimeMessage();
        assertThat(message.getSubject(), is("CrowdSource Registrierung"));
        assertThat(message.getAllRecipients(), arrayContaining(new InternetAddress(emailAddress)));
        assertThat(message.getFrom(), arrayContaining(new InternetAddress(SENDER_ADDRESS)));

        String content = IOUtils.toString(message.getInputStream());
        assertThat(content, startsWith(MAIL_MESSAGE_CONTENT));

        registrationLink = content.substring(MAIL_MESSAGE_CONTENT.length()).trim();
    }

    @And("^the email contains a valid activation link for the email address$")
    public void the_email_contains_a_valid_activation_link_for_the_email_address() throws Throwable {
        String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

        String baseActivationUrl = urlProvider.applicationUrl() + "/user/" + emailAddress + "/activation/";
        assertThat(registrationLink, matchesPattern("^" + Pattern.quote(baseActivationUrl) + uuidPattern + "$"));
    }

    private String generateUniqueEmailAddress() {
        Date now = new Date();
        return "cucumbertest+" + now.getTime() + "@crowdsource.com";
    }
}
