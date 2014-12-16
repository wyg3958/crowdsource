package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.ProjectListItem;
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
    public List<ProjectListItem> getProjects() {

        final List<ProjectEntity> projects = projectRepository.findByPublicationStatus(PublicationStatus.PUBLISHED);
        final List<ProjectListItem> items = projects.stream().map(ProjectListItem::new).collect(toList());

        return items;
    }
}
