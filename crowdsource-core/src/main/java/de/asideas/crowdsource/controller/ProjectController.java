package de.asideas.crowdsource.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.asideas.crowdsource.config.security.Roles;
import de.asideas.crowdsource.enums.ProjectStatus;
import de.asideas.crowdsource.exceptions.ForbiddenException;
import de.asideas.crowdsource.model.persistence.UserEntity;
import de.asideas.crowdsource.model.presentation.Pledge;
import de.asideas.crowdsource.model.presentation.project.Project;
import de.asideas.crowdsource.model.presentation.project.ProjectSummaryView;
import de.asideas.crowdsource.service.ProjectService;
import de.asideas.crowdsource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

import static de.asideas.crowdsource.enums.ProjectStatus.FULLY_PLEDGED;
import static de.asideas.crowdsource.enums.ProjectStatus.PUBLISHED;

@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Secured({Roles.ROLE_TRUSTED_ANONYMOUS, Roles.ROLE_USER})
    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    @JsonView(ProjectSummaryView.class)
    public List<Project> getProjects(Authentication auth) {

        final List<Project> projects = projectService.getProjects();
        // filter projects. only return projects that are published, fully pledged or created by the requesting user (or if requestor is admin)
        return projects.stream().filter(project -> mayViewProjectFilter(project, auth)).collect(Collectors.toList());
    }

    @Secured({Roles.ROLE_TRUSTED_ANONYMOUS, Roles.ROLE_USER})
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.GET)
    public Project getProject(@PathVariable String projectId, Authentication auth) {

        final Project project = projectService.getProject(projectId);
        if (!mayViewProjectFilter(project, auth)) {
            throw new ForbiddenException();
        }
        return project;
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/project", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Project addProject(@RequestBody @Valid Project project, Principal principal) {

        UserEntity userEntity = userService.getUserByEmail(principal.getName());
        return projectService.addProject(project, userEntity);
    }

    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/project/{projectId}/pledge", method = RequestMethod.POST)
    public void pledgeProject(@PathVariable String projectId, @RequestBody @Valid Pledge pledge, Principal principal) {

        UserEntity userEntity = userService.getUserByEmail(principal.getName());
        projectService.pledge(projectId, userEntity, pledge);
    }

    @Secured(Roles.ROLE_ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.PATCH)
    public Project updateProject(@PathVariable("projectId") String projectId, @RequestBody @Validated(Project.UpdateProject.class) Project projectWithUpdateData) {

        return projectService.updateProject(projectId, projectWithUpdateData);
    }


    private boolean mayViewProjectFilter(Project project, Authentication auth) {

        // fully pledged and published are always visible
        final ProjectStatus status = project.getStatus();
        if (status == FULLY_PLEDGED || status == PUBLISHED) {
            return true;
        }

        if (auth == null) {
            return false;
        }

        // admins may do everything
        for (GrantedAuthority grantedAuthority : auth.getAuthorities()) {
            if (Roles.ROLE_ADMIN.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        // the creator always may see his project
        return project.getCreator().getEmail().equals(auth.getName());
    }
}
