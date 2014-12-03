package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.ConstraintViolations;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserActivation;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivationService userActivationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerUser(@RequestBody @Valid UserRegistration userRegistration) {

        UserEntity userEntity = userRepository.findByEmail(userRegistration.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity(userRegistration.getEmail());
        } else {
            log.debug("A user with the address {} already exists, assigning a new activation token", userRegistration.getEmail());
        }

        userEntity.setActivationToken(UUID.randomUUID().toString());

        // This is a blocking call, may last long and throw exceptions if the mail server does not want to talk to us
        // maybe make this asynchronous (+ retry) if this causes problems.
        userActivationService.sendActivationMail(userEntity);

        userRepository.save(userEntity);
        log.debug("User saved: {}", userEntity);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{email}/activation", method = RequestMethod.POST)
    public void activateUser(@PathVariable("email") String email, @RequestBody @Valid UserActivation userActivation) {

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            log.debug("userentity with id {} does not exist and can therefore not be activated.", email);
            throw new ResourceNotFoundException();
        }

        if (StringUtils.isBlank(userEntity.getActivationToken())
                || !userEntity.getActivationToken().equals(userActivation.getActivationToken())) {
            log.debug("token mismatch on activation request for user with email: {}", email);
            throw new InvalidRequestException();
        }

        userEntity.setActivated(true);
        userEntity.setActivationToken("");
        userEntity.setPassword(passwordEncoder.encode(userActivation.getPassword()));

        userRepository.save(userEntity);
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
}
