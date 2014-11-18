package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.model.Hello;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(method = RequestMethod.GET, value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public Hello hello() {

        final Hello hello = new Hello();
        hello.setMessage("hi");
        return hello;
    }
}
