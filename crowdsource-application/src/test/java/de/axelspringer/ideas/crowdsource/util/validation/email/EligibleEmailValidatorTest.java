package de.axelspringer.ideas.crowdsource.util.validation.email;

import de.axelspringer.ideas.crowdsource.testutil.ValidatorTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EligibleEmailValidatorTest {

    private EligibleEmailValidator eligibleEmailValidator = new EligibleEmailValidator();

    @Test
    public void testIsValidValidEmail() throws Exception {

        assertTrue(eligibleEmailValidator.isValid("test@axelspringer.de", ValidatorTestUtil.constraintValidatorContext()));
    }

    @Test
    public void testIsValidNonSpringerEmail() throws Exception {

        assertFalse(eligibleEmailValidator.isValid("test@someHost.de", ValidatorTestUtil.constraintValidatorContext()));
    }

    @Test
    public void testIsValidConsultantEmail() throws Exception {

        assertFalse(eligibleEmailValidator.isValid("test_extern@axelspringer.de", ValidatorTestUtil.constraintValidatorContext()));
    }
}