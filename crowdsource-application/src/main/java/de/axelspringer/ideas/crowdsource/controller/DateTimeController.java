package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.model.presentation.DateTimeWrapper;
import org.joda.time.DateTime;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/datetime")
public class DateTimeController {

    @Secured({ Roles.ROLE_TRUSTED_ANONYMOUS, Roles.ROLE_USER })
    @RequestMapping(method = RequestMethod.GET)
    public DateTimeWrapper getDatetime() {

        return new DateTimeWrapper(DateTime.now());
    }
}
