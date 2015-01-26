package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor // needed for serialization
@Document(collection = "projects")
public class ProjectEntity {

    @Id
    private String id;

    @DBRef
    private UserEntity creator;

    @DBRef
    private FinancingRoundEntity financingRound;

    private String title;

    private String shortDescription;

    private String description;

    private ProjectStatus status;

    private int pledgeGoal;

    @Indexed // since we order by this field
    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public ProjectEntity(UserEntity creator, Project project, FinancingRoundEntity financingRound) {
        this.creator = creator;
        this.financingRound = financingRound;
        this.title = project.getTitle();
        this.shortDescription = project.getShortDescription();
        this.description = project.getDescription();
        this.pledgeGoal = project.getPledgeGoal();
        this.status = ProjectStatus.PROPOSED;
    }
}
