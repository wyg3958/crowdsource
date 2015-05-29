package de.axelspringer.ideas.crowdsource.model.presentation.user;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.ProjectSummaryView;
import de.axelspringer.ideas.crowdsource.util.UserHelper;
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

    @JsonView(ProjectSummaryView.class)
    private String name;

    @JsonView(ProjectSummaryView.class)
    private String email;

    public ProjectCreator(UserEntity user) {
        this.id = user.getId();
        this.name = UserHelper.determineNameFromEmail(user.getEmail());
        this.email = user.getEmail();
    }
}
