package de.axelspringer.ideas.crowdsource.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/idea", consumes = MediaType.APPLICATION_JSON_VALUE)
public class IdeaController {
}
