package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.ConstraintViolations;
import de.axelspringer.ideas.crowdsource.model.presentation.User;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveUser(@RequestBody @Valid User user) {

        String activationToken = userService.generateActivationToken();

        UserEntity newUser = UserEntity.builder()
                .email(user.getEmail())
                .activationToken(activationToken)
                .roles(Arrays.asList(Roles.ROLE_USER))
                .build();

        // TODO: This is a blocking call, may last long and throw exceptions if the mail server does not want to talk to us
        userService.sendActivationMail(newUser);

        userRepository.save(newUser);

        log.debug("User saved: {}", newUser);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ConstraintViolations handleConstraintViolations(MethodArgumentNotValidException e) {

        ConstraintViolations constraintViolations = new ConstraintViolations();
        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> constraintViolations
                        .addConstraintViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        return constraintViolations;
    }

//    @RequestMapping(method = RequestMethod.PUT)
//    public ResponseEntity updateUser(@RequestBody User user) {
//
//        // TODO: replace with @NotNull + @NotEmpty
//        final String email = user.getEmail();
//        if (StringUtils.isEmpty(email)) {
//            log.debug("Email is empty", email);
//            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
//        }
//
//        UserEntity byEmail = userRepository.findByEmail(email);
//        if (byEmail == null) {
//            log.debug("User not found", email);
//            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
//        }
//
//        // TODO: update?
//
//        userRepository.save(byEmail);
//
//        log.debug("User updated: {}", byEmail);
//        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
//    }
}
