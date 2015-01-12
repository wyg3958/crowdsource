package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import de.axelspringer.ideas.crowdsource.service.UserService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AbstractUserControllerTest.Config.class)
public abstract class AbstractUserControllerTest {

    protected static final String NEW_USER_MAIL_ADDRESS = "new@axelspringer.de";
    protected static final String EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS = "existing.not.yet.activated@axelspringer.de";
    protected static final String ACTIVATED_USER_MAIL_ADDRESS = "existing.and.activated@axelspringer.de";
    protected static final String INVALID_USER_MAIL_ADDRESS = "test@test.de";
    protected static final String ENCODED_PASSWORD = "3nc0d3d";

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserActivationService userActivationService;

    @Resource
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;
    protected UserRegistration userRegistration = new UserRegistration();
    protected ObjectMapper mapper = new ObjectMapper();
    protected UserEntity existingButNotYetActivatedUser;
    protected UserEntity activatedUser;

    @Before
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        reset(userActivationService);
        reset(userRepository);

        when(userRepository.findByEmail(eq(NEW_USER_MAIL_ADDRESS))).thenReturn(null);

        existingButNotYetActivatedUser = new UserEntity(EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS);
        existingButNotYetActivatedUser.setId("some-database-generated-id");
        existingButNotYetActivatedUser.setActivationToken("activationToken");
        when(userRepository.findByEmail(eq(EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS))).thenReturn(existingButNotYetActivatedUser);

        activatedUser = new UserEntity(ACTIVATED_USER_MAIL_ADDRESS);
        activatedUser.setActivated(true);
        activatedUser.setActivationToken("activationToken2");
        when(userRepository.findByEmail(eq(ACTIVATED_USER_MAIL_ADDRESS))).thenReturn(activatedUser);

        reset(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    }

    protected MvcResult registerUserAndExpect(ResultMatcher expectedResponseStatus) throws Exception {
        return mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRegistration)))
                .andExpect(expectedResponseStatus)
                .andReturn();
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ControllerExceptionAdvice controllerExceptionAdvice() {
            return new ControllerExceptionAdvice();
        }

        @Bean
        public UserController userController() {
            return new UserController();
        }

        @Bean
        public UserActivationService userActivationService() {
            return mock(UserActivationService.class);
        }

        @Bean
        public UserService userService() {
            return new UserService();
        }

        @Bean
        public JavaMailSender javaMailSender() {
            // this is here to make the app context to boot up (transitive dependency of UserActivationService)
            return mock(JavaMailSender.class);
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }
    }
}