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
}
