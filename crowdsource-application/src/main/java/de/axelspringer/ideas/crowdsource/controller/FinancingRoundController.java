package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Secured(Roles.ROLE_ADMIN)
public class FinancingRoundController {

    @Autowired
    private FinancingRoundRepository financingRoundRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/financingrounds", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FinancingRound> financingRounds() {

        return financingRoundRepository
                .findAll()
                .stream()
                .map(FinancingRound::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "financinground", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FinancingRound startFinancingRound(@Valid @RequestBody FinancingRound financingRound) {

        //the round should end at the end of the day
        final DateTime end = financingRound.getEnd();
        final DateTime modifiedEnd = new DateTime(end.getYear(), end.getMonthOfYear(), end.getDayOfMonth(), 23, 59, 59);
        financingRound.setEnd(modifiedEnd);

        // flush user budget and set new budget
        final List<UserEntity> userEntities = userRepository.findAll();
        final int budgetPerUser = budgetPerUser(financingRound.getValue(), userEntities.size());
        userEntities.forEach(userEntity -> {
            userEntity.setBudget(budgetPerUser);
            userRepository.save(userEntity);
        });

        // create round
        final FinancingRoundEntity financingRoundEntity = new FinancingRoundEntity();
        financingRoundEntity.setStartDate(new DateTime());
        financingRoundEntity.setEndDate(financingRound.getEnd());
        financingRoundEntity.setValue(financingRound.getValue());
        financingRoundRepository.save(financingRoundEntity);

        return new FinancingRound(financingRoundEntity);
    }

    @RequestMapping(value = "financinground/{id}/cancel", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public FinancingRound stopFinancingRound(@PathVariable String id) {

        // find entity
        final FinancingRoundEntity financingRoundEntity = financingRoundRepository.findOne(id);
        financingRoundEntity.setEndDate(new DateTime());
        financingRoundRepository.save(financingRoundEntity);
        return new FinancingRound(financingRoundEntity);
    }


    int budgetPerUser(int financingRoundBudget, int userCount) {

        return userCount < 1 ? 0 : Math.floorDiv(financingRoundBudget, userCount);
    }
}
