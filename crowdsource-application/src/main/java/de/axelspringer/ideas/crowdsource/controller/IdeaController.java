package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.model.persistence.IdeaEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.idea.IdeaStorage;
import de.axelspringer.ideas.crowdsource.repository.IdeaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/idea", consumes = MediaType.APPLICATION_JSON_VALUE)
public class IdeaController {

    @Autowired
    private IdeaRepository ideaRepository;


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void saveIdea(@RequestBody IdeaStorage ideaStorage){

        IdeaEntity ideaEntity = new IdeaEntity();
        ideaEntity.setFullDescription(ideaStorage.getFullDescription());
        ideaEntity.setShortDescription(ideaStorage.getShortDescription());
        ideaEntity.setTitle(ideaStorage.getTitle());
        ideaRepository.save(ideaEntity);

        log.debug("Idea saved: {}", ideaEntity);
    }
}
