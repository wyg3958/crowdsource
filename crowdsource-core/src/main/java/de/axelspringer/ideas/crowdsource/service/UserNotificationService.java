package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.util.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class UserNotificationService {

    public static final String FROM_ADDRESS = "noreply@crowd.asideas.de";

    public static final String PROJECT_LINK_PATTERN = "/project/{id}";
    public static final String ACTIVATION_LINK_PATTERN = "/signup/{emailAddress}/activation/{activationToken}";
    public static final String PASSWORD_RECOVERY_LINK_PATTERN = "/login/password-recovery/{emailAddress}/activation/{activationToken}";

    public static final String ACTIVATION_SUBJECT = "Bitte vergib ein Passwort für Dein Konto auf der AS ideas Crowd Platform";
    public static final String NEW_PROJECT_SUBJECT = "Neues Projekt erstellt";
    public static final String PASSWORD_FORGOTTEN_SUBJECT = "Bitte vergib ein Passwort für Dein Konto auf der AS ideas Crowd Platform";
    public static final String PROJECT_PUBLISHED_SUBJECT = "Freigabe Deines Projektes";
    public static final String PROJECT_REJECTED_SUBJECT = "Freigabe Deines Projektes";

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl:http://localhost:8080}")
    private String applicationUrl;

    @Autowired
    private Expression activationEmailTemplate;

    @Autowired
    private Expression newProjectEmailTemplate;

    @Autowired
    private Expression passwordForgottenEmailTemplate;

    @Autowired
    private Expression projectPublishedEmailTemplate;

    @Autowired
    private Expression projectRejectedEmailTemplate;

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationMail(UserEntity user) {

        String activationLink = buildLink(ACTIVATION_LINK_PATTERN, user.getEmail(), user.getActivationToken());
        log.debug("Sending activation link {} to {}", activationLink, user.getEmail());

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("link", activationLink);
        context.setVariable("userName", UserHelper.determineNameFromEmail(user.getEmail()));
        final String mailContent = activationEmailTemplate.getValue(context, String.class);

        sendMail(user.getEmail(), ACTIVATION_SUBJECT, mailContent);
    }

    public void sendPasswordRecoveryMail(UserEntity user) {

        String passwordRecoveryLink = buildLink(PASSWORD_RECOVERY_LINK_PATTERN, user.getEmail(), user.getActivationToken());
        log.debug("Sending password recovery link {} to {}", passwordRecoveryLink, user.getEmail());

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("link", passwordRecoveryLink);
        context.setVariable("userName", UserHelper.determineNameFromEmail(user.getEmail()));
        final String mailContent = passwordForgottenEmailTemplate.getValue(context, String.class);

        sendMail(user.getEmail(), PASSWORD_FORGOTTEN_SUBJECT, mailContent);
    }

    public void notifyCreatorOnProjectUpdate(ProjectEntity project) {

        final StandardEvaluationContext context = new StandardEvaluationContext();
        final String projectLink = getProjectLink(project.getId());

        context.setVariable("link", projectLink);
        context.setVariable("userName", UserHelper.determineNameFromEmail(project.getCreator().getEmail()));

        switch (project.getStatus()) {
            case PUBLISHED:
                final String publishMessage = projectPublishedEmailTemplate.getValue(context, String.class);
                sendMail(project.getCreator().getEmail(), PROJECT_PUBLISHED_SUBJECT, publishMessage);
                break;

            case REJECTED:
                final String rejectedMessage = projectRejectedEmailTemplate.getValue(context, String.class);
                sendMail(project.getCreator().getEmail(), PROJECT_REJECTED_SUBJECT, rejectedMessage);
                break;

            default:
                final String defaultMessage = "Das Projekt " + project.getTitle() + " wurde in den Zustand " + project.getStatus().name() + " versetzt.";
                final String defaultSubject = "Der Zustand des Projekts " + project.getTitle() + " hat sich geändert!";
                sendMail(project.getCreator().getEmail(), defaultSubject, defaultMessage);
                break;
        }
    }

    public void notifyAdminOnProjectCreation(ProjectEntity project, String emailAddress) {

        final String projectLink = getProjectLink(project.getId());

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("link", projectLink);

        final String mailContent = newProjectEmailTemplate.getValue(context, String.class);
        sendMail(emailAddress, NEW_PROJECT_SUBJECT, mailContent);
    }

    private String getProjectLink(String projectId) {

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(applicationUrl);
        uriBuilder.fragment(PROJECT_LINK_PATTERN);

        return uriBuilder.buildAndExpand(projectId).toUriString();
    }

    private String buildLink(String urlPattern, String emailAddress, String activationToken) {

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(applicationUrl);
        uriBuilder.fragment(urlPattern);

        return uriBuilder.buildAndExpand(emailAddress, activationToken).toUriString();
    }

    private void sendMail(String email, String subject, String messageText) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom(FROM_ADDRESS);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageText);

        mailSender.send(mailMessage);
    }

}
