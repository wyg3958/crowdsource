package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.Activate;
import de.axelspringer.ideas.crowdsource.model.presentation.user.Register;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserActivationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = UserControllerTest.Config.class)
public class UserControllerTest {

    private static final String NEW_USER_MAIL_ADDRESS = "new@axelspringer.de";
    private static final String EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS = "existing_not_yet_activated@axelspringer.de";
    private static final String ACTIVATED_USER_MAIL_ADDRESS = "existing_and_activated@axelspringer.de";
    private static final String INVALID_USER_MAIL_ADDRESS = "test@test.de";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserActivationService userActivationService;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private Register register = new Register();
    private ObjectMapper mapper = new ObjectMapper();
    private UserEntity existingButNotYetActivatedUser;

    @Before
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        reset(userRepository);

        when(userRepository.findByEmail(eq(NEW_USER_MAIL_ADDRESS))).thenReturn(null);

        existingButNotYetActivatedUser = new UserEntity(EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS);
        existingButNotYetActivatedUser.setId("some-database-generated-id");
        existingButNotYetActivatedUser.setActivationToken(UUID.randomUUID().toString());
        when(userRepository.findByEmail(eq(EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS))).thenReturn(existingButNotYetActivatedUser);

        UserEntity activatedUser = new UserEntity(ACTIVATED_USER_MAIL_ADDRESS);
        activatedUser.setActivated(true);
        when(userRepository.findByEmail(eq(ACTIVATED_USER_MAIL_ADDRESS))).thenReturn(activatedUser);
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailAndTosOkOnSave() throws Exception {

        register.setEmail(NEW_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());
    }

    @Test
    public void shouldCallAllRelevantMethodsOnSave() throws Exception {

        register.setEmail(NEW_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());

        // once in NotExistingAndActivatedValidator and once in the UserController
        verify(userRepository, times(2)).findByEmail(any());
        verify(userActivationService).sendActivationMail(any());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void shouldAddNewUserIntoDatabase() throws Exception {

        register.setEmail(NEW_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());

        UserEntity savedUser = userEntityCaptor.getValue();
        assertThat(savedUser.getId(), is(nullValue()));
        assertThat(savedUser.getEmail(), is(NEW_USER_MAIL_ADDRESS));
    }

    @Test
    public void shouldUpdateExistingUserWithNewActivationToken() throws Exception {

        String originalActivationToken = existingButNotYetActivatedUser.getActivationToken();

        register.setEmail(EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());

        UserEntity savedUser = userEntityCaptor.getValue();
        assertThat(savedUser, is(sameInstance(existingButNotYetActivatedUser)));
        assertThat(savedUser.getActivationToken(), is(not(originalActivationToken)));
        assertThat(savedUser.getId(), is(existingButNotYetActivatedUser.getId()));
        assertThat(savedUser.getEmail(), is(existingButNotYetActivatedUser.getEmail()));
        assertThat(savedUser.isActivated(), is(existingButNotYetActivatedUser.isActivated()));
    }

    @Test
    public void shouldReturnErroneouslyWhenEmailNotAxelspringerOnSave() throws Exception {

        register.setEmail(INVALID_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(true);

        final MvcResult mvcResult = registerUserAndExpect(status().isBadRequest());

        assertEquals("", "{\"fieldViolations\":{\"email\":\"eligible\"}}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnErroneouslyWhenTocNotAcceptedOnSave() throws Exception {

        register.setEmail(NEW_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(false);

        final MvcResult mvcResult = registerUserAndExpect(status().isBadRequest());

        assertEquals("", "{\"fieldViolations\":{\"termsOfServiceAccepted\":\"must be true\"}}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnErroneouslyWhenUserAlreadyActivated() throws Exception {

        register.setEmail(ACTIVATED_USER_MAIL_ADDRESS);
        register.setTermsOfServiceAccepted(true);

        final MvcResult mvcResult = registerUserAndExpect(status().isBadRequest());

        assertEquals("", "{\"fieldViolations\":{\"email\":\"not_activated\"}}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testActivateUser() throws Exception {

        // TODO: right now this fails
        final Activate activate = new Activate();
        activate.setActivationToken(existingButNotYetActivatedUser.getActivationToken());
        activate.setPassword("some_passw0rd");

        final String email = existingButNotYetActivatedUser.getEmail();

        mockMvc.perform(put("/user/" + email + "/activate")
                .content(mapper.writeValueAsString(activate)))
                .andExpect(status().isOk());
    }

    @Test
    public void testActivateTwice() throws Exception {

        // TODO
        fail("not implemented");
    }

    @Test
    public void testActivateWrongActivationToken() throws Exception {

        // TODO
        fail("not implemented");
    }

    @Test
    public void testActivateInvalidPassword() throws Exception {

        // TODO
        fail("not implemented");
    }

    private MvcResult registerUserAndExpect(ResultMatcher expectedResponseStatus) throws Exception {
        return mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(register)))
                .andExpect(expectedResponseStatus)
                .andReturn();
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public UserController userController() {
            return new UserController();
        }

        @Bean
        public UserActivationService userService() {
            return mock(UserActivationService.class);
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
    }
}