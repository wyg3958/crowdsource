package de.asideas.crowdsource.repository;

import de.asideas.crowdsource.model.persistence.CommentEntity;
import de.asideas.crowdsource.model.persistence.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {

    List<CommentEntity> findByProject(ProjectEntity projectEntity);
}
