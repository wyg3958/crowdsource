package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.config.AppProfile;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary // use this bean if dev profile is active, instead of the RealUserService
@Profile(AppProfile.DEV)
public class UserActivationServiceMock extends UserActivationService {

    @Override
    public void sendActivationMail(UserEntity user) {
        String activationLink = buildActivationLink(user.getEmail(), user.getActivationToken());
        log.info("Activation link for {}: {}", activationLink, user.getEmail());
    }
}
