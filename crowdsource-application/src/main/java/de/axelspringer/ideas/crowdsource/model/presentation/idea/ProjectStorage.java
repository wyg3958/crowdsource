package de.axelspringer.ideas.crowdsource.model.presentation.idea;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

@Data
public class ProjectStorage {

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @NotEmpty
    private String description;

    @Min(1)
    private int pledgeGoal;
}
