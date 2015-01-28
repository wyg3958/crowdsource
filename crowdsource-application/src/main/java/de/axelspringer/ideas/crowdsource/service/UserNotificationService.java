package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.util.UserHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String applicationUrl;

    @Value("${de.axelspringer.ideas.crowdsource.mail.activationSubject}")
    private String activationSubject;

    @Value("${de.axelspringer.ideas.crowdsource.mail.newProjectSubject}")
    private String newProjectSubject;

    @Value("${de.axelspringer.ideas.crowdsource.mail.passwordForgottenSubject}")
    private String passwordForgottenSubject;

    @Value("${de.axelspringer.ideas.crowdsource.mail.projectPublishedSubject}")
    private String projectPublishedSubject;

    @Value("${de.axelspringer.ideas.crowdsource.mail.projectRejectedSubject}")
    private String projectRejectedSubject;


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templateService;

    public void sendActivationMail(UserEntity user) {

        String activationLink = buildLink(ACTIVATION_LINK_PATTERN, user.getEmail(), user.getActivationToken());
        log.debug("Sending activation link {} to {}", activationLink, user.getEmail());

        final String mailContent = getMailContent(user.getEmail(), activationLink, "email/activation.template");
        sendMail(user.getEmail(), activationSubject, mailContent);
    }

    public void sendPasswordRecoveryMail(UserEntity user) {

        String passwordRecoveryLink = buildLink(PASSWORD_RECOVERY_LINK_PATTERN, user.getEmail(), user.getActivationToken());
        log.debug("Sending password recovery link {} to {}", passwordRecoveryLink, user.getEmail());

        final String mailContent = getMailContent(user.getEmail(), passwordRecoveryLink, "email/password-forgotten.template");
        sendMail(user.getEmail(), passwordForgottenSubject, mailContent);
    }

    public void notifyUserOnProjectUpdate(ProjectEntity project, String emailAddress) {

        final String projectLink = getProjectLink(project.getId());
        String mailContent;
        String subject;

        switch (project.getStatus()) {
            case PUBLISHED:
                mailContent = getMailContent(emailAddress, projectLink, "email/project-published.template");
                subject = projectPublishedSubject;
                break;

            case REJECTED:
                mailContent = getMailContent(emailAddress, projectLink, "email/project-rejected.template");
                subject = projectRejectedSubject;
                break;

            default:
                subject = "Der Zustand des Projekts " + project.getTitle() + " hat sich geändert!";
                mailContent = "Das Projekt " + project.getTitle() + " wurde in den Zustand " + project.getStatus().name() + " versetzt.";
                break;
        }

        sendMail(emailAddress, subject, mailContent);
    }

    public void notifyAdminOnProjectCreation(ProjectEntity project, String emailAddress) {

        final String projectLink = getProjectLink(project.getId());

        final String mailContent = getMailContent(null, projectLink, "email/new-project.template");
        sendMail(emailAddress, newProjectSubject, mailContent);
    }

    private String getProjectLink(String projectId) {

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(applicationUrl);
        uriBuilder.fragment(PROJECT_LINK_PATTERN);

        return uriBuilder.buildAndExpand(projectId).toUriString();
    }

    private String getMailContent(String userEmail, String link, String templatePath) {

        EmailTemplateContext context = new EmailTemplateContext();
        context.setLink(link);
        context.setUserName(UserHelper.determineNameFromEmail(userEmail));

        return templateService.format(templatePath, context);
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
