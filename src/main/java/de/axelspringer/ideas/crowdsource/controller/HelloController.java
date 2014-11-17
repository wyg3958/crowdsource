package de.axelspringer.ideas.crowdsource.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private MongoOperations mongoOperations;

    @RequestMapping(method = RequestMethod.GET, value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public Hello hello() {
        return new Hello();
    }

    public class Hello {
        String message = "hi";

        public String getMessage() {
            final List<Hello> hellos = mongoOperations.find(new Query(), Hello.class);
            return message + " -> " + hellos.size();
        }
    }
}
