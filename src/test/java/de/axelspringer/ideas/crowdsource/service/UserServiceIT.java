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

public class UserServiceIT {

    private static final int SMTP_PORT = 10025;
    private static final String RECIPIENT_ADRESS = "test@test.de";
    private static final String SENDER_ADDRESS = "test@crowdsource.de";
    private static final String APP_URL = "some.adress.de";
    private static final String ACTIVATION_TOKEN = "123";

    private final UserService userService = new UserService();

    private Wiser inMemorySMTPServer;

    @Before
    public void injectDependencies() {

        ReflectionTestUtils.setField(userService, "fromAddress", SENDER_ADDRESS);
        ReflectionTestUtils.setField(userService, "applicationUrl", APP_URL);

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(SMTP_PORT);

        ReflectionTestUtils.setField(userService, "mailSender", mailSender);
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

        userService.sendActivationMail(UserEntity.builder().email(RECIPIENT_ADRESS).activationToken(ACTIVATION_TOKEN).build());

        assertThat(inMemorySMTPServer.getMessages(), hasSize(1));

        MimeMessage message = inMemorySMTPServer.getMessages().get(0).getMimeMessage();

        assertThat(message.getSubject(), is("CrowdSource Registrierung"));
        assertThat(message.getAllRecipients(), arrayContaining(new InternetAddress(RECIPIENT_ADRESS)));
        assertThat(message.getFrom(), arrayContaining(new InternetAddress(SENDER_ADDRESS)));

        String content = IOUtils.toString(message.getInputStream());
        assertThat(content, startsWith(UserService.MAIL_CONTENT));
    }

    @Test
    public void testActivationMailContainsActivationLink() throws MessagingException, IOException {

        userService.sendActivationMail(UserEntity.builder().email(RECIPIENT_ADRESS).activationToken(ACTIVATION_TOKEN).build());

        assertThat(inMemorySMTPServer.getMessages(), hasSize(1));


        MimeMessage message = inMemorySMTPServer.getMessages().get(0).getMimeMessage();
        String content = IOUtils.toString(message.getInputStream());
        final String registrationLink = content.substring(UserService.MAIL_CONTENT.length()).trim();

        String baseActivationUrl = APP_URL + "/user/" + RECIPIENT_ADRESS + "/activation/";
        assertEquals(registrationLink, baseActivationUrl + ACTIVATION_TOKEN);
    }
}