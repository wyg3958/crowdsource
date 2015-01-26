package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.model.presentation.project.PublicFinancingRoundInformationView;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FinancingRoundController {

    @Autowired
    private FinancingRoundRepository financingRoundRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Secured(Roles.ROLE_ADMIN)
    @RequestMapping(value = "/financingrounds", method = RequestMethod.GET)
    public List<FinancingRound> financingRounds() {

        return financingRoundRepository
                .findAll()
                .stream()
                .map(FinancingRound::new)
                .collect(Collectors.toList());
    }

    @JsonView(PublicFinancingRoundInformationView.class)
    @RequestMapping(value = "/financinground/active", method = RequestMethod.GET)
    @Secured({Roles.ROLE_TRUSTED_ANONYMOUS, Roles.ROLE_USER, Roles.ROLE_ADMIN})
    public FinancingRound getActive() {

        final FinancingRoundEntity financingRoundEntity = financingRoundRepository.findActive(DateTime.now());
        if (financingRoundEntity == null) {
            throw new ResourceNotFoundException();
        }

        return new FinancingRound(financingRoundEntity);
    }

    @Secured(Roles.ROLE_ADMIN)
    @RequestMapping(value = "financinground", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FinancingRound startFinancingRound(@Valid @RequestBody FinancingRound financingRound) {

        // flush user budget and set new budget
        final List<UserEntity> userEntities = userRepository.findAll();
        final int budgetPerUser = budgetPerUser(financingRound.getBudget(), userEntities.size());
        userEntities.forEach(userEntity -> {
            userEntity.setBudget(budgetPerUser);
            userRepository.save(userEntity);
        });

        // create round
        final FinancingRoundEntity financingRoundEntity = new FinancingRoundEntity();
        financingRoundEntity.setStartDate(new DateTime());
        financingRoundEntity.setEndDate(financingRound.getEndDate());
        financingRoundEntity.setBudget(financingRound.getBudget());
        financingRoundEntity.setBudgetPerUser(budgetPerUser);
        financingRoundEntity.setUserCount(userEntities.size());

        final FinancingRoundEntity savedFinancingRoundEntity = financingRoundRepository.save(financingRoundEntity);

        projectRepository.findAll().stream()
                .filter(p -> p.getStatus() != ProjectStatus.FULLY_PLEDGED)
                .forEach(project -> {
                    project.setFinancingRound(savedFinancingRoundEntity);
                    projectRepository.save(project);
                });

        return new FinancingRound(savedFinancingRoundEntity);
    }

    @Secured(Roles.ROLE_ADMIN)
    @RequestMapping(value = "financinground/{id}/cancel", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public FinancingRound stopFinancingRound(@PathVariable String id) {

        // find entity
        final FinancingRoundEntity financingRoundEntity = financingRoundRepository.findOne(id);

        if (financingRoundEntity == null) {
            throw new ResourceNotFoundException();
        }
        if (financingRoundEntity.getEndDate().isBeforeNow()) {
            throw InvalidRequestException.financingRoundAlreadyStopped();
        }

        financingRoundEntity.setEndDate(new DateTime());
        financingRoundRepository.save(financingRoundEntity);
        return new FinancingRound(financingRoundEntity);
    }


    int budgetPerUser(int financingRoundBudget, int userCount) {

        return userCount < 1 ? 0 : Math.floorDiv(financingRoundBudget, userCount);
    }
}
