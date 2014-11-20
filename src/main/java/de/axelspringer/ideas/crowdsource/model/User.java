package de.axelspringer.ideas.crowdsource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@RequiredArgsConstructor
public class User {

    @Id
    private String id;

    private final String username;

    private final String password;

    private final List<String> roles;
}
