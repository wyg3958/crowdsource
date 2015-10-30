package de.asideas.crowdsource.util.validation.financinground;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Checks if a given Email is eligible for a user account at crowdsource
 */
@Documented
@Constraint(validatedBy = FinancingRoundNotCollidingValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface FinancingRoundNotColliding {

    String message() default "non-colliding";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
