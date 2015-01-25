package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.exceptions.NotAuthorizedException;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.project.ProjectSummaryView;
import de.axelspringer.ideas.crowdsource.model.presentation.project.UpdateProject;
import de.axelspringer.ideas.crowdsource.service.ProjectService;
import de.axelspringer.ideas.crowdsource.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static de.axelspringer.ideas.crowdsource.enums.ProjectStatus.FULLY_PLEDGED;
import static de.axelspringer.ideas.crowdsource.enums.ProjectStatus.PUBLISHED;

@Slf4j
@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Secured({Roles.ROLE_TRUSTED_ANONYMOUS, Roles.ROLE_USER})
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    @JsonView(ProjectSummaryView.class)
    public List<Project> getProjects(Principal principal) {

        final List<Project> projects = projectService.getProjects();
        // filter projects. only return projects that are published, fully pledged or created by the requesting user (or if requestor is admin)
        return projects.stream().filter(project -> mayViewProjectFilter(project, principal)).collect(Collectors.toList());
    }

    @Secured({Roles.ROLE_TRUSTED_ANONYMOUS, Roles.ROLE_USER})
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public Project getProject(@PathVariable String projectId, Principal principal) {

        final Project project = projectService.getProject(projectId);
        if (!mayViewProjectFilter(project, principal)) {
            throw new NotAuthorizedException("you may not get information about this project.");
        }
        return project;
    }

    private boolean mayViewProjectFilter(Project project, Principal requestor) {

        // fully pledged and published are always visible
        final ProjectStatus status = project.getStatus();
        if (status == FULLY_PLEDGED || status == PUBLISHED) {
            return true;
        }

        // try find a user for the principal
        final UserEntity userEntity = userService.getUserByName(requestor.getName());

        // admins may do everything
        if (userEntity != null && userEntity.getRoles().contains(Roles.ROLE_ADMIN)) {
            return true;
        }

        // the creator always may see his project
        final String requestorEmail = userEntity == null ? "foo" : userEntity.getEmail();
        return project.getCreator().getEmail().equals(requestorEmail);
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/project", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Project addProject(@RequestBody @Valid Project project, Principal principal) {

        UserEntity userEntity = userService.getUserByName(principal.getName());
        return projectService.addProject(project, userEntity);
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/project/{projectId}/pledge", method = RequestMethod.POST)
    public void pledgeProject(@PathVariable String projectId, @RequestBody @Valid Pledge pledge, Principal principal) {

        UserEntity userEntity = userService.getUserByName(principal.getName());
        projectService.pledge(projectId, userEntity, pledge);
    }

    @Secured(Roles.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.PATCH)
    public Project updateProject(@PathVariable("projectId") String projectId, @RequestBody @Validated(UpdateProject.class) Project projectWithUpdateData) {

        return projectService.updateProject(projectId, projectWithUpdateData);
    }
}
