package de.axelspringer.ideas.crowdsource.config.security;

import de.axelspringer.ideas.crowdsource.config.AppProfiles;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;

import static java.util.stream.Collectors.toList;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    public final static String DEFAULT_USER_EMAIL = "crowdsource@axelspringer.de";
    public final static String DEFAULT_USER_PASS = "einEselGehtZumBaecker!";

    public final static String DEFAULT_ADMIN_EMAIL = "cs_admin@axelspringer.de";
    public final static String DEFAULT_ADMIN_PASS = "einAdminGehtZumBaecker!";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MongoUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void createUsers() {

        if (!environment.acceptsProfiles(AppProfiles.CREATE_USERS)) {
            log.info("not creating or updating any users.");
            return;
        }

        log.info("creating or updating users: {}:{} and {}:{}", DEFAULT_USER_EMAIL, DEFAULT_USER_PASS, DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASS);

        // default user
        UserEntity defaultUser = userRepository.findByEmail(DEFAULT_USER_EMAIL);
        if (defaultUser == null) {
            defaultUser = new UserEntity(DEFAULT_USER_EMAIL);
        }
        defaultUser.setPassword(passwordEncoder.encode(DEFAULT_USER_PASS));
        defaultUser.setActivated(true);
        defaultUser.setRoles(Collections.singletonList(Roles.ROLE_USER));
        userRepository.save(defaultUser);

        // admin
        UserEntity admin = userRepository.findByEmail(DEFAULT_ADMIN_EMAIL);
        if (admin == null) {
            admin = new UserEntity(DEFAULT_ADMIN_EMAIL);
        }
        admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASS));
        admin.setActivated(true);
        admin.setRoles(Arrays.asList(Roles.ROLE_USER, Roles.ROLE_ADMIN));
        userRepository.save(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        username = username.toLowerCase();

        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user with username [" + username + "] found");
        }

        if (!user.isActivated()) {
            throw new UsernameNotFoundException("User with username [" + username + "] is not activated yet");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(toList())
        );
    }
}
