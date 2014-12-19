package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {
    List<ProjectEntity> findByPublicationStatusOrderByCreatedDateDesc(PublicationStatus publicationStatus);
}
