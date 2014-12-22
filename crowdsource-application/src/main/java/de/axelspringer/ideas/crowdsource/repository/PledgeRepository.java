package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PledgeRepository extends MongoRepository<PledgeEntity, String> {
}
