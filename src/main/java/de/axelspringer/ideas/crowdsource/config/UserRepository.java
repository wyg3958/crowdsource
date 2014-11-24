package de.axelspringer.ideas.crowdsource.config;

import de.axelspringer.ideas.crowdsource.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    List<User> findByEmail(String email);
}
