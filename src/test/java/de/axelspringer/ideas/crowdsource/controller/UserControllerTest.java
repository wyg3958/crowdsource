package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.model.MongoResponse;
import de.axelspringer.ideas.crowdsource.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private MongoOperations mongoOperations;

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
        when(mongoOperations.findOne(isA(Query.class), Matchers.any())).thenReturn(new User("test", "test"));

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
        when(mongoOperations.findOne(isA(Query.class), Matchers.any())).thenReturn(new User("test", "test"));

        final MongoResponse mongoResponse = controller.deleteUser("test@test.de");
        assertEquals("Incorrect response text", "Deletion failed (user still in DB)", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 1, mongoResponse.getCode());
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailIsGivenOnDelete() throws Exception {
        when(mongoOperations.findOne(isA(Query.class), Matchers.any())).thenReturn(new User("test", "test")).thenReturn(null);

        final MongoResponse mongoResponse = controller.deleteUser("test@test.de");
        assertEquals("Incorrect response text", "User deleted", mongoResponse.getMessage());
        assertEquals("Incorrect response code", 0, mongoResponse.getCode());
    }
}