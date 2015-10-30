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
