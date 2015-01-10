package de.axelspringer.ideas.crowdsource.util.validation.email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmail}
 */
public class EligibleEmailValidator implements ConstraintValidator<EligibleEmail, String> {

    public static final String ELIGIBLE_EMAIL_DOMAIN = "@axelspringer.de";

    @Override
    public void initialize(EligibleEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (email.contains(ELIGIBLE_EMAIL_DOMAIN) && !email.contains("_extern")) {
            return true;
        }

        context.buildConstraintViolationWithTemplate("eligible").addConstraintViolation();

        return false;
    }
}
