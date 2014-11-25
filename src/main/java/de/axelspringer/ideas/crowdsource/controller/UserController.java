package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.UserRepository;
import de.axelspringer.ideas.crowdsource.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    public static final String EMAIL = "email";

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestParam(value = EMAIL, required = true) String email) {

        if (StringUtils.isEmpty(email)) {
            log.debug("Email is empty", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        final List<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.size() > 0) {
            log.debug("User not saved (already exists): {}", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        User user = new User(email, null);
        userRepository.save(user);

        log.debug("User saved", email);
        return new ResponseEntity<Void>(HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@RequestParam(value = EMAIL, required = true) String email) {

        if (StringUtils.isEmpty(email)) {
            log.debug("Email is empty", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        List<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.size() == 0) {
            log.debug("User not found", email);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        userRepository.delete(byEmail);

        log.debug("User deleted", email);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
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
