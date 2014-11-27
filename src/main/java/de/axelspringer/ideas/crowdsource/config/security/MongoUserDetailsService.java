package de.axelspringer.ideas.crowdsource.config.security;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MongoUserDetailsService implements UserDetailsService {

    @Value("${de.axelspringer.ideas.crowdsource.defaultUser:test@crowd.asideas.de}")
    private String defaultEmailAddress;

    @Value("${de.axelspringer.ideas.crowdsource.defaultPassword:test}")
    private String defaultPassword;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultUserIfNeeded() {

        long existingUserCount = userRepository.count();
        if (existingUserCount > 0) {
            log.debug("There are already {} users in the database - a default user will not be created", existingUserCount);
            return;
        }

        log.info("Creating default user {} with password {}", defaultEmailAddress, defaultPassword);

        String encodedPassword = passwordEncoder.encode(defaultPassword);

        UserEntity user = new UserEntity(defaultEmailAddress, encodedPassword);

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user with username [" + username + "] found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(toList())
        );
    }
}
