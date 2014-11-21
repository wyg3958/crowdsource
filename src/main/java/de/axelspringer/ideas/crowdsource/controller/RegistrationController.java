package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.model.MongoResponse;
import de.axelspringer.ideas.crowdsource.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private MongoOperations mongoOperations;

    @RequestMapping(method = RequestMethod.POST, value = "/saveUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public MongoResponse saveUser(@RequestParam("email") String email, @RequestParam("password") String password) {

        Query query = new Query(Criteria.where("email").is(email));

        User savedUser = mongoOperations.findOne(query, User.class);

        if (savedUser != null) {
            return new MongoResponse(1, "User not saved (already exists)");
        }

        User user = new User(email, password);

        mongoOperations.save(user);
        return new MongoResponse(0, "User saved");

    }

    @RequestMapping(method = RequestMethod.POST, value = "/deleteUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public MongoResponse saveUser(@RequestParam("email") String email) {

        Query query = new Query(Criteria.where("email").is(email));

        User userToDelete = mongoOperations.findOne(query, User.class);

        if (userToDelete == null) {
            return new MongoResponse(1, "User not found");
        }

        mongoOperations.remove(query, User.class);
        userToDelete = mongoOperations.findOne(query, User.class);

        if (userToDelete != null) {
            return new MongoResponse(1, "Deletion failed (user still in DB)");
        }

        return new MongoResponse(0, "User deleted");
    }
}
