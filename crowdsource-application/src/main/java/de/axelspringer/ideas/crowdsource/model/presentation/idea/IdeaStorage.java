package de.axelspringer.ideas.crowdsource.model.presentation.idea;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class IdeaStorage {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String shortDescription;

    @NotEmpty
    private String fullDescription;

    @NotEmpty
    private int currentFunding;
}
