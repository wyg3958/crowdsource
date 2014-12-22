package de.axelspringer.ideas.crowdsource.model.presentation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // needed for serialization
public class Pledge {

    private int amount;

}
