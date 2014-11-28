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

@Slf4j
@Service
public class UserActivationService {

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

        sendMail(mailMessage);
    }

    private String buildActivationLink(String emailAddress, String activationToken) {
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(applicationUrl);
        uriBuilder.fragment("/signup/{emailAddress}/activation/{activationToken}");

        return uriBuilder.buildAndExpand(emailAddress, activationToken).toUriString();
    }

    protected void sendMail(SimpleMailMessage mailMessage) {
        mailSender.send(mailMessage);
    }
}
