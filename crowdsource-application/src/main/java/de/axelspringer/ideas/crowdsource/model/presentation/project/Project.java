package de.axelspringer.ideas.crowdsource.model.presentation.project;

import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor // needed for serialization
public class Project {

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @NotEmpty
    private String description;

    @Min(1)
    private int pledgeGoal;

    // no validation here on purpose, as this is only filled on response and ignored in request. Ideally,
    // this is filled on request too and denied if a normal user tries to create a project for someone else
    private User creator;

    public Project(ProjectEntity projectEntity) {
        this.title = projectEntity.getTitle();
        this.shortDescription = projectEntity.getShortDescription();
        this.description = projectEntity.getDescription();
        this.pledgeGoal = projectEntity.getPledgeGoal();
        this.creator = new User(projectEntity.getUser());
    }
}
