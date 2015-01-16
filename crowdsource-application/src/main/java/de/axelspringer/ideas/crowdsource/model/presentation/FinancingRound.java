package de.axelspringer.ideas.crowdsource.model.presentation;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.PublicFinancingRoundInformationView;
import de.axelspringer.ideas.crowdsource.util.validation.financinground.FinancingRoundNotColliding;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
@Setter
@FinancingRoundNotColliding
public class FinancingRound {

    @JsonView(PublicFinancingRoundInformationView.class)
    private String id;

    @JsonView(PublicFinancingRoundInformationView.class)
    private DateTime startDate = new DateTime();

    @NotNull
    @Future(message = "end-date-in-future")
    @JsonView(PublicFinancingRoundInformationView.class)
    private DateTime endDate;

    @NotNull
    @Min(value = 1l, message = "at-least-one-dollar")
    private Integer budget;

    @JsonView(PublicFinancingRoundInformationView.class)
    private boolean active;

    public FinancingRound(FinancingRoundEntity financingRoundEntity) {
        startDate = financingRoundEntity.getStartDate();
        endDate = financingRoundEntity.getEndDate();
        budget = financingRoundEntity.getBudget();
        id = financingRoundEntity.getId();
        long now = DateTime.now().getMillis();
        active = startDate.getMillis() < now && endDate.getMillis() > now;
    }
}
