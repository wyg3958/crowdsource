package de.asideas.crowdsource.domain.model;

import de.asideas.crowdsource.domain.exception.InvalidRequestException;
import de.asideas.crowdsource.domain.presentation.Pledge;
import de.asideas.crowdsource.domain.presentation.project.Project;
import de.asideas.crowdsource.domain.shared.ProjectStatus;
import de.asideas.crowdsource.security.Roles;
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
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

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

    /**
     * Allows pledging <code>this</code> project, using budget from the <code>pledgingUser</code> given.
     * Moreover negative pledges are supported by reducing investment for the project in which case the amount
     * is credited the <code>pledginUser</code>'s budget (only as much posssible as originally pledged by her).
     *
     * @param pledge             The amount to (positively or negatively) pledge <code>this</code>
     * @param pledgingUser       the user that wants to invest and whose balance is affected
     * @param pledgesAlreadyDone all investements that have been done so far for <code>this</code>
     * @return the value describing the [reduced] investment done
     */
    public PledgeEntity pledge(Pledge pledge, UserEntity pledgingUser, List<PledgeEntity> pledgesAlreadyDone) {

        if (this.financingRound == null || !this.financingRound.active()) {
            throw InvalidRequestException.noFinancingRoundCurrentlyActive();
        }
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

        return new PledgeEntity(this, pledgingUser, pledge, financingRound);
    }

    /**
     * Allows admin users to pledge <code>this</code> project after the last financing round using its remaining budget.
     * Thus, the user's budget is not debited or credited rather than <code>this</code>' financingRound's remaining budget.
     * Negative pledges are supported as well but only as much as was pledged by admin users on the terminated financing round.
     *
     * @param pledge                            the amount to [negatively] pledge
     * @param pledgingUser                      the admin user executing the pledge.
     * @param pledgesAlreadyDone                all investements that have been done so far for <code>this</code>
     * @param postRoundPledgableBudgetAvailable how much budget is available from financing round to be used for investments
     * @return the value describing the [reduced] investment done using budget from <code>this</code>' financing round
     */
    public PledgeEntity pledgeUsingPostRoundBudget(Pledge pledge, UserEntity pledgingUser, List<PledgeEntity> pledgesAlreadyDone, int postRoundPledgableBudgetAvailable) {
        Assert.isTrue(pledgingUser.getRoles().contains(Roles.ROLE_ADMIN), "pledgeUsingPostRoundBudget(..) called with non admin user: " + pledgingUser);
        Assert.notNull(this.financingRound, "FinancingRound must not be null; Project was: " + this);
        Assert.isTrue(this.financingRound.terminated(), "pledgeUsingPostRoundBudget(..) requires its financingRound to be terminated; Project was: " + this);

        if (!this.financingRound.getTerminationPostProcessingDone()) {
            throw InvalidRequestException.financingRoundNotPostProcessedYet();
        }

        if (this.status == ProjectStatus.FULLY_PLEDGED) {
            throw InvalidRequestException.projectAlreadyFullyPledged();
        }
        if (this.status != ProjectStatus.PUBLISHED) {
            throw InvalidRequestException.projectNotPublished();
        }
        if (pledge.getAmount() == 0) {
            throw InvalidRequestException.zeroPledgeNotValid();
        }
        if (pledge.getAmount() > postRoundPledgableBudgetAvailable) {
            throw InvalidRequestException.postRoundBudgetExceeded();
        }

        if (pledgedAmountPostRound(pledgesAlreadyDone) + pledge.getAmount() < 0) {
            throw InvalidRequestException.reversePledgeExceeded();
        }

        int newPledgedAmount = pledgedAmount(pledgesAlreadyDone) + pledge.getAmount();
        if (newPledgedAmount > this.pledgeGoal) {
            throw InvalidRequestException.pledgeGoalExceeded();
        }

        if (newPledgedAmount == this.pledgeGoal) {
            setStatus(ProjectStatus.FULLY_PLEDGED);
        }

        return new PledgeEntity(this, pledgingUser, pledge, financingRound);
    }

    /**
     * Modifies status of <code>this</code>.
     *
     * @param newStatus
     * @return whether an actual change of the status has taken place
     * @throws InvalidRequestException in case constraints are violated
     */
    public boolean modifyStatus(ProjectStatus newStatus) throws InvalidRequestException {
        if (this.status == newStatus) {
            return false;
        }

        if (ProjectStatus.FULLY_PLEDGED == this.status) {
            throw InvalidRequestException.projectAlreadyFullyPledged();
        }

        if (ProjectStatus.DEFERRED == newStatus) {
            if (this.financingRound != null && this.financingRound.active()) {
                throw InvalidRequestException.projectAlreadyInFinancingRound();
            }
            if (ProjectStatus.REJECTED == this.status) {
                throw InvalidRequestException.setToDeferredNotPossibleOnRejected();
            }
        }

        setStatus(newStatus);
        return true;
    }

    /**
     * Upon termination of its financing round the status is adapted accordingly as well as allocation of financing round.
     *
     * @param financingRound
     * @return whether something actually changed.
     */
    public boolean onFinancingRoundTerminated(FinancingRoundEntity financingRound) {
        Assert.notNull(financingRound);
        if (this.financingRound == null || !this.financingRound.getId().equals(financingRound.getId())) {
            return false;
        }

        if (this.status == ProjectStatus.DEFERRED) {
            modifyStatus(ProjectStatus.PUBLISHED);
            setFinancingRound(null);
            return true;
        }
        return false;
    }

    public boolean pledgeGoalAchieved() {
        return this.status == ProjectStatus.FULLY_PLEDGED;
    }

    public int pledgedAmount(List<PledgeEntity> pledges) {
        return pledges.stream().mapToInt(PledgeEntity::getAmount).sum();
    }

    public long countBackers(List<PledgeEntity> pledges) {
        Optional<Integer> backers = pledges.stream()
                .collect(groupingBy(PledgeEntity::getUser, reducing(new PledgeEntity(), PledgeEntity::add)))
                .entrySet().stream()
                .map(pledgeSumByUser -> (pledgeSumByUser.getValue().getAmount() == 0 ? 0 : 1)).reduce((a, b) -> a + b);

        return backers.orElse(0);
    }

    public int pledgedAmountOfUser(List<PledgeEntity> pledges, UserEntity requestingUser) {
        if (requestingUser == null || pledges == null || pledges.isEmpty()) {
            return 0;
        }
        return pledges.stream().filter(p -> requestingUser.getId().equals(p.getUser().getId()))
                .mapToInt(PledgeEntity::getAmount).sum();
    }

    public int pledgedAmountPostRound(List<PledgeEntity> pledges) {
        if (pledges == null || pledges.isEmpty() || this.financingRound == null || !this.financingRound.terminated()) {
            return 0;
        }
        return pledges.stream()
                .filter(p -> p.getCreatedDate() != null && p.getCreatedDate().isAfter(financingRound.getEndDate()))
                .mapToInt(PledgeEntity::getAmount)
                .sum();
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
