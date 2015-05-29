package de.axelspringer.ideas.crowdsource.testutil;

import javax.validation.ConstraintValidatorContext;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidatorTestUtil {

    /**
     * avoid NPEs
     */
    public static ConstraintValidatorContext constraintValidatorContext() {

        final ConstraintValidatorContext validatorContext = mock(ConstraintValidatorContext.class);
        when(validatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
        return validatorContext;
    }
}
