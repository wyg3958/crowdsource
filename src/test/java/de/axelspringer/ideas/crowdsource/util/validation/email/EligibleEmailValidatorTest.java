package de.axelspringer.ideas.crowdsource.util.validation.email;

import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EligibleEmailValidatorTest {

    private EligibleEmailValidator eligibleEmailValidator = new EligibleEmailValidator();

    @Test
    public void testIsValidValidEmail() throws Exception {

        assertTrue(eligibleEmailValidator.isValid("test@axelspringer.de", constraintValidatorContext()));
    }

    @Test
    public void testIsValidNonSpringerEmail() throws Exception {

        assertFalse(eligibleEmailValidator.isValid("test@someHost.de", constraintValidatorContext()));
    }

    @Test
    public void testIsValidConsultantEmail() throws Exception {

        assertFalse(eligibleEmailValidator.isValid("test_extern@axelspringer.de", constraintValidatorContext()));
    }

    /**
     * avoid NPEs
     */
    private ConstraintValidatorContext constraintValidatorContext() {

        final ConstraintValidatorContext validatorContext = mock(ConstraintValidatorContext.class);
        when(validatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));
        return validatorContext;
    }
}