package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.subethamail.wiser.Wiser;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class UserActivationServiceIT {

    private static final int SMTP_PORT = 10025;
    private static final String RECIPIENT_ADRESS = "test@test.de";
    private static final String SENDER_ADDRESS = "test@crowdsource.de";
    private static final String APP_URL = "some.adress.de";

    private final UserActivationService userActivationService = new UserActivationService();

    private Wiser inMemorySMTPServer;

    @Before
    public void injectDependencies() {

        ReflectionTestUtils.setField(userActivationService, "fromAddress", SENDER_ADDRESS);
        ReflectionTestUtils.setField(userActivationService, "applicationUrl", APP_URL);

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(SMTP_PORT);

        ReflectionTestUtils.setField(userActivationService, "mailSender", mailSender);
    }

    @Before
    public void startMailServer() {
        inMemorySMTPServer = new Wiser();
        inMemorySMTPServer.setPort(SMTP_PORT);
        inMemorySMTPServer.start();
    }

    @After
    public void killMailServer() {
        inMemorySMTPServer.stop();
    }

    @Test
    public void testSendActivationMail() throws Exception {

        userActivationService.sendActivationMail(new UserEntity(RECIPIENT_ADRESS));

        assertThat(inMemorySMTPServer.getMessages(), hasSize(1));

        MimeMessage message = inMemorySMTPServer.getMessages().get(0).getMimeMessage();

        assertThat(message.getSubject(), is("CrowdSource Registrierung"));
        assertThat(message.getAllRecipients(), arrayContaining(new InternetAddress(RECIPIENT_ADRESS)));
        assertThat(message.getFrom(), arrayContaining(new InternetAddress(SENDER_ADDRESS)));

        String content = IOUtils.toString(message.getInputStream());
        assertThat(content, startsWith(UserActivationService.MAIL_CONTENT));
    }

    @Test
    public void testActivationMailContainsActivationLink() throws MessagingException, IOException {

        UserEntity user = new UserEntity(RECIPIENT_ADRESS);
        userActivationService.sendActivationMail(user);

        assertThat(inMemorySMTPServer.getMessages(), hasSize(1));


        MimeMessage message = inMemorySMTPServer.getMessages().get(0).getMimeMessage();
        String content = IOUtils.toString(message.getInputStream());
        final String registrationLink = content.substring(UserActivationService.MAIL_CONTENT.length()).trim();

        String baseActivationUrl = APP_URL + "#/signup/" + RECIPIENT_ADRESS + "/activation/";
        assertEquals(registrationLink, baseActivationUrl + user.getActivationToken());
    }
}