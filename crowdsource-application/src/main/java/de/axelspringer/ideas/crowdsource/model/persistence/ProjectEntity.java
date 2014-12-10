package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
// needed for serialization
@NoArgsConstructor
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
}
