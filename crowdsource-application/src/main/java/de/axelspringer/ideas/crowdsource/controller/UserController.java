package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.User;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserActivation;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerUser(@RequestBody @Valid UserRegistration userRegistration) {

        UserEntity userEntity = userRepository.findByEmail(userRegistration.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity(userRegistration.getEmail());
        } else {
            log.debug("A user with the address {} already exists, assigning a new activation token", userRegistration.getEmail());
        }

        userService.assignActivationTokenForRegistration(userEntity);
    }

    @RequestMapping(value = "/{email}/activation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activateUser(
            @PathVariable String email,
            @RequestBody @Valid UserActivation userActivation) {

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            log.debug("userentity with id {} does not exist and can therefore not be activated.", email);
            throw new ResourceNotFoundException();
        }

        // The activation token may be set to non blank when using password recovery.
        // In this case, the user is still activated but has a token set.
        if (isBlank(userEntity.getActivationToken()) && userEntity.isActivated()) {
            log.debug("user {} is already activated", userEntity);
            throw InvalidRequestException.userAlreadyActivated();
        }

        if (isBlank(userEntity.getActivationToken())
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{email}/password-recovery", method = RequestMethod.GET)
    public void recoverPassword(@PathVariable String email) {

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            log.debug("userentity with id {} does not exist and a password can therefore not be recovered.", email);
            throw new ResourceNotFoundException();
        }

        userService.assignActivationTokenForPasswordRecovery(userEntity);
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public User getCurrentUser(Principal principal) {

        UserEntity userEntity = userService.getUserByName(principal.getName());
        return new User(userEntity);
    }
}
