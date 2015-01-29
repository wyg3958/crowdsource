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
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserNotificationServiceTest {

    private static final String EMAIL = "horst@mail.de";
    private static final String APP_URL = "http://test.de";

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private Expression activationEmailTemplate;

    @Mock
    private Expression newProjectEmailTemplate;

    @Mock
    private Expression passwordForgottenEmailTemplate;

    @Mock
    private Expression projectPublishedEmailTemplate;

    @Mock
    private Expression projectRejectedTemplateMock;

    @InjectMocks
    private UserNotificationService userNotificationService;

    private UserEntity user;
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;
    private ArgumentCaptor<StandardEvaluationContext> contextCaptor;

    @Before
    public void init() {
        ReflectionTestUtils.setField(userNotificationService, "applicationUrl", APP_URL);

        ReflectionTestUtils.setField(userNotificationService, "activationSubject", "test-activationSubject");
        ReflectionTestUtils.setField(userNotificationService, "newProjectSubject", "test-newProjectSubject");
        ReflectionTestUtils.setField(userNotificationService, "passwordForgottenSubject", "test-passwordForgottenSubject");
        ReflectionTestUtils.setField(userNotificationService, "projectPublishedSubject", "test-projectPublishedSubject");
        ReflectionTestUtils.setField(userNotificationService, "projectRejectedSubject", "test-projectRejectedSubject");

        when(activationEmailTemplate.getValue(any(), any())).thenReturn("the_mail_content_for_activation");
        when(newProjectEmailTemplate.getValue(any(), any())).thenReturn("the_mail_content_for_new_project");
        when(passwordForgottenEmailTemplate.getValue(any(), any())).thenReturn("the_mail_content_for_password_forgotten");
        when(projectPublishedEmailTemplate.getValue(any(), any())).thenReturn("the_mail_content_for_project_published");
        when(projectRejectedTemplateMock.getValue(any(), any())).thenReturn("the_mail_content_for_project_rejected");

        messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        contextCaptor = ArgumentCaptor.forClass(StandardEvaluationContext.class);
        user = new UserEntity(EMAIL);
        user.setActivationToken("xyz");
    }

    @Test
    public void testSendActivationMail() {

        userNotificationService.sendActivationMail(user);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        verify(activationEmailTemplate).getValue(contextCaptor.capture(), any());

        final StandardEvaluationContext context = contextCaptor.getValue();
        assertThat(context.lookupVariable("link"), is("http://test.de#/signup/horst@mail.de/activation/xyz"));
        assertThat(context.lookupVariable("userName"), is("Horst"));

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getText(), is("the_mail_content_for_activation"));
    }

    @Test
    public void testSendPasswordRecoveryMail() {

        userNotificationService.sendPasswordRecoveryMail(user);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        verify(passwordForgottenEmailTemplate).getValue(contextCaptor.capture(), any());

        final StandardEvaluationContext context = contextCaptor.getValue();
        assertThat(context.lookupVariable("link"), is("http://test.de#/login/password-recovery/horst@mail.de/activation/xyz"));
        assertThat(context.lookupVariable("userName"), is("Horst"));

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getText(), is("the_mail_content_for_password_forgotten"));
    }

    @Test
    public void testSendUserNotificationMailForPublished() {

        userNotificationService.notifyUserOnProjectUpdate(project("some_id", ProjectStatus.PUBLISHED, user), EMAIL);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        verify(projectPublishedEmailTemplate).getValue(contextCaptor.capture(), any());

        final StandardEvaluationContext context = contextCaptor.getValue();
        assertThat(context.lookupVariable("link"), is("http://test.de#/project/some_id"));
        assertThat(context.lookupVariable("userName"), is("Horst"));

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getSubject(), containsString("test-projectPublishedSubject"));
        assertThat(message.getText(), is("the_mail_content_for_project_published"));
    }

    @Test
    public void testSendUserNotificationMailForRejected() {

        userNotificationService.notifyUserOnProjectUpdate(project("some_id", ProjectStatus.REJECTED, user), EMAIL);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        verify(projectRejectedTemplateMock).getValue(contextCaptor.capture(), any());

        final StandardEvaluationContext context = contextCaptor.getValue();
        assertThat(context.lookupVariable("link"), is("http://test.de#/project/some_id"));
        assertThat(context.lookupVariable("userName"), is("Horst"));

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getSubject(), containsString("test-projectRejectedSubject"));
        assertThat(message.getText(), is("the_mail_content_for_project_rejected"));
    }

    @Test
    public void testSendUserNotificationMailForDefault() {

        userNotificationService.notifyUserOnProjectUpdate(project("some_id", ProjectStatus.FULLY_PLEDGED, user), EMAIL);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getSubject(), containsString("Der Zustand des Projekts null hat sich geändert!"));
        assertThat(message.getText(), is("Das Projekt null wurde in den Zustand FULLY_PLEDGED versetzt."));
    }

    @Test
    public void testSendAdminNotificationMail() {

        userNotificationService.notifyAdminOnProjectCreation(project("some_id", ProjectStatus.PUBLISHED, user), EMAIL);

        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        verify(newProjectEmailTemplate).getValue(contextCaptor.capture(), any());

        final StandardEvaluationContext context = contextCaptor.getValue();
        assertThat(context.lookupVariable("link"), is("http://test.de#/project/some_id"));

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo(), is(new String[]{EMAIL}));
        assertThat(message.getFrom(), is(UserNotificationService.FROM_ADDRESS));
        assertThat(message.getSubject(), containsString("test-newProjectSubject"));
        assertThat(message.getText(), is("the_mail_content_for_new_project"));
    }

    private ProjectEntity project(String id, ProjectStatus status, UserEntity user) {
        final ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setCreator(user);
        project.setStatus(status);
        return project;
    }
}