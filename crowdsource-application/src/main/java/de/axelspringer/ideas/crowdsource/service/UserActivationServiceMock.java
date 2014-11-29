package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.config.AppProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary // use this bean if dev profile is active, instead of the RealUserService
@Profile(AppProfile.DEV)
public class UserActivationServiceMock extends UserActivationService {

    @Override
    protected void sendMail(SimpleMailMessage mailMessage) {
        log.info("MOCK! This mail would have been sent out on production: {}", mailMessage);
    }
}
