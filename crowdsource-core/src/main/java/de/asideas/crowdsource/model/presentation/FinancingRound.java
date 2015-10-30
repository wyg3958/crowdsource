package de.asideas.crowdsource.model.presentation;

import com.fasterxml.jackson.annotation.JsonView;
import de.asideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.asideas.crowdsource.model.presentation.project.PublicFinancingRoundInformationView;
import de.asideas.crowdsource.util.validation.financinground.FinancingRoundNotColliding;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    public FinancingRound() {
    }

    public String getId() {
        return this.id;
    }

    public DateTime getStartDate() {
        return this.startDate;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

    public Integer getBudget() {
        return this.budget;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
