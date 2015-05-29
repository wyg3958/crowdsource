package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

@Data
// needed for serialization
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String activationToken;

    private List<String> roles = Arrays.asList(Roles.ROLE_USER);

    private boolean activated = false;

    private int budget = 0;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public UserEntity(String email) {
        this(email, null);
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void reduceBudget(int reductionAmount) {
        if ((budget - reductionAmount) < 0) {
            throw new IllegalArgumentException("User budget may not drop below 0");
        }

        budget -= reductionAmount;
    }
}
