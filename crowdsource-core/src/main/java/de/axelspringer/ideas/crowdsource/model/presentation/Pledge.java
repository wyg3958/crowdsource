package de.axelspringer.ideas.crowdsource.model.presentation;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor // needed for serialization
public class Pledge {

    @Min(1)
    private int amount;

    public Pledge(int amount) {
        this.amount = amount;
    }

}
