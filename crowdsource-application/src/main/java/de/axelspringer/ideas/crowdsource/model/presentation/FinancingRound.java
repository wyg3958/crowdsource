package de.axelspringer.ideas.crowdsource.model.presentation;

import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.util.validation.financinground.FinancingRoundNotColliding;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;

@NoArgsConstructor
@Data
@FinancingRoundNotColliding
public class FinancingRound {

    private DateTime start;

    @Future
    private DateTime end;

    @Min(1l)
    private Integer value;

    public FinancingRound(FinancingRoundEntity financingRoundEntity) {
        start = financingRoundEntity.getStartDate();
        end = financingRoundEntity.getEndDate();
        value = financingRoundEntity.getValue();
    }
}
