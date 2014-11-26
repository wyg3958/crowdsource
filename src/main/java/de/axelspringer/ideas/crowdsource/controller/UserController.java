package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.model.RequestUser;
import de.axelspringer.ideas.crowdsource.model.User;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestBody @Valid RequestUser requestUser) {

        final String email = requestUser.getEmail();
        if (StringUtils.isEmpty(email)) {
            log.debug("Email is empty", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        final User byEmail = userRepository.findByEmail(email);
        if (byEmail != null) {
            log.debug("User not saved (already exists): {}", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        String activationToken = userService.generateActivationToken();

        User user = User.builder()
                .email(email)
                .activationToken(activationToken)
                .roles(Arrays.asList(Roles.ROLE_USER))
                .build();

        // TODO: This is a blocking call, may last long and throw exceptions if the mail server does not want to talk to us
        userService.sendActivationMail(user);

        userRepository.save(user);

        log.debug("User saved: {}", user);
        return new ResponseEntity<Void>(HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateUser(@RequestBody RequestUser requestUser) {

        final String email = requestUser.getEmail();
        if (StringUtils.isEmpty(email)) {
            log.debug("Email is empty", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        User byEmail = userRepository.findByEmail(email);
        if (byEmail == null) {
            log.debug("User not found", email);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        // TODO: update?

        userRepository.save(byEmail);

        log.debug("User updated: {}", byEmail);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@RequestBody RequestUser requestUser) {

        final String email = requestUser.getEmail();
        if (StringUtils.isEmpty(email)) {
            log.debug("Email is empty", email);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        User byEmail = userRepository.findByEmail(email);
        if (byEmail == null) {
            log.debug("User not found", email);
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        userRepository.delete(byEmail);

        log.debug("User deleted", email);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}
