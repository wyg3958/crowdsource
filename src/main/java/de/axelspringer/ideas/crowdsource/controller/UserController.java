package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.ConstraintViolations;
import de.axelspringer.ideas.crowdsource.model.presentation.User;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public void saveUser(@RequestBody @Valid User user) {

        UserEntity userEntity = userRepository.findByEmail(user.getEmail());
        if (userEntity == null) {
            userEntity = new UserEntity(user.getEmail());
        }
        else {
            log.debug("A user with the address {} already exists, assigning a new activation token", user.getEmail());
            userEntity.assignNewActivationToken();
        }

        // This is a blocking call, may last long and throw exceptions if the mail server does not want to talk to us
        // maybe make this asynchronous (+ retry) if this causes problems.
        userActivationService.sendActivationMail(userEntity);

        userRepository.save(userEntity);
        log.debug("User saved: {}", userEntity);
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
