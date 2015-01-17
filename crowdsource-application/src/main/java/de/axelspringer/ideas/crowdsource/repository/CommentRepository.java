package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.CommentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<CommentEntity, String> {
}
