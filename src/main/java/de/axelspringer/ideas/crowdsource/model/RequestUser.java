package de.axelspringer.ideas.crowdsource.model;

import lombok.Data;

@Data
public class RequestUser {

    private String email;

    private String password;

    private String activationToken;
}
