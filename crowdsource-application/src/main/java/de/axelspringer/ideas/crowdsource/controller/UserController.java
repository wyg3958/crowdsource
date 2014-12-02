package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.ConstraintViolations;
import de.axelspringer.ideas.crowdsource.model.presentation.user.Activate;
import de.axelspringer.ideas.crowdsource.model.presentation.user.Register;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerUser(@RequestBody @Valid Register register) {

        UserEntity userEntity = userRepository.findByEmail(register.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity(register.getEmail());
        } else {
            log.debug("A user with the address {} already exists, assigning a new activation token", register.getEmail());
            userEntity.setActivationToken(UUID.randomUUID().toString());
        }

        // This is a blocking call, may last long and throw exceptions if the mail server does not want to talk to us
        // maybe make this asynchronous (+ retry) if this causes problems.
        userActivationService.sendActivationMail(userEntity);

        userRepository.save(userEntity);
        log.debug("User saved: {}", userEntity);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{email}/activate", method = RequestMethod.PUT)
    public void activateUser(@PathVariable("email") String email, @Valid Activate activate) {

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new ResourceNotFoundException();
        }

        if (StringUtils.isBlank(userEntity.getActivationToken())
                || !userEntity.getActivationToken().equals(activate.getActivationToken())) {
            throw new InvalidRequestException();
        }

        userEntity.setActivated(true);
        userEntity.setActivationToken("");
        // TODO: hash
        userEntity.setPassword(activate.getPassword());
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
