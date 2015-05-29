package de.axelspringer.ideas.crowdsource.model.presentation.user;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor // required for serialization
public class UserMetrics {

    private int count;

    private int remainingBudget;

    public UserMetrics(List<UserEntity> users) {
        count = users.size();

        remainingBudget = users.stream()
                .mapToInt(UserEntity::getBudget)
                .sum();
    }
}
