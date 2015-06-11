package de.axelspringer.ideas.crowdsource.model.presentation.user;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.ProjectSummaryView;
import de.axelspringer.ideas.crowdsource.util.UserHelper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

// required for serialization
public class ProjectCreator {

    private String id;

    @JsonView(ProjectSummaryView.class)
    private String name;

    @JsonView(ProjectSummaryView.class)
    private String email;

    public ProjectCreator(UserEntity user) {
        this.id = user.getId();
        this.name = UserHelper.determineNameFromEmail(user.getEmail());
        this.email = user.getEmail();
    }

    public ProjectCreator() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
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
