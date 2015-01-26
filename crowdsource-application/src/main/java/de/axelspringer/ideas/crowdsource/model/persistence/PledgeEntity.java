package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor // needed for serialization
@Document(collection = "pledges")
public class PledgeEntity {

    @Id
    private String id;

    @DBRef
    private ProjectEntity project;

    @DBRef
    private UserEntity user;

    @DBRef
    private FinancingRoundEntity financingRound;

    private int amount;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public PledgeEntity(ProjectEntity projectEntity, UserEntity userEntity, Pledge pledge, FinancingRoundEntity financingRoundEntity) {
        this.project = projectEntity;
        this.user = userEntity;
        this.financingRound = financingRoundEntity;
        this.amount = pledge.getAmount();
    }
}
