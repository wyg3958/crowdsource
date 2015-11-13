package de.asideas.crowdsource.domain.presentation.project;

import de.asideas.crowdsource.domain.shared.ProjectStatus;

import javax.validation.constraints.NotNull;

/**
 * A Json Wrapper
 */
public class ProjectStatusUpdate {

    @NotNull
    public ProjectStatus status;

    public ProjectStatusUpdate() {
    }
    public ProjectStatusUpdate(ProjectStatus status) {
        this.status = status;
    }
}
