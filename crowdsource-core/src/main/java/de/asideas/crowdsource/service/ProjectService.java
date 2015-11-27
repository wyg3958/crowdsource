package de.asideas.crowdsource.service;

import de.asideas.crowdsource.domain.exception.InvalidRequestException;
import de.asideas.crowdsource.domain.exception.ResourceNotFoundException;
import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import de.asideas.crowdsource.domain.model.PledgeEntity;
import de.asideas.crowdsource.domain.model.ProjectEntity;
import de.asideas.crowdsource.domain.model.UserEntity;
import de.asideas.crowdsource.domain.presentation.Pledge;
import de.asideas.crowdsource.domain.presentation.project.Project;
import de.asideas.crowdsource.domain.service.user.UserNotificationService;
import de.asideas.crowdsource.domain.shared.ProjectStatus;
import de.asideas.crowdsource.repository.FinancingRoundRepository;
import de.asideas.crowdsource.repository.PledgeRepository;
import de.asideas.crowdsource.repository.ProjectRepository;
import de.asideas.crowdsource.repository.UserRepository;
import de.asideas.crowdsource.security.Roles;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    private ProjectRepository projectRepository;
    private PledgeRepository pledgeRepository;
    private UserRepository userRepository;
    private FinancingRoundRepository financingRoundRepository;
    private UserNotificationService userNotificationService;
    private FinancingRoundService financingRoundService;

    private ProjectService thisInstance;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, PledgeRepository pledgeRepository,
                          UserRepository userRepository, FinancingRoundRepository financingRoundRepository,
                          UserNotificationService userNotificationService,
                          FinancingRoundService financingRoundService) {
        this.projectRepository = projectRepository;
        this.pledgeRepository = pledgeRepository;
        this.userRepository = userRepository;
        this.financingRoundRepository = financingRoundRepository;
        this.userNotificationService = userNotificationService;
        this.financingRoundService = financingRoundService;
        this.thisInstance = this;
    }

    public Project getProject(String projectId, UserEntity requestingUser) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        return project(projectEntity, requestingUser);
    }

    public List<Project> getProjects(UserEntity requestingUser) {

        final List<ProjectEntity> projects = projectRepository.findAll();
        return projects.stream().map(p -> project(p, requestingUser)).collect(toList());
    }

    public Project addProject(Project project, UserEntity creator) {
        Assert.notNull(project);
        Assert.notNull(creator);

        ProjectEntity projectEntity = new ProjectEntity(creator, project, currentFinancingRound());
        projectEntity = projectRepository.save(projectEntity);

        notifyAdminsOnNewProject(projectEntity);

        LOG.debug("Project added: {}", projectEntity);
        return project(projectEntity, creator);
    }

    public Project modifyProjectStatus(String projectId, ProjectStatus newStatusToApply, UserEntity requestingUser) {
        ProjectEntity projectEntity = projectRepository.findOne(projectId);

        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        if (projectEntity.modifyStatus(newStatusToApply)) {
            projectEntity = projectRepository.save(projectEntity);
            userNotificationService.notifyCreatorOnProjectUpdate(projectEntity);
        }

        return project(projectEntity, requestingUser);
    }

    public void pledge(String projectId, UserEntity userEntity, Pledge pledge) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);

        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        FinancingRoundEntity financingRound = financingRoundService.mostRecentRoundEntity();
        if (financingRound != null &&
                financingRound.terminated() &&
                financingRound.getTerminationPostProcessingDone() &&
                userEntity.getRoles().contains(Roles.ROLE_ADMIN)) {

            if (!financingRound.idenitityEquals(projectEntity.getFinancingRound()) ) {
                throw InvalidRequestException.projectTookNotPartInLastFinancingRond();
            }
            thisInstance.pledgeProjectUsingPostRoundBudget(projectEntity, userEntity, pledge);
        } else {
            thisInstance.pledgeProjectInFinancingRound(projectEntity, userEntity, pledge);
        }
    }

    void pledgeProjectInFinancingRound(ProjectEntity projectEntity, UserEntity userEntity, Pledge pledge) {
        List<PledgeEntity> pledgesSoFar = pledgeRepository.findByProjectAndFinancingRound(
                projectEntity, projectEntity.getFinancingRound());

        // potential problem: race condition. Two simultaneous requests could lead to "over-pledging"
        PledgeEntity pledgeEntity = projectEntity.pledge(
                pledge, userEntity, pledgesSoFar);

        // potential problem: no transaction -> no rollback -- Possible Solution -> sort of mini event sourcing?
        if (projectEntity.pledgeGoalAchieved()) {
            projectRepository.save(projectEntity);
        }
        userRepository.save(userEntity);
        pledgeRepository.save(pledgeEntity);

        LOG.debug("Project pledged: {}", pledgeEntity);
    }

    void pledgeProjectUsingPostRoundBudget(ProjectEntity projectEntity, UserEntity userEntity, Pledge pledge) {
        FinancingRoundEntity financingRound = projectEntity.getFinancingRound();

        List<PledgeEntity> postRoundPledges = pledgeRepository.findByFinancingRoundAndCreatedDateGreaterThan(
                financingRound, financingRound.getEndDate());

        int postRoundPledgableBudget = financingRound.postRoundPledgableBudgetRemaining(postRoundPledges);

        List<PledgeEntity> pledgesSoFar = pledgeRepository.findByProjectAndFinancingRound(
                projectEntity, projectEntity.getFinancingRound());

        PledgeEntity pledgeResult = projectEntity.pledgeUsingPostRoundBudget(
                pledge, userEntity, pledgesSoFar, postRoundPledgableBudget);

        if (projectEntity.pledgeGoalAchieved()) {
            projectRepository.save(projectEntity);
        }
        userRepository.save(userEntity);
        pledgeRepository.save(pledgeResult);

        LOG.debug("Project pledged using post round budget: {}", pledgeResult);
    }

    private Project project(ProjectEntity projectEntity, UserEntity requestingUser) {
        List<PledgeEntity> pledges = pledgeRepository.findByProjectAndFinancingRound(projectEntity, projectEntity.getFinancingRound());
        return new Project(projectEntity, pledges, requestingUser);
    }

    private FinancingRoundEntity currentFinancingRound() {
        return financingRoundRepository.findActive(DateTime.now());
    }

    private void notifyAdminsOnNewProject(final ProjectEntity projectEntity) {
        userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Roles.ROLE_ADMIN))
                .map(UserEntity::getEmail)
                .forEach(emailAddress -> userNotificationService.notifyAdminOnProjectCreation(projectEntity, emailAddress));
    }

}
