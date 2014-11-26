package de.axelspringer.ideas.crowdsource.util.validation.email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// TODO: test
public class EligibleEmailValidator implements ConstraintValidator<EligibleEmail, String> {

    @Override
    public void initialize(EligibleEmail constraintAnnotation) {
        // no-op
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        if (email.contains("@axelspringer.de") && !email.contains("_extern")) {
            return true;
        }

        context.buildConstraintViolationWithTemplate("not_eligible").addConstraintViolation();

        return false;
    }
}
