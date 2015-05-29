package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.CommentEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {

    List<CommentEntity> findByProject(ProjectEntity projectEntity);
}
