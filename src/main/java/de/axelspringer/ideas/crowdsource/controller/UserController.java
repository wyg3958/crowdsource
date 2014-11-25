package de.axelspringer.ideas.crowdsource.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${de.axelspringer.ideas.crowdsource.mail.from:crowdsource@asideas.de}")
    private String fromAddress;

    @Value("${de.axelspringer.ideas.crowdsource.baseUrl}")
    private String baseUrl;

    @Autowired
    private JavaMailSender mailSender;

    @RequestMapping
    public void register(@RequestParam String emailAddress) {
        String activationLink = buildActivationLink(emailAddress);
        log.debug("Sending activation link {} to {}", activationLink, emailAddress);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailAddress);
        mailMessage.setFrom(fromAddress);
        mailMessage.setSubject("CrowdSource Registrierung");
        mailMessage.setText("Hier du klicken auf link: " + buildActivationLink(emailAddress));

        mailSender.send(mailMessage);
    }

    private String buildActivationLink(String emailAddress) {
        // TODO: maybe hash this token with the email address?
        UUID uuid = UUID.randomUUID();
        String activationToken = uuid.toString();

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromUriString(baseUrl);
        uriBuilder.path("/user/{emailAddress}/activation/{activationToken}");

        return uriBuilder.buildAndExpand(emailAddress, activationToken).toUriString();
    }

}
