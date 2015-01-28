package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserNotificationServiceTest {

    private static final String EMAIL = "some@mail.de";
    private static final String APP_URL = "http://test.de";

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private UserNotificationService userNotificationService;

    private UserEntity user;


    @Before
    public void init() {
        ReflectionTestUtils.setField(userNotificationService, "applicationUrl", APP_URL);

        user = new UserEntity(EMAIL);
        user.setActivationToken("xyz");
    }

    @Test
    public void testSendActivationMail() {

        userNotificationService.sendActivationMail(user);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getText(), is("Activation link: http://test.de#/signup/some@mail.de/activation/xyz"));
    }

    @Test
    public void testSendPasswordRecoveryMail() {

        userNotificationService.sendPasswordRecoveryMail(user);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getText(), is("Password recovery link: http://test.de#/login/password-recovery/some@mail.de/activation/xyz"));
    }

    @Test
    public void testSendUserNotificationMail() {

        userNotificationService.notifyUserOnProjectUpdate(project("some_id", ProjectStatus.PUBLISHED, user), EMAIL);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getSubject(), containsString("Der Zustand Des Projektes"));
        assertThat(message.getText(), containsString("wurde in den Zustand"));
    }

    @Test
    public void testSendAdminNotificationMail() {

        userNotificationService.notifyAdminOnProjectCreation(project("some_id", ProjectStatus.PUBLISHED, user), EMAIL);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getSubject(), containsString("Freigabeanforderung: Das Projekt"));
        assertThat(message.getText(), containsString("wurde in den Zustand"));
    }

    private ProjectEntity project(String id, ProjectStatus status, UserEntity user) {
        final ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setCreator(user);
        project.setStatus(status);
        return project;
    }
}