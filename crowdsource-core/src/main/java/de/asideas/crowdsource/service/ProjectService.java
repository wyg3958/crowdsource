package de.asideas.crowdsource.service;

import de.asideas.crowdsource.security.Roles;
import de.asideas.crowdsource.enums.ProjectStatus;
import de.asideas.crowdsource.exceptions.InvalidRequestException;
import de.asideas.crowdsource.exceptions.ResourceNotFoundException;
import de.asideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.asideas.crowdsource.model.persistence.PledgeEntity;
import de.asideas.crowdsource.model.persistence.ProjectEntity;
import de.asideas.crowdsource.model.persistence.UserEntity;
import de.asideas.crowdsource.model.presentation.Pledge;
import de.asideas.crowdsource.model.presentation.project.Project;
import de.asideas.crowdsource.repository.FinancingRoundRepository;
import de.asideas.crowdsource.repository.PledgeRepository;
import de.asideas.crowdsource.repository.ProjectRepository;
import de.asideas.crowdsource.repository.UserRepository;
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

    @Autowired
    public ProjectService(ProjectRepository projectRepository, PledgeRepository pledgeRepository,
                          UserRepository userRepository, FinancingRoundRepository financingRoundRepository,
                          UserNotificationService userNotificationService) {
        this.projectRepository = projectRepository;
        this.pledgeRepository = pledgeRepository;
        this.userRepository = userRepository;
        this.financingRoundRepository = financingRoundRepository;
        this.userNotificationService = userNotificationService;
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

    private void notifyAdminsOnNewProject(final ProjectEntity projectEntity) {
        userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Roles.ROLE_ADMIN))
                .map(UserEntity::getEmail)
                .forEach(emailAddress -> userNotificationService.notifyAdminOnProjectCreation(projectEntity, emailAddress));
    }

    public Project modifyProjectStatus(String projectId, Project project, UserEntity requestingUser) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        if (projectEntity.getStatus() != project.getStatus()) {

            if (projectEntity.getStatus() == ProjectStatus.FULLY_PLEDGED) {
                throw InvalidRequestException.projectAlreadyFullyPledged();
            }

            projectEntity.setStatus(project.getStatus());
            projectEntity = projectRepository.save(projectEntity);
            userNotificationService.notifyCreatorOnProjectUpdate(projectEntity);
        }

        LOG.debug("Project updated: {}", projectEntity);
        return project(projectEntity, requestingUser);
    }

    public void pledge(String projectId, UserEntity userEntity, Pledge pledge) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        FinancingRoundEntity activeFinancingRoundEntity = currentFinancingRound();

        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }
        if (activeFinancingRoundEntity == null) {
            throw InvalidRequestException.noFinancingRoundCurrentlyActive();
        }

        List<PledgeEntity> pledgesSoFar = pledgeRepository.findByProjectAndFinancingRound(
                projectEntity, projectEntity.getFinancingRound());

        // potential problem: race condition. Two simultaneous requests could lead to "over-pledging"
        PledgeEntity pledgeEntity = projectEntity.pledge(
                pledge, activeFinancingRoundEntity, userEntity, pledgesSoFar);

        if(projectEntity.pledgeGoalAchieved()){
            projectRepository.save(projectEntity);
        }
        // potential problem: no transaction -> no rollback -- Possible Solution -> sort of mini event sourcing?
        userRepository.save(userEntity);
        pledgeRepository.save(pledgeEntity);

        LOG.debug("Project pledged: {}", pledgeEntity);
    }

    private Project project(ProjectEntity projectEntity, UserEntity requestingUser) {
        List<PledgeEntity> pledges = pledgeRepository.findByProjectAndFinancingRound(projectEntity, projectEntity.getFinancingRound());
        return new Project(projectEntity, pledges, requestingUser);
    }

    private FinancingRoundEntity currentFinancingRound() {
        return financingRoundRepository.findActive(DateTime.now());
    }

}
