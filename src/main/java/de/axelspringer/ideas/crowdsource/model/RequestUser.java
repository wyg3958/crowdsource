package de.axelspringer.ideas.crowdsource.model;

import lombok.Data;

import javax.validation.constraints.AssertTrue;

@Data
public class RequestUser {

    private String email;

    private String password;

    private String activationToken;

    @AssertTrue
    private boolean termsOfServiceAccepted;
}
