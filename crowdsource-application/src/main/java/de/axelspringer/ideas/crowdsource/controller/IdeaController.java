package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.exceptions.NotAuthorizedException;
import de.axelspringer.ideas.crowdsource.model.persistence.IdeaEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.idea.IdeaStorage;
import de.axelspringer.ideas.crowdsource.repository.IdeaRepository;
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
@RequestMapping(value = "/idea", consumes = MediaType.APPLICATION_JSON_VALUE)
public class IdeaController {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;


    @Secured(Roles.ROLE_USER)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void saveIdea(@RequestBody @Valid IdeaStorage ideaStorage, Principal principal){

        UserEntity userEntity = userRepository.findByEmail(principal.getName());
        if (userEntity == null) {
            throw new NotAuthorizedException("No user found with username " + principal.getName());
        }

        IdeaEntity ideaEntity = new IdeaEntity();
        ideaEntity.setFullDescription(ideaStorage.getFullDescription());
        ideaEntity.setShortDescription(ideaStorage.getShortDescription());
        ideaEntity.setTitle(ideaStorage.getTitle());
        ideaEntity.setUser(userEntity);
        ideaRepository.save(ideaEntity);

        log.debug("Idea saved: {}", ideaEntity);
    }
}
