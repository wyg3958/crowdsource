package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.ErrorResponse;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserActivation;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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

@Slf4j
@RestController
@RequestMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivationService userActivationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void registerUser(@RequestBody @Valid UserRegistration userRegistration) {

        UserEntity userEntity = userRepository.findByEmail(userRegistration.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity(userRegistration.getEmail());
        } else {
            log.debug("A user with the address {} already exists, assigning a new activation token", userRegistration.getEmail());
        }

        userEntity.setActivationToken(RandomStringUtils.randomAlphanumeric(32));

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

        if (userEntity.isActivated()) {
            log.debug("user {} is already activated", userEntity);
            throw InvalidRequestException.userAlreadyActivated();
        }

        if (StringUtils.isBlank(userEntity.getActivationToken())
            ||!userEntity.getActivationToken().equals(userActivation.getActivationToken())) {
            log.debug("token mismatch on activation request for user with email: {} (was {}, expected: {})",
                    email, userActivation.getActivationToken(), userEntity.getActivationToken());

            throw InvalidRequestException.activationTokenInvalid();
        }

        userEntity.setActivated(true);
        userEntity.setActivationToken("");
        userEntity.setPassword(passwordEncoder.encode(userActivation.getPassword()));

        userRepository.save(userEntity);
        log.debug("User activated: {}", userEntity);
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequest(InvalidRequestException e) {

        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolations(MethodArgumentNotValidException e) {

        ErrorResponse errorResponse = new ErrorResponse("field_errors");
        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> errorResponse
                        .addConstraintViolation(fieldError.getField(), fieldError.getDefaultMessage()));
        return errorResponse;
    }
}
