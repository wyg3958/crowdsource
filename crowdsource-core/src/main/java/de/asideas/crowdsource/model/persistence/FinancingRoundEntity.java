package de.asideas.crowdsource.model.persistence;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

// for serialization
@Document(collection = "financerounds")
public class FinancingRoundEntity {

    @Id
    private String id;

    private DateTime startDate;

    private DateTime endDate;

    /**
     * The amount of money available in the financing round
     */
    private Integer budget;

    private Integer budgetPerUser;

    private Integer userCount;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public FinancingRoundEntity() {
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

    public Integer getBudgetPerUser() {
        return this.budgetPerUser;
    }

    public Integer getUserCount() {
        return this.userCount;
    }

    public DateTime getCreatedDate() {
        return this.createdDate;
    }

    public DateTime getLastModifiedDate() {
        return this.lastModifiedDate;
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

    public void setBudgetPerUser(Integer budgetPerUser) {
        this.budgetPerUser = budgetPerUser;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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
