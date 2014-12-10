package de.axelspringer.ideas.crowdsource.model.presentation.idea;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;

@Data
public class IdeaStorage {

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @NotEmpty
    private String fullDescription;

    @Min(1)
    private int currentFunding;
}
