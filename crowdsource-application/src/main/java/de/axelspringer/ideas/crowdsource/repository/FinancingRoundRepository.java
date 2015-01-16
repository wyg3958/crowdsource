package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FinancingRoundRepository extends MongoRepository<FinancingRoundEntity, String> {

    @Query("{ startDate: { $lte: ?0 }, endDate: { $gte: ?0 }}")
    FinancingRoundEntity findActive(DateTime forDate);
}
