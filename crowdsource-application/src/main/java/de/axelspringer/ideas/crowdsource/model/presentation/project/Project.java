package de.axelspringer.ideas.crowdsource.model.presentation.project;

import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
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

    public Project(ProjectEntity projectEntity) {
        this.title = projectEntity.getTitle();
        this.shortDescription = projectEntity.getShortDescription();
        this.description = projectEntity.getDescription();
        this.pledgeGoal = projectEntity.getPledgeGoal();
    }
}
