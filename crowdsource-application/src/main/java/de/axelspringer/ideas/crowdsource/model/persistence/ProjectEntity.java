package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor // needed for serialization
@Document(collection = "projects")
public class ProjectEntity {
    @Id
    private String id;

    @DBRef
    private UserEntity user;

    private String title;

    private String shortDescription;

    private String description;

    private PublicationStatus publicationStatus;

    private int pledgeGoal;

    public ProjectEntity(UserEntity userEntity, Project project) {
        this.user = userEntity;
        this.title = project.getTitle();
        this.shortDescription = project.getShortDescription();
        this.description = project.getDescription();
        this.pledgeGoal = project.getPledgeGoal();
        this.publicationStatus = PublicationStatus.PUBLISHED;
    }
}
