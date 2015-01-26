package de.axelspringer.ideas.crowdsource.service;

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


    public Project getProject(String projectId) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        return project(projectEntity, getActiveFinancingRoundEntity());
    }

    public List<Project> getProjects() {

        final List<ProjectEntity> projects = projectRepository.findAll();
        return projects.stream().map(p -> project(p, getActiveFinancingRoundEntity())).collect(toList());
    }

    public Project addProject(Project project, UserEntity userEntity) {

        ProjectEntity projectEntity = new ProjectEntity(userEntity, project);
        projectEntity = projectRepository.save(projectEntity);

        log.debug("Project added: {}", projectEntity);
        return project(projectEntity, getActiveFinancingRoundEntity());
    }

    public Project updateProject(String projectId, Project project) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }
        projectEntity.setStatus(project.getStatus());
        projectEntity = projectRepository.save(projectEntity);

        log.debug("Project updated: {}", projectEntity);
        return project(projectEntity, getActiveFinancingRoundEntity());
    }

    public void pledge(String projectId, UserEntity userEntity, Pledge pledge) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        FinancingRoundEntity activeFinancingRoundEntity = getActiveFinancingRoundEntity();

        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        if (activeFinancingRoundEntity == null) {
            throw InvalidRequestException.noFinancingRoundCurrentlyActive();
        }

        // potential problem: race condition. Two simultaneous requests could lead to "over-pledging"
        if (projectEntity.getStatus() == ProjectStatus.FULLY_PLEDGED) {
            throw InvalidRequestException.projectAlreadyFullyPledged();
        }

        if (financingRoundRepository.findActive(DateTime.now()) == null) {
            throw InvalidRequestException.noFinancingRoundCurrentlyActive();
        }

        if (pledge.getAmount() > userEntity.getBudget()) {
            throw InvalidRequestException.userBudgetExceeded();
        }

        Project project = project(projectEntity, activeFinancingRoundEntity);
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

    
    private FinancingRoundEntity getActiveFinancingRoundEntity() {
        return financingRoundRepository.findActive(DateTime.now());
    }

    private Project project(ProjectEntity projectEntity, FinancingRoundEntity activeFinancingRoundEntity) {

        List<PledgeEntity> pledges = pledgeRepository.findByProjectAndFinancingRound(projectEntity, activeFinancingRoundEntity);
        return new Project(projectEntity, pledges);
    }

}
