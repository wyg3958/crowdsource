package de.axelspringer.ideas.crowdsource.testsupport;

import de.axelspringer.ideas.crowdsource.controller.UserController;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.mockito.Mockito.mock;

@Configuration
@EnableWebMvc
public class UserControllerTestConfig extends WebMvcConfigurerAdapter {

    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }

    @Bean
    public UserController userController() {
        return new UserController();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }

    @Bean
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }
}
