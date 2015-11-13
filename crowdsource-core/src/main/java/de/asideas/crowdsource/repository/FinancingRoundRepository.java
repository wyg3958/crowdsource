package de.asideas.crowdsource.repository;

import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FinancingRoundRepository extends MongoRepository<FinancingRoundEntity, String> {

    @Query("{ startDate: { $lte: ?0 }, endDate: { $gte: ?0 }}")
    FinancingRoundEntity findActive(DateTime forDate);
}
