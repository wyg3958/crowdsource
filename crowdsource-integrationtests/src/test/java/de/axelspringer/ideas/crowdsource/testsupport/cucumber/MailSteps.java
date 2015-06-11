package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.config.security.MongoUserDetailsService;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.user.ProjectCreator;
import de.axelspringer.ideas.crowdsource.service.UserNotificationService;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.util.CrowdSourceClient;
import de.axelspringer.ideas.crowdsource.testsupport.util.MailServerClient;
import de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmailValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class MailSteps {

    @Autowired
    private CrowdSourceClient crowdSourceClient;

    @Autowired
    private MailServerClient mailServerClient;

    private String userEmail;

    private Project createdProject;

    @When("^a new user registers$")
    public void a_new_user_registers() throws Throwable {

        userEmail = "registrationTest+" + RandomStringUtils.randomAlphanumeric(10);
        crowdSourceClient.registerUser(userEmail);
    }

    @Then("^he receives an activation email$")
    public void he_receives_an_activation_email() throws Throwable {

        final MailServerClient.Message receivedMessage = grabMessage();

        assertEquals(UserNotificationService.FROM_ADDRESS, receivedMessage.from);
        assertThat(receivedMessage.to, is(equalToIgnoringCase(userEmail + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN)));
        assertEquals(UserNotificationService.ACTIVATION_SUBJECT, receivedMessage.subject);
        assertThat(receivedMessage.message, containsString("Du hast Dich gerade auf der CrowdSource Platform angemeldet."));
        assertThat(receivedMessage.message, containsString("Um Deine Registrierung abzuschließen, öffne bitte diesen Link und setze Dein Passwort:"));
    }

    @When("^the user claims to have forgotten his password$")
    public void the_user_claims_to_have_forgotten_his_password() throws Throwable {

        crowdSourceClient.recoverPassword(MongoUserDetailsService.DEFAULT_USER_EMAIL);
    }

    @Then("^he receives a 'password forgotten' email$")
    public void he_receives_a_password_forgotten_email() throws Throwable {

        final MailServerClient.Message receivedMessage = grabMessage();
        assertEquals(UserNotificationService.FROM_ADDRESS, receivedMessage.from);
        assertEquals(MongoUserDetailsService.DEFAULT_USER_EMAIL, receivedMessage.to);
        assertEquals(UserNotificationService.PASSWORD_FORGOTTEN_SUBJECT, receivedMessage.subject);
        assertThat(receivedMessage.message, containsString("Du hast soeben ein neues Passwort für Dein Konto bei der CrowdSource Plattform angefordert."));
        assertThat(receivedMessage.message, containsString("Bitte öffne diesen Link:"));
    }

    @When("^a new project is submitted via the HTTP-Endpoint$")
    public void a_new_project_is_submitted_via_the_HTTP_Endpoint() throws Throwable {

        final CrowdSourceClient.AuthToken authToken = crowdSourceClient.authorizeWithDefaultUser();
        final Project project = new Project();
        project.setCreator(new ProjectCreator(new UserEntity(MongoUserDetailsService.DEFAULT_USER_EMAIL)));
        project.setStatus(ProjectStatus.PUBLISHED);
        project.setPledgeGoal(1000);
        project.setShortDescription("short description");
        project.setDescription("my cool description");
        project.setTitle("wow!");
        this.createdProject = crowdSourceClient.createProject(project, authToken).getBody();
    }

    @Then("^an email notification is sent to the administrator$")
    public void an_email_notification_is_sent_to_the_administrator() throws Throwable {

        final MailServerClient.Message receivedMessage = grabMessage();
        assertEquals(UserNotificationService.FROM_ADDRESS, receivedMessage.from);
        assertEquals(MongoUserDetailsService.DEFAULT_ADMIN_EMAIL, receivedMessage.to);
        assertEquals(UserNotificationService.NEW_PROJECT_SUBJECT, receivedMessage.subject);
        assertThat(receivedMessage.message, containsString("es liegt ein neues Projekt zur Freigabe vor:"));
    }

    @And("^an administrator rejects the project$")
    public void an_administrator_rejects_the_project() throws Throwable {

        createdProject.setStatus(ProjectStatus.REJECTED);
        crowdSourceClient.reject(createdProject, crowdSourceClient.authorizeWithAdminUser());
    }

    @And("^an administrator publishes the project$")
    public void an_administrator_publishes_the_project() throws Throwable {

        createdProject.setStatus(ProjectStatus.PUBLISHED);
        crowdSourceClient.publish(createdProject, crowdSourceClient.authorizeWithAdminUser());
    }

    @Then("^an email notification about the rejected project is sent to the user$")
    public void an_email_notification_about_the_rejected_project_is_sent_to_the_user() throws Throwable {

        final MailServerClient.Message receivedMessage = grabMessage();
        assertEquals(UserNotificationService.FROM_ADDRESS, receivedMessage.from);
        assertEquals(MongoUserDetailsService.DEFAULT_USER_EMAIL, receivedMessage.to);
        assertEquals(UserNotificationService.PROJECT_REJECTED_SUBJECT, receivedMessage.subject);
        assertThat(receivedMessage.message, containsString("Dein Projekt wurde leider abgelehnt."));
    }

    @Then("^an email notification about the published project is sent to the user$")
    public void an_email_notification_about_the_published_project_is_sent_to_the_user() throws Throwable {

        final MailServerClient.Message receivedMessage = grabMessage();
        assertEquals(UserNotificationService.FROM_ADDRESS, receivedMessage.from);
        assertEquals(MongoUserDetailsService.DEFAULT_USER_EMAIL, receivedMessage.to);
        assertEquals(UserNotificationService.PROJECT_PUBLISHED_SUBJECT, receivedMessage.subject);
        assertThat(receivedMessage.message, containsString("Dein Projekt wurde erfolgreich freigegeben!"));
    }

    @And("^the sent mail is cleared$")
    public void the_sent_mail_is_cleared() throws Throwable {
        grabMessage();
        mailServerClient.clearMails();
    }

    private MailServerClient.Message grabMessage() {

        mailServerClient.waitForMails(1, 5000);
        final List<MailServerClient.Message> messages = mailServerClient.messages();
        assertEquals(1, messages.size());

        return messages.get(0);
    }
}
