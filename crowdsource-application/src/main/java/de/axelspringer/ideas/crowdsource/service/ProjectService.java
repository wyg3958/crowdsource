package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;

import java.util.List;

public interface ProjectService {
    Project getProject(String projectId);

    List<Project> getProjects();

    void addProject(Project project, UserEntity userEntity);

    void pledgeProject(String projectId, UserEntity userEntity, Pledge pledge);
}
