package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {

    List<ProjectEntity> findByStatusOrderByCreatedDateDesc(ProjectStatus projectStatus);
}
