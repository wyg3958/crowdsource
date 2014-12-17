package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.project.ProjectSummaryView;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectsController {

    @Autowired
    private ProjectRepository projectRepository;

    @RequestMapping(method = RequestMethod.GET)
    @JsonView(ProjectSummaryView.class)
    public List<Project> getProjects() {

        final List<ProjectEntity> projects = projectRepository.findByPublicationStatus(PublicationStatus.PUBLISHED);
        return projects.stream().map(Project::new).collect(toList());
    }
}
