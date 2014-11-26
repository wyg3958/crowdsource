package de.axelspringer.ideas.crowdsource.model.presentation;

import de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmail;
import de.axelspringer.ideas.crowdsource.util.validation.email.NotExistingAndActivated;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;

@Data
public class User {

    @NotEmpty
    @Email
    @NotExistingAndActivated
    @EligibleEmail
    private String email;

    @AssertTrue
    private boolean termsOfServiceAccepted;
}
