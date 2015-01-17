package de.axelspringer.ideas.crowdsource.model.presentation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private DateTime created;

    private String userName;

    @NotNull
    @Length(min = 20, max = 1000, message = "length-constraint-respected")
    private String comment;
}
