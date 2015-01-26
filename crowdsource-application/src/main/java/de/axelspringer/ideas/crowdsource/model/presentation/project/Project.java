package de.axelspringer.ideas.crowdsource.model.presentation.project;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.user.ProjectCreator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor // needed for serialization
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
    private DateTime lastModifiedDate;

    public Project(ProjectEntity projectEntity, List<PledgeEntity> pledges) {
        this.id = projectEntity.getId();
        this.status = projectEntity.getStatus();
        this.title = projectEntity.getTitle();
        this.shortDescription = projectEntity.getShortDescription();
        this.description = projectEntity.getDescription();
        this.pledgeGoal = projectEntity.getPledgeGoal();
        this.lastModifiedDate = projectEntity.getLastModifiedDate();

        this.pledgedAmount = pledges.stream().mapToInt(PledgeEntity::getAmount).sum();
        this.backers = pledges.stream().map(PledgeEntity::getUser).distinct().count();

        this.creator = new ProjectCreator(projectEntity.getCreator());
    }

    /**
     * Used as Marker for {@link de.axelspringer.ideas.crowdsource.model.presentation.project.Project} Validation on update requests
     */
    public interface UpdateProject {
    }
}
