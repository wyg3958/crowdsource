package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.exceptions.NotAuthorizedException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    public final static int ACTIVATION_TOKEN_LENGTH = 32;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserNotificationService userNotificationService;

    public UserEntity getUserByEmail(String email) {

        email = email.toLowerCase();

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new NotAuthorizedException("No user found with email " + email);
        }
        return userEntity;
    }

    public void assignActivationTokenForRegistration(UserEntity userEntity) {

        userEntity.setActivationToken(generateActivationToken());
        userNotificationService.sendActivationMail(userEntity);
        saveUser(userEntity);
    }

    public void assignActivationTokenForPasswordRecovery(UserEntity userEntity) {

        userEntity.setActivationToken(generateActivationToken());
        userNotificationService.sendPasswordRecoveryMail(userEntity);
        saveUser(userEntity);
    }


    private String generateActivationToken() {
        return RandomStringUtils.randomAlphanumeric(ACTIVATION_TOKEN_LENGTH);
    }

    private void saveUser(UserEntity userEntity) {
        userRepository.save(userEntity);
        log.debug("User saved: {}", userEntity);
    }
}
