package de.asideas.crowdsource.model.persistence;

import de.asideas.crowdsource.model.presentation.Pledge;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

// needed for serialization
@Document(collection = "pledges")
public class PledgeEntity {

    @Id
    private String id;
    @DBRef
    private ProjectEntity project;
    @DBRef
    private UserEntity user;
    @DBRef
    private FinancingRoundEntity financingRound;

    private int amount;

    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public PledgeEntity(ProjectEntity projectEntity, UserEntity userEntity, Pledge pledge, FinancingRoundEntity financingRoundEntity) {
        this.project = projectEntity;
        this.user = userEntity;
        this.financingRound = financingRoundEntity;
        this.amount = pledge.getAmount();
    }
    public PledgeEntity() {
    }

    /**
     * Adds the <code>other</code> pledge's amount to <code>this</code> returning a new object, leaving this as is.
     * In case the <code>users</code> differ it will be set to <code>null</code>.
     * The <code>id</code> will always be set to null.
     * In case <code>financingRound</code>s differ, it will be set to <code>null</code>, set otherwise
     * The <code>createdDate</code> and <code>lastModifiedDate</code> will be set to <code>null</code>
     * The <code>project</code> will be <code>null</code> if different, set otherwise
     * @param other the other to add
     * @return the summed pledge; if <code>other</code> is <code>null</code> will return a copy of <code>this</code>
     */
    public PledgeEntity add(PledgeEntity other){
        if(other == null) {
            PledgeEntity res = new PledgeEntity();
            res.setUser(user);
            res.setProject(project);
            res.setFinancingRound(financingRound);
            res.setAmount(amount);
            return res;
        }

        PledgeEntity res = new PledgeEntity();
        res.setAmount(this.amount + other.amount);
        if(this.user == null || this.user.equals(other.user)) {
            res.user = other.user;
        }

        if(this.financingRound != null && this.financingRound.equals(other.financingRound)) {
            res.financingRound = this.financingRound;
        }
        if(this.project != null && this.project.equals(other.project)) {
            res.project = this.project;
        }
        return res;
    }

    public String getId() {
        return this.id;
    }

    public ProjectEntity getProject() {
        return this.project;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public FinancingRoundEntity getFinancingRound() {
        return this.financingRound;
    }

    public int getAmount() {
        return this.amount;
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

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setFinancingRound(FinancingRoundEntity financingRound) {
        this.financingRound = financingRound;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
