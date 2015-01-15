package de.axelspringer.ideas.crowdsource.model.presentation;

import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.util.validation.financinground.FinancingRoundNotColliding;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;

@NoArgsConstructor
@Data
@Setter
@FinancingRoundNotColliding
public class FinancingRound {

    private DateTime start = new DateTime();

    @Future(message = "end-date-in-future")
    private DateTime end;

    @Min(value = 1l, message = "at-least-one-dollar")
    private Integer value;

    private String id;

    private boolean active;

    public FinancingRound(FinancingRoundEntity financingRoundEntity) {
        start = financingRoundEntity.getStartDate();
        end = financingRoundEntity.getEndDate();
        value = financingRoundEntity.getValue();
        id = financingRoundEntity.getId();
        long now = DateTime.now().getMillis();
        active = start.getMillis() < now && end.getMillis() > now;
    }
}
