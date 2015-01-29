package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserActivation;
import de.axelspringer.ideas.crowdsource.service.UserNotificationService;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.ActivationForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.CrowdSourceClient;
import de.axelspringer.ideas.crowdsource.testsupport.util.MailServerClient;
import de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@Slf4j
@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ActivationSteps {

    private String activationLink;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private CrowdSourceClient crowdSourceClient;

    @Autowired
    private MailServerClient mailServerClient;

    @Autowired
    private ActivationForm activationForm;

    private WebDriver webDriver;
    private String emailName;
    private String initialPassword;
    private String newPassword;


    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
        emailName = "registrationTest+" + RandomStringUtils.randomAlphanumeric(10);
    }

    @Given("^the user's email address is already activated$")
    public void the_user_s_email_address_is_already_activated() throws Throwable {
        crowdSourceClient.registerUser(emailName);
        activationLink = getActivationLinkFromFirstEmail();

        String activationToken = activationLink.substring(activationLink.lastIndexOf('/') + 1);
        initialPassword = RandomStringUtils.randomAlphanumeric(10) + "!";
        crowdSourceClient.activateUser(emailName, new UserActivation(activationToken, initialPassword));

        mailServerClient.clearMails();
    }

    @Then("^the user has (\\d+) activation mails in his inbox with the last mail being a (registration|password-recovery) confirmation mail$")
    public void the_user_has_activation_mails_in_his_inbox_with_the_last_mail_being_a_registration_confirmation_mail(int mailCount, String type) throws Throwable {
        boolean isRegistrationFlow = type.equals("registration");

        // wait for mails to be available in mail server
        mailServerClient.waitForMails(mailCount, 5000);

        assertThat(mailServerClient.messages(), hasSize(mailCount));
        // check last received mail
        final MailServerClient.Message message = mailServerClient.messages().get(mailCount - 1);
        assertThat(message.from, is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.to, is(getGeneratedEmail()));

        String expectedSubject = isRegistrationFlow ? UserNotificationService.ACTIVATION_SUBJECT : UserNotificationService.PASSWORD_FORGOTTEN_SUBJECT;
        assertThat(message.subject, is(expectedSubject));

        String expectedContent = isRegistrationFlow ? "Du hast Dich gerade auf der AS ideas Crowd Platform angemeldet" : "Du hast soeben ein neues Passwort für Dein Konto bei der AS ideas Crowd Plattform angefordert.";
        assertThat(message.message, Matchers.containsString(expectedContent));
    }

    @When("^the user clicks the email's activation link$")
    public void the_user_clicks_the_email_s_activation_link() throws Throwable {

        activationLink = getActivationLinkFromFirstEmail();

        log.debug("Email activation link: {}", activationLink);

        webDriver.get(activationLink);
    }

    @When("^the user changes the activation token in the URL$")
    public void the_user_changes_the_activation_token_in_the_URL() throws Throwable {
        activationLink = activationLink + "somethingToMakeTheActivationTokenInvalid";
        webDriver.get(activationLink);
    }

    @Then("^the activation form for the (registration|password-recovery) flow is displayed$")
    public void the_activation_form_for_the_password_recovery_flow_is_displayed(String type) throws Throwable {
        boolean isRegistrationFlow = "registration".equals(type);

        activationForm.waitForPageLoad();
        PageFactory.initElements(webDriver, activationForm);

        String expectedHeadline = isRegistrationFlow ? "Registrierung - Letzte Schritte" : "Passwort neu setzen";
        assertThat(activationForm.getHeadline(), is(expectedHeadline));

        String expectedInfoText = isRegistrationFlow ? "Bitte vergib ein Passwort, um die Aktivierung Deines Kontos abzuschließen." : "Bitte vergib jetzt ein neues Passwort.";
        assertThat(activationForm.getInfoText(), is(expectedInfoText));
    }

    @And("^the user enters a valid password twice on activation page$")
    public void the_user_enters_a_valid_password_twice_on_activation_page() throws Throwable {
        PageFactory.initElements(webDriver, activationForm);

        newPassword = RandomStringUtils.randomAlphanumeric(10) + "!";
        activationForm.setPasswordText(newPassword);
        activationForm.setRepeatPasswordText(newPassword);
    }

    @And("^the user submits the activation form$")
    public void the_user_submits_the_activation_form() throws Throwable {
        PageFactory.initElements(webDriver, activationForm);
        activationForm.submit();
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

    @And("^he can request an access token with his newly set password$")
    public void he_can_request_an_access_token_with_his_newly_set_password() throws Throwable {
        crowdSourceClient.authorize(getGeneratedEmail(), newPassword);
    }

    @And("^he can still request an access token with his old password$")
    public void he_can_still_request_an_access_token_with_his_old_password() throws Throwable {
        crowdSourceClient.authorize(getGeneratedEmail(), initialPassword);
    }

    private String getActivationLinkFromFirstEmail() {
        mailServerClient.waitForMails(1, 5000);

        final MailServerClient.Message message = mailServerClient.messages().get(0);
        String messageContent = message.message;
        return messageContent.substring(messageContent.indexOf("http")).trim();
    }

    public String getGeneratedEmail() {
        return emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN;
    }

    public String getGeneratedEmailName() {
        return emailName;
    }

}
