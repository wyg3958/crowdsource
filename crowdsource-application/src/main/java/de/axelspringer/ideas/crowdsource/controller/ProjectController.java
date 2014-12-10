package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.exceptions.NotAuthorizedException;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping(value = "/project", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;


    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void saveProject(@RequestBody @Valid Project project, Principal principal) {

        UserEntity userEntity = userRepository.findByEmail(principal.getName());
        if (userEntity == null) {
            throw new NotAuthorizedException("No user found with username " + principal.getName());
        }

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setDescription(project.getDescription());
        projectEntity.setShortDescription(project.getShortDescription());
        projectEntity.setTitle(project.getTitle());
        projectEntity.setUser(userEntity);
        projectRepository.save(projectEntity);

        log.debug("Project saved: {}", projectEntity);
    }
}
