package de.axelspringer.ideas.crowdsource.model.presentation.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Activate {

    @NotEmpty
    private String activationToken;

    @NotEmpty
    private String password;
}
