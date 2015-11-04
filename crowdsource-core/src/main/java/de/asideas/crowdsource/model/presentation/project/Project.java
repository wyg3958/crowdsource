package de.asideas.crowdsource.model.presentation.project;

import com.fasterxml.jackson.annotation.JsonView;
import de.asideas.crowdsource.enums.ProjectStatus;
import de.asideas.crowdsource.model.persistence.PledgeEntity;
import de.asideas.crowdsource.model.persistence.ProjectEntity;
import de.asideas.crowdsource.model.persistence.UserEntity;
import de.asideas.crowdsource.model.presentation.user.ProjectCreator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

// needed for serialization
public class Project {

    // no validation here on purpose, as this is only filled on response and ignored in request.
    @JsonView(ProjectSummaryView.class)
    private String id;

    // no validation here on purpose, as this is only filled on response and ignored in request
    @NotNull(groups = {UpdateProject.class})
    @JsonView(ProjectSummaryView.class)
    private ProjectStatus status;

    @NotEmpty
    @JsonView(ProjectSummaryView.class)
    private String title;

    @NotEmpty
    @JsonView(ProjectSummaryView.class)
    private String shortDescription;

    @NotEmpty
    private String description;

    @Min(1)
    @JsonView(ProjectSummaryView.class)
    private int pledgeGoal;

    // no validation here on purpose, as this is only filled on response and ignored in request
    @JsonView(ProjectSummaryView.class)
    private int pledgedAmount;

    // no validation here on purpose, as this is only filled on response and ignored in request
    @JsonView(ProjectSummaryView.class)
    private long backers;

    // no validation here on purpose, as this is only filled on response and ignored in request. Ideally,
    // this is filled on request too and denied if a normal user tries to create a project for someone else
    @JsonView(ProjectSummaryView.class)
    private ProjectCreator creator;

    // no validation here on purpose, as this is only filled on response and ignored in request
    @JsonView(ProjectSummaryView.class)
    private Date lastModifiedDate;

    @JsonView(ProjectSummaryView.class)
    private int pledgedAmountByRequestingUser;

    public Project(ProjectEntity projectEntity, List<PledgeEntity> pledges, UserEntity requestingUser) {
        this.id = projectEntity.getId();
        this.status = projectEntity.getStatus();
        this.title = projectEntity.getTitle();
        this.shortDescription = projectEntity.getShortDescription();
        this.description = projectEntity.getDescription();
        this.pledgeGoal = projectEntity.getPledgeGoal();
        this.lastModifiedDate = projectEntity.getLastModifiedDate() != null ? projectEntity.getLastModifiedDate().toDate() : null;

        this.pledgedAmount = projectEntity.pledgedAmount(pledges);
        this.backers = projectEntity.countBackers(pledges);
        this.pledgedAmountByRequestingUser = projectEntity.pledgedAmountOfUser(pledges, requestingUser);

        this.creator = new ProjectCreator(projectEntity.getCreator());
    }

    public Project() {
    }

    public String getId() {
        return this.id;
    }

    public ProjectStatus getStatus() {
        return this.status;
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

    public int getPledgeGoal() {
        return this.pledgeGoal;
    }

    public int getPledgedAmount() {
        return this.pledgedAmount;
    }

    public long getBackers() {
        return this.backers;
    }

    public ProjectCreator getCreator() {
        return this.creator;
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
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

    public void setPledgeGoal(int pledgeGoal) {
        this.pledgeGoal = pledgeGoal;
    }

    public void setPledgedAmount(int pledgedAmount) {
        this.pledgedAmount = pledgedAmount;
    }

    public void setBackers(long backers) {
        this.backers = backers;
    }

    public void setCreator(ProjectCreator creator) {
        this.creator = creator;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getPledgedAmountByRequestingUser() {
        return pledgedAmountByRequestingUser;
    }

    public void setPledgedAmountByRequestingUser(int pledgedAmountByRequestingUser) {
        this.pledgedAmountByRequestingUser = pledgedAmountByRequestingUser;
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

    /**
     * Used as Marker for {@link de.asideas.crowdsource.model.presentation.project.Project} Validation on update requests
     */
    public interface UpdateProject {
    }
}
