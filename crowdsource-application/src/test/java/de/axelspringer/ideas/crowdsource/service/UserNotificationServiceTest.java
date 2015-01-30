package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.config.mail.MailTemplateConfig;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MailTemplateConfig.class, UserNotificationServiceTest.Config.class })
public class UserNotificationServiceTest {

    private static final String ADMIN_EMAIL = "some.admin@email.com";

    @Autowired
    private UserNotificationService userNotificationService;
    @Autowired
    private JavaMailSender javaMailSender;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(userNotificationService, "applicationUrl", "https://crowd.asideas.de");

        reset(javaMailSender);
    }

    @Test
    public void testSendActivationMail() {
        UserEntity user = new UserEntity("some.one@email.com");
        user.setActivationToken("activationTok3n");

        userNotificationService.sendActivationMail(user);

        SimpleMailMessage mail = getMessageFromMailSender();
        assertThat(mail.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(mail.getTo(), arrayContaining(user.getEmail()));
        assertThat(mail.getSubject(), is(UserNotificationService.ACTIVATION_SUBJECT));
        assertThat(mail.getText(), is("Hallo Some One,\n\nDu hast Dich gerade auf der AS ideas Crowd Platform angemeldet.\nUm Deine Registrierung abzuschließen, öffne bitte diesen Link und setze Dein Passwort:\n\nhttps://crowd.asideas.de#/signup/some.one@email.com/activation/activationTok3n\n\nBei Fragen wende dich an: support@crowd.asideas.de\n\nMit freundlichen Grüßen\nDein AS ideas Crowd Team\n\nAS ideAS Engineering\nAxel-Springer-Straße 65\n10888 Berlin\n\nAxel Springer ideAS Engineering GmbH\nEin Unternehmen der Axel Springer SE\nSitz Berlin, Amtsgericht Charlottenburg, HRB 138466 B\nGeschäftsführer: Daniel Keller, Niels Matusch"));
    }

    @Test
    public void testSendPasswordRecoveryMail() {
        UserEntity user = new UserEntity("some.one@email.com");
        user.setActivationToken("activationTok3n");

        userNotificationService.sendPasswordRecoveryMail(user);

        SimpleMailMessage mail = getMessageFromMailSender();
        assertThat(mail.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(mail.getTo(), arrayContaining(user.getEmail()));
        assertThat(mail.getSubject(), is(UserNotificationService.PASSWORD_FORGOTTEN_SUBJECT));
        assertThat(mail.getText(), is("Hallo Some One,\n\nDu hast soeben ein neues Passwort für Dein Konto bei der AS ideas Crowd Plattform angefordert.\n\nBitte öffne diesen Link:\n\nhttps://crowd.asideas.de#/login/password-recovery/some.one@email.com/activation/activationTok3n\n\nund setze Dein neues Passwort.\n\nBei Fragen wende Dich an: support@crowd.asideas.de\n\nMit freundlichen Grüßen\nDein AS ideas Crowd Team\n\nAS ideAS Engineering\nAxel-Springer-Straße 65\n10888 Berlin\n\nAxel Springer ideAS Engineering GmbH\nEin Unternehmen der Axel Springer SE\nSitz Berlin, Amtsgericht Charlottenburg, HRB 138466 B\nGeschäftsführer: Daniel Keller, Niels Matusch"));
    }

    @Test
    public void testSendUserNotificationMailForPublished() {
        UserEntity user = new UserEntity("some.one@email.com");

        userNotificationService.notifyCreatorOnProjectUpdate(project("proj3ctId", ProjectStatus.PUBLISHED, user, "My Super Project"));

        SimpleMailMessage mail = getMessageFromMailSender();
        assertThat(mail.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(mail.getTo(), arrayContaining(user.getEmail()));
        assertThat(mail.getSubject(), is(UserNotificationService.PROJECT_PUBLISHED_SUBJECT));
        assertThat(mail.getText(), is("Hallo Some One,\n\nDein Projekt wurde erfolgreich freigegeben!\nWeitere Informationen hinsichtlich des Prozesses kannst Du der FAQ entnehmen.\n\nZu Deinem Projekt:\n\nhttps://crowd.asideas.de#/project/proj3ctId\n\nBei Fragen wende Dich an: support@crowd.asideas.de\n\nMit freundlichen Grüßen\nDein AS ideas Crowd Team\n\nAS ideAS Engineering\nAxel-Springer-Straße 65\n10888 Berlin\n\nAxel Springer ideAS Engineering GmbH\nEin Unternehmen der Axel Springer SE\nSitz Berlin, Amtsgericht Charlottenburg, HRB 138466 B\nGeschäftsführer: Daniel Keller, Niels Matusch"));
    }

    @Test
    public void testSendUserNotificationMailForRejected() {
        UserEntity user = new UserEntity("some.one@email.com");

        userNotificationService.notifyCreatorOnProjectUpdate(project("proj3ctId", ProjectStatus.REJECTED, user, "My Super Project"));

        SimpleMailMessage mail = getMessageFromMailSender();
        assertThat(mail.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(mail.getTo(), arrayContaining(user.getEmail()));
        assertThat(mail.getSubject(), is(UserNotificationService.PROJECT_REJECTED_SUBJECT));
        assertThat(mail.getText(), is("Hallo Some One,\n\nDein Projekt wurde leider abgelehnt.\nDas AS Crowd Platform Team wird in Kürze mit Dir in Kontakt treten, um die nächsten Schritte zu besprechen.\n\nZu Deinem Projekt:\n\nhttps://crowd.asideas.de#/project/proj3ctId\n\nBei Fragen wende Dich an: support@crowd.asideas.de\n\nMit freundlichen Grüßen\nDein AS ideas Crowd Team\n\nAS ideAS Engineering\nAxel-Springer-Straße 65\n10888 Berlin\n\nAxel Springer ideAS Engineering GmbH\nEin Unternehmen der Axel Springer SE\nSitz Berlin, Amtsgericht Charlottenburg, HRB 138466 B\nGeschäftsführer: Daniel Keller, Niels Matusch"));
    }

    @Test
    public void testSendUserNotificationMailForFallback() {
        UserEntity user = new UserEntity("some.one@email.com");

        userNotificationService.notifyCreatorOnProjectUpdate(project("proj3ctId", ProjectStatus.PROPOSED, user, "My Super Project"));

        SimpleMailMessage mail = getMessageFromMailSender();
        assertThat(mail.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(mail.getTo(), arrayContaining(user.getEmail()));
        assertThat(mail.getSubject(), is("Der Zustand des Projekts My Super Project hat sich geändert!"));
        assertThat(mail.getText(), is("Das Projekt My Super Project wurde in den Zustand PROPOSED versetzt."));
    }

    @Test
    public void testNotifyAdminOnProjectCreation() {
        UserEntity user = new UserEntity("some.one@email.com");

        userNotificationService.notifyAdminOnProjectCreation(project("proj3ctId", ProjectStatus.PUBLISHED, user, "My Super Project"), ADMIN_EMAIL);

        SimpleMailMessage mail = getMessageFromMailSender();
        assertThat(mail.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(mail.getTo(), arrayContaining(ADMIN_EMAIL));
        assertThat(mail.getSubject(), is(UserNotificationService.NEW_PROJECT_SUBJECT));
        assertThat(mail.getText(), is("Hallo Admin,\n\nes liegt ein neues Projekt zur Freigabe vor:\n\nhttps://crowd.asideas.de#/project/proj3ctId\n\nMit freundlichen Grüßen\nDein AS ideas Crowd Team\n\nAS ideAS Engineering\nAxel-Springer-Straße 65\n10888 Berlin\n\nAxel Springer ideAS Engineering GmbH\nEin Unternehmen der Axel Springer SE\nSitz Berlin, Amtsgericht Charlottenburg, HRB 138466 B\nGeschäftsführer: Daniel Keller, Niels Matusch"));
    }


    private SimpleMailMessage getMessageFromMailSender() {
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        return messageCaptor.getValue();
    }

    private ProjectEntity project(String id, ProjectStatus status, UserEntity user, String title) {
        final ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setCreator(user);
        project.setTitle(title);
        project.setStatus(status);
        return project;
    }

    @Configuration
    static class Config {

        @Bean
        public UserNotificationService userNotificationService() {
            return new UserNotificationService();
        }

        @Bean
        public JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }

    }

}
