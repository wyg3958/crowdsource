package de.axelspringer.ideas.crowdsource.model;

import lombok.Data;
import lombok.experimental.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String email;

    private String password;

    private String activationToken;

    private List<String> roles;

    private boolean activated;
}
