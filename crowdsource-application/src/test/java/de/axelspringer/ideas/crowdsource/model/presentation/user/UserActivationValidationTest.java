package de.axelspringer.ideas.crowdsource.model.presentation.user;

import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.Assert.assertEquals;

public class UserActivationValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testAllFine() {

        final UserActivation userActivation = new UserActivation("activation_token", "1234567!");
        assertEquals(0, validator.validate(userActivation).size());
    }

    @Test
    public void testEmptyActivationToken() {

        final UserActivation userActivation = new UserActivation("", "1234567!");
        assertEquals(1, validator.validate(userActivation).size());
    }

    @Test
    public void testNullActivationToken() {

        final UserActivation userActivation = new UserActivation(null, "1234567!");
        assertEquals(1, validator.validate(userActivation).size());
    }

    @Test
    public void testTooShortPassword() {

        final UserActivation userActivation = new UserActivation("activation_token", "123456!");
        assertEquals(1, validator.validate(userActivation).size());
    }

    @Test
    public void testWhiteSpacePassword() {

        final UserActivation userActivation = new UserActivation("activation_token", "12345 67!");
        assertEquals(1, validator.validate(userActivation).size());
    }

    @Test
    public void testWhiteSpaceAtBeginningPassword() {

        final UserActivation userActivation = new UserActivation("activation_token", " 1234567!");
        assertEquals(1, validator.validate(userActivation).size());
    }

    @Test
    public void testWhiteSpaceAtEndPassword() {

        final UserActivation userActivation = new UserActivation("activation_token", "1234567! ");
        assertEquals(1, validator.validate(userActivation).size());
    }

    @Test
    public void testNoSpecialCharPassword() {

        final UserActivation userActivation = new UserActivation("activation_token", "12345678");
        assertEquals(1, validator.validate(userActivation).size());
    }
}