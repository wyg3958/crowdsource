package de.axelspringer.ideas.crowdsource.model.persistence;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "financerounds")
public class FinanceRoundEntity {

    @Id
    private String id;

    private int initialUserBudget;

    private DateTime endDate;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;
}
