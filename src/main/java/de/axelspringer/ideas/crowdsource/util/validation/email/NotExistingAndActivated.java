package de.axelspringer.ideas.crowdsource.util.validation.email;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 */
@Documented
@Constraint(validatedBy = NotExistingAndActivatedValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
@ReportAsSingleViolation
@NotNull
@Size(min = 1)
public @interface NotExistingAndActivated {

    String message() default "already_activated";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
