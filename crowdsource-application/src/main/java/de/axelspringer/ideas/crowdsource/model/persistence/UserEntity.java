package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

@Data
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

    public UserEntity() {
    }

    public UserEntity(String email) {
        this(email, null);
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
