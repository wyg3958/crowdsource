package de.axelspringer.ideas.crowdsource.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(method = RequestMethod.GET, value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public Hello hello() {
        return new Hello();
    }

    public class Hello {
        final String message = "hi";

        public String getMessage() {
            return message;
        }
    }
}
