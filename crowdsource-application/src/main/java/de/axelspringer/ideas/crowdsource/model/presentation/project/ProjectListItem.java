package de.axelspringer.ideas.crowdsource.model.presentation.project;

import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * This object is used for overviews only to reduce amount of data transferred.
 */
@Data
public class ProjectListItem {

    public ProjectListItem() {
    }

    public ProjectListItem(ProjectEntity entity) {
        this.title = entity.getTitle();
        this.shortDescription = entity.getShortDescription();
        this.pledgeGoal = entity.getPledgeGoal();
    }

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @Min(1)
    private int pledgeGoal;
}
