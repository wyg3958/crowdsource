package de.axelspringer.ideas.crowdsource.controller.usercontroller;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterUserControllerTest extends AbstractUserControllerTest {

    private UserRegistration userRegistration = new UserRegistration();

    @Test
    public void registerUser_shouldReturnSuccessfullyWhenEmailAndTosOkOnSave() throws Exception {

        userRegistration.setEmail(NEW_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());
    }

    @Test
    public void registerUser_shouldCallAllRelevantMethodsOnSave() throws Exception {

        userRegistration.setEmail(NEW_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());

        // once in NotExistingAndActivatedValidator and once in the UserController
        verify(userRepository, times(2)).findByEmail(any());
        verify(userNotificationService).sendActivationMail(any());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void registerUser_shouldAddNewUserIntoDatabase() throws Exception {

        userRegistration.setEmail(NEW_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(true);

        registerUserAndExpect(status().isCreated());

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userEntityCaptor.capture());

        UserEntity savedUser = userEntityCaptor.getValue();
        assertThat(savedUser.getId(), is(nullValue()));
        assertThat(savedUser.getEmail(), is(NEW_USER_MAIL_ADDRESS));
    }

    @Test
    public void registerUser_shouldUpdateExistingUserWithNewActivationToken() throws Exception {

        String originalActivationToken = existingButNotYetActivatedUser.getActivationToken();

        userRegistration.setEmail(EXISTING_BUT_NOT_YET_ACTIVATED_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(true);

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
    public void registerUser_shouldReturnErroneouslyWhenEmailNotAxelspringerOnSave() throws Exception {

        userRegistration.setEmail(INVALID_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(true);

        final MvcResult mvcResult = registerUserAndExpect(status().isBadRequest());

        assertEquals("{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"email\":\"eligible\"}}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void registerUser_shouldReturnErroneouslyWhenTocNotAcceptedOnSave() throws Exception {

        userRegistration.setEmail(NEW_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(false);

        final MvcResult mvcResult = registerUserAndExpect(status().isBadRequest());

        assertEquals("{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"termsOfServiceAccepted\":\"must be true\"}}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void registerUser_shouldReturnErroneouslyWhenUserAlreadyActivated() throws Exception {

        userRegistration.setEmail(ACTIVATED_USER_MAIL_ADDRESS);
        userRegistration.setTermsOfServiceAccepted(true);

        final MvcResult mvcResult = registerUserAndExpect(status().isBadRequest());

        assertEquals("", "{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"email\":\"not_activated\"}}", mvcResult.getResponse().getContentAsString());
    }

    private MvcResult registerUserAndExpect(ResultMatcher expectedResponseStatus) throws Exception {
        return mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRegistration)))
                .andExpect(expectedResponseStatus)
                .andReturn();
    }

}
