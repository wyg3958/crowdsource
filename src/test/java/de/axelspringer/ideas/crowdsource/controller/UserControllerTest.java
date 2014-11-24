package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.UserRepository;
import de.axelspringer.ideas.crowdsource.model.MongoResponse;
import de.axelspringer.ideas.crowdsource.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController controller;

    @Test
    public void shouldReturnErroneouslyWhenEmailIsEmptyOnSave() throws Exception {
        final MongoResponse mongoResponse = controller.saveUser("");
        assertEquals("Incorrect response text", "User email must be given.", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 1, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailIsGivenOnSave() throws Exception {
        final MongoResponse mongoResponse = controller.saveUser("test@test.de");
        assertEquals("Incorrect response text", "User saved", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 0, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenUsedEmailIsGivenOnSave() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(new User("test", "test"));

        when(userRepository.findByEmail(isA(String.class))).thenReturn(userList);

        final MongoResponse mongoResponse = controller.saveUser("test@test.de");
        assertEquals("Incorrect response text", "User not saved (already exists)", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 1, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenEmailIsEmptyOnDelete() throws Exception {
        final MongoResponse mongoResponse = controller.deleteUser("");
        assertEquals("Incorrect response text", "User email must be given.", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 1, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenUnknownEmailIsGivenOnDelete() throws Exception {
        final MongoResponse mongoResponse = controller.deleteUser("test@test.de");
        assertEquals("Incorrect response text", "User not found", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 1, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnErroneouslyWhenEmailIsGivenOnFailingDelete() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(new User("test", "test"));

        when(userRepository.findByEmail(isA(String.class))).thenReturn(userList);

        final MongoResponse mongoResponse = controller.deleteUser("test@test.de");
        assertEquals("Incorrect response text", "Deletion failed (user still in DB)", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 1, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailIsGivenOnDelete() throws Exception {
        List<User> emptyList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        userList.add(new User("test", "test"));

        when(userRepository.findByEmail(isA(String.class))).thenReturn(userList).thenReturn(emptyList);

        final MongoResponse mongoResponse = controller.deleteUser("test@test.de");
        assertEquals("Incorrect response text", "User deleted", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 0, mongoResponse.getCode());
    }
}