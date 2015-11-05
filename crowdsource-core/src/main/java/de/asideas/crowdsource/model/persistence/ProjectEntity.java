package de.asideas.crowdsource.model.persistence;

import de.asideas.crowdsource.enums.ProjectStatus;
import de.asideas.crowdsource.exceptions.InvalidRequestException;
import de.asideas.crowdsource.model.presentation.Pledge;
import de.asideas.crowdsource.model.presentation.project.Project;
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

import java.util.List;

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

    public PledgeEntity pledge(Pledge pledge, FinancingRoundEntity activeFinancingRound,
                               UserEntity pledgingUser, List<PledgeEntity> pledgesAlreadyDone) {

        if (this.status == ProjectStatus.FULLY_PLEDGED) {
            throw InvalidRequestException.projectAlreadyFullyPledged();
        }
        if (this.status != ProjectStatus.PUBLISHED) {
            throw InvalidRequestException.projectNotPublished();
        }
        if (pledge.getAmount() == 0) {
            throw InvalidRequestException.zeroPledgeNotValid();
        }
        if (pledge.getAmount() > pledgingUser.getBudget()) {
            throw InvalidRequestException.userBudgetExceeded();
        }
        if (pledgedAmountOfUser(pledgesAlreadyDone, pledgingUser) + pledge.getAmount() < 0) {
            throw InvalidRequestException.reversePledgeExceeded();
        }

        int newPledgedAmount = pledgedAmount(pledgesAlreadyDone) + pledge.getAmount();
        if (newPledgedAmount > this.pledgeGoal) {
            throw InvalidRequestException.pledgeGoalExceeded();
        }

        if (newPledgedAmount == this.pledgeGoal) {
            setStatus(ProjectStatus.FULLY_PLEDGED);
        }

        pledgingUser.accountPledge(pledge);

        PledgeEntity pledgeEntity = new PledgeEntity(this, pledgingUser, pledge, activeFinancingRound);
        return pledgeEntity;
    }

    public boolean pledgeGoalAchieved() {
        return this.status == ProjectStatus.FULLY_PLEDGED;
    }

    public int pledgedAmount(List<PledgeEntity> pledges) {
        return pledges.stream().mapToInt(PledgeEntity::getAmount).sum();
    }

    public long countBackers(List<PledgeEntity> pledges) {
        return pledges.stream().map(PledgeEntity::getUser).distinct().count();
    }

    public int pledgedAmountOfUser(List<PledgeEntity> pledges, UserEntity requestingUser) {
        if (requestingUser == null || pledges == null || pledges.isEmpty()) {
            return 0;
        }
        return pledges.stream().filter(p -> requestingUser.getId().equals(p.getUser().getId()))
                .mapToInt(PledgeEntity::getAmount).sum();
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
