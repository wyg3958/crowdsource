package de.axelspringer.ideas.crowdsource.model.persistence;

import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

// needed for serialization
@Document(collection = "projects")
public class ProjectEntity {

    @Id
    private String id;

    @DBRef
    private UserEntity creator;

    @DBRef
    private FinancingRoundEntity financingRound;

    private String title;

    private String shortDescription;

    private String description;

    private ProjectStatus status;

    private int pledgeGoal;

    @Indexed // since we order by this field
    @CreatedDate
    private DateTime createdDate;

    @LastModifiedDate
    private DateTime lastModifiedDate;

    public ProjectEntity(UserEntity creator, Project project, FinancingRoundEntity financingRound) {
        this.creator = creator;
        this.financingRound = financingRound;
        this.title = project.getTitle();
        this.shortDescription = project.getShortDescription();
        this.description = project.getDescription();
        this.pledgeGoal = project.getPledgeGoal();
        this.status = ProjectStatus.PROPOSED;
    }

    public ProjectEntity() {
    }

    public String getId() {
        return this.id;
    }

    public UserEntity getCreator() {
        return this.creator;
    }

    public FinancingRoundEntity getFinancingRound() {
        return this.financingRound;
    }

    public String getTitle() {
        return this.title;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public String getDescription() {
        return this.description;
    }

    public ProjectStatus getStatus() {
        return this.status;
    }

    public int getPledgeGoal() {
        return this.pledgeGoal;
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

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public void setFinancingRound(FinancingRoundEntity financingRound) {
        this.financingRound = financingRound;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public void setPledgeGoal(int pledgeGoal) {
        this.pledgeGoal = pledgeGoal;
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
