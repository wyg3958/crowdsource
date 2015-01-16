package de.axelspringer.ideas.crowdsource.model.persistence;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "financerounds")
public class FinancingRoundEntity {

    @Id
    private String id;

    private DateTime startDate;

    private DateTime endDate;

    /**
     * The amount of money available in the financing round
     */
    private Integer budget;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;
}
