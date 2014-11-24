package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.UserRepository;
import de.axelspringer.ideas.crowdsource.model.MongoResponse;
import de.axelspringer.ideas.crowdsource.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    public static final String EMAIL = "email";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @RequestMapping(method = RequestMethod.POST, value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public MongoResponse saveUser(@RequestParam(value = EMAIL, required = true) String email) {

        if (StringUtils.isEmpty(email)) {
            return new MongoResponse(1, "User email must be given.");
        }

        final List<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.size() > 0) {
            return new MongoResponse(1, "User not saved (already exists)");
        }

        User user = new User(email, null);
        userRepository.save(user);

        return new MongoResponse(0, "User saved");

    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public MongoResponse deleteUser(@RequestParam(value = EMAIL, required = true) String email) {

        if (StringUtils.isEmpty(email)) {
            return new MongoResponse(1, "User email must be given.");
        }

        List<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.size() == 0) {
            return new MongoResponse(1, "User not found");
        }

        User user = new User(email, null);
        userRepository.delete(user);

        byEmail = userRepository.findByEmail(email);
        if (byEmail.size() > 0) {
            return new MongoResponse(1, "Deletion failed (user still in DB)");
        }

        return new MongoResponse(0, "User deleted");
    }
}
