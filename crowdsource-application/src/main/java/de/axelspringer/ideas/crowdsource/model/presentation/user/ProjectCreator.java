package de.axelspringer.ideas.crowdsource.model.presentation.user;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor // required for serialization
public class ProjectCreator {

    private String id;

    private String name;

    public ProjectCreator(UserEntity user) {
        this.id = user.getId();
        this.name = UserHelper.determineNameFromEmail(user.getEmail());
    }
}
