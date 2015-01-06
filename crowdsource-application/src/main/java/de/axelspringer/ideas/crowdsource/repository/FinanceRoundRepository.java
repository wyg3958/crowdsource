package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.FinanceRoundEntity;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FinanceRoundRepository extends MongoRepository<FinanceRoundEntity, String> {

    @Query("{ startDate: { $lte: ?0 }, endDate: { $gte: ?0 }}")
    FinanceRoundEntity findActive(DateTime forDate);
}
