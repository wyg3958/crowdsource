package de.asideas.crowdsource.repository;

import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import de.asideas.crowdsource.domain.model.PledgeEntity;
import de.asideas.crowdsource.domain.model.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PledgeRepository extends MongoRepository<PledgeEntity, String> {
    List<PledgeEntity> findByProjectAndFinancingRound(ProjectEntity projectEntity, FinancingRoundEntity financingRoundEntity);
}
