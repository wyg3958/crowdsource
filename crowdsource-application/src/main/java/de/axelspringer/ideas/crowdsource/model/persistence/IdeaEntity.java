package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
// needed for serialization
@NoArgsConstructor
@Document(collection = "ideas")
public class IdeaEntity {
    @Id
    private String id;

    private String title;

    private String shortDescription;

    private String fullDescription;

    private PublicationStatus publicationStatus;

    private int funding;
}
