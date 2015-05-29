package de.axelspringer.ideas.crowdsource.model.presentation.user;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.util.UserHelper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor // required for serialization
public class User {

    private String email;
    private List<String> roles;
    private int budget;
    private String name;

    public User(final UserEntity userEntity) {
        this.email = userEntity.getEmail();
        this.budget = userEntity.getBudget();
        this.roles = userEntity.getRoles();
        this.name = UserHelper.determineNameFromEmail(email);
    }
}
