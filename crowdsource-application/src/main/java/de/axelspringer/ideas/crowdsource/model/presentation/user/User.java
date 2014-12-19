package de.axelspringer.ideas.crowdsource.model.presentation.user;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor // required for serialization
public class User {

    private String id;

    private String name;

    public User(UserEntity user) {
        this.id = user.getId();
        this.name = determineNameFromEmail(user.getEmail());
    }

    private static String determineNameFromEmail(String email) {
        if (email == null) {
            return null;
        }

        int atPos = email.indexOf('@');
        if (atPos < 1) {
            return null;
        }

        String localPart = email.substring(0, atPos);
        List<String> localParts = Arrays.asList(localPart.split("\\."));

        return localParts.stream()
                .map(s -> s.replaceAll("\\d+", ""))
                .map(StringUtils::capitalize)
                .collect(joining(" "));
    }
}
