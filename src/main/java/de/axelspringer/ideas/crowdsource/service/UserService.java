package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@Service
public class UserService {

    public static final String MAIL_CONTENT = "Activation link: ";

    @Value("${de.axelspringer.ideas.crowdsource.mail.from:crowdsource@asideas.de}")
    private String fromAddress;

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String applicationUrl;

    @Autowired
    private JavaMailSender mailSender;

    public void sendActivationMail(UserEntity user) {

        String activationLink = buildActivationLink(user.getEmail(), user.getActivationToken());
        log.debug("Sending activation link {} to {}", activationLink, user.getEmail());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom(fromAddress);
        mailMessage.setSubject("CrowdSource Registrierung");
        mailMessage.setText(MAIL_CONTENT + activationLink);

        mailSender.send(mailMessage);
    }

    protected String buildActivationLink(String emailAddress, String activationToken) {
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(applicationUrl);
        uriBuilder.path("/user/{emailAddress}/activation/{activationToken}");

        return uriBuilder.buildAndExpand(emailAddress, activationToken).toUriString();
    }

    public String generateActivationToken() {
        // TODO: maybe hash this token with the email address?
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
