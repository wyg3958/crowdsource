package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ProjectService {

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

        log.debug("Project added: {}", projectEntity);
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
            userNotificationService.notifyUserOnProjectUpdate(projectEntity, projectEntity.getCreator().getEmail());
        }

        log.debug("Project updated: {}", projectEntity);
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

        log.debug("Project pledged: {}", pledgeEntity);
    }

    private Project project(ProjectEntity projectEntity) {
        List<PledgeEntity> pledges = pledgeRepository.findByProjectAndFinancingRound(projectEntity, projectEntity.getFinancingRound());
        return new Project(projectEntity, pledges);
    }

    private FinancingRoundEntity currentFinancingRound() {
        return financingRoundRepository.findActive(DateTime.now());
    }

}
