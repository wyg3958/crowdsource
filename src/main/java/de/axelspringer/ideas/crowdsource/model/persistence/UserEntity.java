package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE) // required for serialization
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

    public UserEntity(String email) {
        this(email, null);
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;

        generateNewActivationToken();
    }

    public void generateNewActivationToken() {
        // TODO: maybe hash this token with the email address?
        UUID uuid = UUID.randomUUID();

        this.activationToken = uuid.toString();
    }
}
