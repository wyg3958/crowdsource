package de.asideas.crowdsource.domain.model;

import de.asideas.crowdsource.domain.presentation.FinancingRound;
import de.asideas.crowdsource.domain.shared.ProjectStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

// for serialization
@Document(collection = "financerounds")
public class FinancingRoundEntity {

    private static final Logger log = LoggerFactory.getLogger(FinancingRoundEntity.class);

    @Id
    private String id;

    private DateTime startDate;

    private DateTime endDate;

    /**
     * The amount of money available in the financing round
     */
    private Integer budget;

    /**
     * The amount of money left after round has been terminated; thus the amount of money users did not spend on pledging projects during
     * official lifetime of that round. It is set during post processing of financing rounds and eventually can be used for pledging projects
     * by admins. When admin users pledge after termination of the round the pledging amounts are subtracted from this member.
     */
    private Integer budgetRemainingAfterRound = 0;

    private Integer budgetPerUser;

    private Integer userCount;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    private Boolean terminationPostProcessingDone = false;

    public FinancingRoundEntity() {
    }


    /**
     * Factory for a new financing round that immediately will be in active state
     * @param creationCmd
     * @return
     */
    public static FinancingRoundEntity newFinancingRound(FinancingRound creationCmd, int userCount) {
        FinancingRoundEntity res = new FinancingRoundEntity();
        res.setStartDate(new DateTime());
        res.setEndDate(creationCmd.getEndDate());
        res.setBudget(creationCmd.getBudget());
        res.setUserCount(userCount);
        res.setBudgetPerUser(res.calculateBudgetPerUser());
        return res;
    }

    public boolean projectEligibleForRound(ProjectEntity project) {
        return ProjectStatus.FULLY_PLEDGED != project.getStatus();
    }

    public void stopFinancingRound() {
        this.terminationPostProcessingDone = false;
        this.setEndDate(new DateTime());
    }

    /**
     * @return Whether <code>this</code> is currently active
     */
    public boolean active(){
        long now = DateTime.now().getMillis();
        return startDate.getMillis() < now && endDate.getMillis() > now;
    }

    /**
     * @return whether <code>this</code> is terminated.
     */
    public boolean terminated(){
        long now = DateTime.now().getMillis();
        return endDate.getMillis() <= now;
    }

    public boolean terminationPostProcessingRequiredNow(){
         return terminated() && !this.terminationPostProcessingDone;
    }

    /**
     * Calculates and initializes the remaining budget that was not pledget by users during <code>this</code> active round
     * @param pledgeAmountByUsers the total amount pledged by users during active financing round
     */
    public void initBudgetRemainingAfterRound(int pledgeAmountByUsers){
        if(!this.terminated()) {
            throw new IllegalStateException("Cannot initialize remaining budget on not yet terminated financing round: " + this);
        }
        int budgetRemainingAfterRound = getBudget() - pledgeAmountByUsers;
        if(budgetRemainingAfterRound < 0){
            log.warn("It seems, within this financing round there were more pledges done than budget available; Setting remaining budget to 0; " +
                    "The pledge amount above budget is: {}; FinancingRound was: {}", budgetRemainingAfterRound, this);
            budgetRemainingAfterRound = 0;
        }
        setBudgetRemainingAfterRound(budgetRemainingAfterRound);
    }

    Integer calculateBudgetPerUser() {
        Assert.notNull(this.userCount);
        Assert.notNull(this.budget);
        return this.userCount < 1 ? 0 : Math.floorDiv(this.budget, this.userCount);
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

    public Boolean getTerminationPostProcessingDone() {
        return terminationPostProcessingDone;
    }

    public Integer getBudgetRemainingAfterRound() {
        return budgetRemainingAfterRound;
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

    public void setTerminationPostProcessingDone(Boolean terminationPostProcessingDone) {
        this.terminationPostProcessingDone = terminationPostProcessingDone;
    }

    public void setBudgetRemainingAfterRound(Integer budgetRemainingAfterRound) {
        this.budgetRemainingAfterRound = budgetRemainingAfterRound;
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
