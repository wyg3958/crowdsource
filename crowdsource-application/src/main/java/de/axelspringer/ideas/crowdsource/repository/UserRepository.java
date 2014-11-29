package de.axelspringer.ideas.crowdsource.repository;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {

    UserEntity findByEmail(String email);

}
