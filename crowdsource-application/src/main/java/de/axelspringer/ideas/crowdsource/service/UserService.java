package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.exceptions.NotAuthorizedException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity getUserByName(final String name) {

        UserEntity userEntity = userRepository.findByEmail(name);
        if (userEntity == null) {
            throw new NotAuthorizedException("No user found with username " + name);
        }
        return userEntity;
    }
}
