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

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PledgeRepository pledgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancingRoundRepository financingRoundRepository;

    @Autowired
    private UserNotificationService userNotificationService;

    public Project getProject(String projectId) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        return project(projectEntity);
    }

    public List<Project> getProjects() {

        final List<ProjectEntity> projects = projectRepository.findAll();
        return projects.stream().map(this::project).collect(toList());
    }

    public Project addProject(Project project, UserEntity userEntity) {

        ProjectEntity projectEntity = new ProjectEntity(userEntity, project, currentFinancingRound());
        projectEntity = projectRepository.save(projectEntity);

        notifyAdminsOnNewProject(projectEntity);

        LOG.debug("Project added: {}", projectEntity);
        return project(projectEntity);
    }

    private void notifyAdminsOnNewProject(final ProjectEntity projectEntity) {
        userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(Roles.ROLE_ADMIN))
                .map(UserEntity::getEmail)
                .forEach(emailAddress -> userNotificationService.notifyAdminOnProjectCreation(projectEntity, emailAddress));
    }

    public Project updateProject(String projectId, Project project) {

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
        return project(projectEntity);
    }

    public void pledge(String projectId, UserEntity userEntity, Pledge pledge) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        FinancingRoundEntity activeFinancingRoundEntity = currentFinancingRound();

        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        final ProjectStatus projectStatus = projectEntity.getStatus();

        // potential problem: race condition. Two simultaneous requests could lead to "over-pledging"
        if (projectStatus == ProjectStatus.FULLY_PLEDGED) {
            throw InvalidRequestException.projectAlreadyFullyPledged();
        }

        if (projectStatus != ProjectStatus.PUBLISHED) {
            throw InvalidRequestException.projectNotPublished();
        }

        if (activeFinancingRoundEntity == null) {
            throw InvalidRequestException.noFinancingRoundCurrentlyActive();
        }

        if (pledge.getAmount() > userEntity.getBudget()) {
            throw InvalidRequestException.userBudgetExceeded();
        }

        Project project = project(projectEntity);
        int newPledgedAmount = pledge.getAmount() + project.getPledgedAmount();
        if (newPledgedAmount > project.getPledgeGoal()) {
            throw InvalidRequestException.pledgeGoalExceeded();
        }

        PledgeEntity pledgeEntity = new PledgeEntity(projectEntity, userEntity, pledge, activeFinancingRoundEntity);
        userEntity.reduceBudget(pledge.getAmount());

        if (newPledgedAmount == project.getPledgeGoal()) {
            projectEntity.setStatus(ProjectStatus.FULLY_PLEDGED);
            projectRepository.save(projectEntity);
        }

        // potential problem: no transaction -> no rollback
        userRepository.save(userEntity);
        pledgeRepository.save(pledgeEntity);

        LOG.debug("Project pledged: {}", pledgeEntity);
    }

    private Project project(ProjectEntity projectEntity) {
        List<PledgeEntity> pledges = pledgeRepository.findByProjectAndFinancingRound(projectEntity, projectEntity.getFinancingRound());
        return new Project(projectEntity, pledges);
    }

    private FinancingRoundEntity currentFinancingRound() {
        return financingRoundRepository.findActive(DateTime.now());
    }

}
