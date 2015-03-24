package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProjectUpdateService {

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


    public Project updateProjectStatus(String projectId, Project project) {
        ProjectEntity projectEntity = loadProjectEntity(projectId);
        if (hasNewStatus(project, projectEntity)) {
            if (projectEntity.getStatus() == ProjectStatus.FULLY_PLEDGED) {
                throw InvalidRequestException.projectAlreadyFullyPledged();
            }

            projectEntity.setStatus(project.getStatus());
            projectEntity = projectRepository.save(projectEntity);
            userNotificationService.notifyCreatorOnProjectStatusUpdate(projectEntity);
        }

        log.debug("Project updated: {}", projectEntity);
        return project(projectEntity);
    }

    public Project updateProject(String projectId, Project project) {
        ProjectEntity projectEntity = loadProjectEntity(projectId);

        if (hasProjectChanged(project, projectEntity)) {
            projectEntity.setTitle(project.getTitle());
            projectEntity.setDescription(project.getDescription());
            projectEntity.setShortDescription(project.getShortDescription());
            projectEntity.setPledgeGoal(project.getPledgeGoal());
            projectEntity = projectRepository.save(projectEntity);
            userNotificationService.notifyCreatorOnProjectUpdate(projectEntity);
        }

        log.debug("Project updated: {}", projectEntity);
        return project(projectEntity);
    }

    private boolean hasProjectChanged(Project project, ProjectEntity projectEntity) {
        return !project(projectEntity).equals(project);
    }

    private boolean hasNewStatus(Project project, ProjectEntity projectEntity) {
        return projectEntity.getStatus() != project.getStatus();
    }

    private ProjectEntity loadProjectEntity(String projectId) {
        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }
        return projectEntity;
    }


    private Project project(ProjectEntity projectEntity) {
        List<PledgeEntity> pledges = pledgeRepository.findByProjectAndFinancingRound(projectEntity, projectEntity.getFinancingRound());
        return new Project(projectEntity, pledges);
    }
}
