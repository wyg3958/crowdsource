package de.axelspringer.ideas.crowdsource.model.presentation.project;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

@Data
public class Project {

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @NotEmpty
    private String description;

    @Min(1)
    private int pledgeGoal;
}
