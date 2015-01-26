package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
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

    public static final String ACTIVATION_MAIL_CONTENT = "Activation link: ";
    public static final String PASSWORD_RECOVERY_MAIL_CONTENT = "Password recovery link: ";

    public static final String FROM_ADDRESS = "noreply@crowd.asideas.de";
    public static final String REGISTRATION_SUBJECT = "CrowdSource Registrierung";
    public static final String PASSWORD_RECOVERY_SUBJECT = "CrowdSource Passwort Vergessen";

    public static final String ACTIVATION_LINK_PATTERN = "/signup/{emailAddress}/activation/{activationToken}";
    public static final String PASSWORD_RECOVERY_LINK_PATTERN = "/login/password-recovery/{emailAddress}/activation/{activationToken}";

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String applicationUrl;

    @Autowired
    private JavaMailSender mailSender;


    public void sendActivationMail(UserEntity user) {

        String activationLink = buildLink(ACTIVATION_LINK_PATTERN, user.getEmail(), user.getActivationToken());
        log.debug("Sending activation link {} to {}", activationLink, user.getEmail());

        sendMail(user.getEmail(), REGISTRATION_SUBJECT, ACTIVATION_MAIL_CONTENT + activationLink);
    }

    public void sendPasswordRecoveryMail(UserEntity user) {

        String passwordRecoveryLink = buildLink(PASSWORD_RECOVERY_LINK_PATTERN, user.getEmail(), user.getActivationToken());
        log.debug("Sending password recovery link {} to {}", passwordRecoveryLink, user.getEmail());

        sendMail(user.getEmail(), PASSWORD_RECOVERY_SUBJECT, PASSWORD_RECOVERY_MAIL_CONTENT + passwordRecoveryLink);
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

    public void notifyUserOnProjectUpdate(ProjectEntity project, String emailAddress) {

        final String subject = "Der Zustand Des Projektes " + project.getTitle() + " hat sich geändert!";
        final String message = "Das Projekt " + project.getTitle() + " wurde in den Zustand " + project.getStatus().name() + " versetzt.";

        sendMail(emailAddress, subject, message);
    }

    public void notifyAdminOnProjectCreation(ProjectEntity project, String emailAddress) {

        final String subject = "Freigabeanforderung: Das Projekt " + project.getTitle() + " wurde angelegt.";
        final String message = "Das Projekt " + project.getTitle() + " wurde in den Zustand " + project.getStatus().name() + " versetzt.";

        sendMail(emailAddress, subject, message);
    }
}
