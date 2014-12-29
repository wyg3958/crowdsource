package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.FinanceRoundEntity;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FinanceRoundRepository extends MongoRepository<FinanceRoundEntity, String> {

    FinanceRoundEntity findByEndDateLessThanEqual(DateTime forDate);
}
