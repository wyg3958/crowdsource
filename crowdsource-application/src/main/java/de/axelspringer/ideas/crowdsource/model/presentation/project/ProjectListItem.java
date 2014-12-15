package de.axelspringer.ideas.crowdsource.model.presentation.project;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

/**
 * This object is used for overviews only to reduce amount of data transferred.
 */
@Data
public class ProjectListItem {

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @Min(1)
    private int pledgeGoal;
}
