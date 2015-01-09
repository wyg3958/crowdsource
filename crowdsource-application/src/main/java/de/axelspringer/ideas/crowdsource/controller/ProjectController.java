package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.project.ProjectSummaryView;
import de.axelspringer.ideas.crowdsource.service.ProjectService;
import de.axelspringer.ideas.crowdsource.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    @JsonView(ProjectSummaryView.class)
    public List<Project> getProjects() {

        return projectService.getProjects();
    }

    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public Project getProject(@PathVariable String projectId) {

        return projectService.getProject(projectId);
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/project", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addProject(@RequestBody @Valid Project project, Principal principal) {

        UserEntity userEntity = userService.getUserByName(principal.getName());

        projectService.addProject(project, userEntity);
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping("/project/{projectId}/pledge")
    public void pledgeProject(@PathVariable String projectId, @RequestBody @Valid Pledge pledge, Principal principal) {

        UserEntity userEntity = userService.getUserByName(principal.getName());

        projectService.pledgeProject(projectId, userEntity, pledge);
    }
}
