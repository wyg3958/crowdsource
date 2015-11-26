package de.asideas.crowdsource.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.asideas.crowdsource.domain.exception.ResourceNotFoundException;
import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import de.asideas.crowdsource.domain.presentation.FinancingRound;
import de.asideas.crowdsource.domain.presentation.project.PublicFinancingRoundInformationView;
import de.asideas.crowdsource.repository.FinancingRoundRepository;
import de.asideas.crowdsource.security.Roles;
import de.asideas.crowdsource.service.FinancingRoundService;
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
    private FinancingRoundService financingRoundService;

    @Secured(Roles.ROLE_ADMIN)
    @RequestMapping(value = "/financingrounds", method = RequestMethod.GET)
    public List<FinancingRound> allFinancingRounds() {
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
    @RequestMapping(value = "financinground", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FinancingRound startFinancingRound(@Valid @RequestBody FinancingRound financingRound) {
        return financingRoundService.startNewFinancingRound(financingRound);
    }

    @Secured(Roles.ROLE_ADMIN)
    @RequestMapping(value = "financinground/{id}/cancel", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public FinancingRound stopFinancingRound(@PathVariable String id) {
        return financingRoundService.stopFinancingRound(id);
    }

}
