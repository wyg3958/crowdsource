package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.model.User;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    public static final String WRONG_HTTP_STATUS_CODE = "Wrong HTTP status code";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @Test
    public void shouldReturnErroneouslyWhenEmailIsEmptyOnSave() throws Exception {
        final ResponseEntity responseEntity = controller.saveUser("");
        assertEquals(WRONG_HTTP_STATUS_CODE, HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailIsGivenOnSave() throws Exception {
        final ResponseEntity responseEntity = controller.saveUser("test@test.de");
        assertEquals(WRONG_HTTP_STATUS_CODE, HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenUsedEmailIsGivenOnSave() throws Exception {
        User user = new User("test@test.test", "test", Arrays.asList());

        when(userRepository.findByEmail(isA(String.class))).thenReturn(user);

        final ResponseEntity responseEntity = controller.saveUser("test@test.de");
        assertEquals(WRONG_HTTP_STATUS_CODE, HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenEmailIsEmptyOnDelete() throws Exception {
        final ResponseEntity responseEntity = controller.deleteUser("");
        assertEquals(WRONG_HTTP_STATUS_CODE, HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenUnknownEmailIsGivenOnDelete() throws Exception {
        final ResponseEntity responseEntity = controller.deleteUser("test@test.de");
        assertEquals(WRONG_HTTP_STATUS_CODE, HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailIsGivenOnDelete() throws Exception {
        User user = new User("test@test.test", "test", Arrays.asList());

        when(userRepository.findByEmail(isA(String.class))).thenReturn(user).thenReturn(null);

        final ResponseEntity responseEntity = controller.deleteUser("test@test.de");
        assertEquals(WRONG_HTTP_STATUS_CODE, HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
}