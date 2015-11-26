package de.asideas.crowdsource.service;

import de.asideas.crowdsource.domain.exception.InvalidRequestException;
import de.asideas.crowdsource.domain.exception.ResourceNotFoundException;
import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import de.asideas.crowdsource.domain.model.UserEntity;
import de.asideas.crowdsource.domain.presentation.FinancingRound;
import de.asideas.crowdsource.domain.service.financinground.FinancingRoundPostProcessor;
import de.asideas.crowdsource.repository.FinancingRoundRepository;
import de.asideas.crowdsource.repository.ProjectRepository;
import de.asideas.crowdsource.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class FinancingRoundService implements ApplicationListener<ContextRefreshedEvent>{


    private static final Logger log = LoggerFactory.getLogger(FinancingRoundService.class);

    private UserRepository userRepository;
    private UserService userService;
    private FinancingRoundRepository financingRoundRepository;
    private ProjectRepository projectRepository;
    private FinancingRoundPostProcessor financingRoundPostProcessor;
    private TaskScheduler crowdScheduler;

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent ) {
        this.reschedulePostProcessingOfFinancingRounds();
    }

    @Autowired
    public FinancingRoundService(UserRepository userRepository,
                                 UserService userService,
                                 FinancingRoundRepository financingRoundRepository,
                                 ProjectRepository projectRepository,
                                 FinancingRoundPostProcessor financingRoundPostProcessor,
                                 TaskScheduler crowdScheduler) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.financingRoundRepository = financingRoundRepository;
        this.projectRepository = projectRepository;
        this.financingRoundPostProcessor = financingRoundPostProcessor;
        this.crowdScheduler = crowdScheduler;
    }

    public FinancingRound startNewFinancingRound(FinancingRound creationCommand) {

        final List<UserEntity> userEntities = userRepository.findAll();

        // create round
        final FinancingRoundEntity financingRoundEntity = FinancingRoundEntity
                .newFinancingRound(creationCommand, userEntities.size());

        // flush user budget and set new budget
        final int budgetPerUser = financingRoundEntity.getBudgetPerUser();
        userEntities.forEach(userEntity -> {
            userEntity.setBudget(budgetPerUser);
            userRepository.save(userEntity);
        });

        final FinancingRoundEntity res = financingRoundRepository.save(financingRoundEntity);

        projectRepository.findAll().stream()
                .filter(res::projectEligibleForRound)
                .forEach(project -> {
                    project.setFinancingRound(res);
                    projectRepository.save(project);
                });

        schedulePostProcessing(res);

        return new FinancingRound(res);
    }

    public FinancingRound stopFinancingRound(String financingRoundId) throws ResourceNotFoundException, InvalidRequestException{
        FinancingRoundEntity financingRoundEntity = financingRoundRepository.findOne(financingRoundId);

        if (financingRoundEntity == null) {
            throw new ResourceNotFoundException();
        }
        if (financingRoundEntity.getEndDate().isBeforeNow()) {
            throw InvalidRequestException.financingRoundAlreadyStopped();
        }

        financingRoundEntity.stopFinancingRound();
        financingRoundRepository.save(financingRoundEntity);

        financingRoundEntity = financingRoundPostProcessor.postProcess(financingRoundEntity);

        return new FinancingRound(financingRoundEntity);
    }

    /**
     * Post processes a financing round upon its termination according to domain logic implemented
     * in {@link FinancingRoundPostProcessor}
     * @param financingRound the round to be processed after termination
     */
    void schedulePostProcessing(FinancingRoundEntity financingRound) {
        Assert.hasText(financingRound.getId());
        final String financingRoundId = financingRound.getId();

        crowdScheduler.schedule(() -> {
            FinancingRoundEntity entity2Process = financingRoundRepository.findOne(financingRoundId);
            if (entity2Process == null) {
                log.warn("The FinancingRoundEntity having ID {}, scheduled for post processing doesn't exist (anymore).", financingRoundId);
                return;
            }
            financingRoundPostProcessor.postProcess(entity2Process);
        }, financingRound.getEndDate().toDate());
        log.info("|-- Scheduled post processing of financing round targeted at: {} for financing round {}.", financingRound.getEndDate().toDate(), financingRound);
    }

    /**
     * Re-Schedules the post processing of active financing rounds as well as actually executes
     * post processing of financing rounds already terminated in case the application was not active
     * at point in time when triggering of post processing should have occurred.
     */
    void reschedulePostProcessingOfFinancingRounds(){
        log.info("Going to (re-) schedule post processing of still active or not already processed inactive financing rounds.");
        financingRoundRepository.findAll().stream()
                .filter(fr -> !fr.getTerminationPostProcessingDone() )
                .forEach(this::schedulePostProcessing);
    }
}
