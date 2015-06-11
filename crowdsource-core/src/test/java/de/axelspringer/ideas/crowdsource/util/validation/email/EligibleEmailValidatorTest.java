package de.axelspringer.ideas.crowdsource.util.validation.email;

import de.axelspringer.ideas.crowdsource.testutil.ValidatorTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class EligibleEmailValidatorTest {

    private EligibleEmailValidator eligibleEmailValidator;

    @Before
    public void beforeMethod() throws Exception {
        eligibleEmailValidator = spy(new EligibleEmailValidator());
        Whitebox.setInternalState(eligibleEmailValidator, "emailBlacklistPatterns", Arrays.asList("_extern"));
        doReturn("@axelspringer.de").when(eligibleEmailValidator).getEligibleEmailDomain();
    }

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