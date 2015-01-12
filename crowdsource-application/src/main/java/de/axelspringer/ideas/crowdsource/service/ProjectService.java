package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.exceptions.InvalidRequestException;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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


    public Project getProject(String projectId) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        return convertProject(projectEntity);
    }

    public List<Project> getProjects() {

        final List<ProjectEntity> projects = projectRepository.findByPublicationStatusOrderByCreatedDateDesc(PublicationStatus.PUBLISHED);
        return projects.stream().map(this::convertProject).collect(toList());
    }

    public void addProject(Project project, UserEntity userEntity) {

        ProjectEntity projectEntity = new ProjectEntity(userEntity, project);
        projectRepository.save(projectEntity);

        log.debug("Project added: {}", projectEntity);
    }

    public void pledgeProject(String projectId, UserEntity userEntity, Pledge pledge) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        if (pledge.getAmount() > userEntity.getBudget()) {
            throw InvalidRequestException.userBudgetExceeded();
        }

        Project project = convertProject(projectEntity);
        int newPledgedAmount = pledge.getAmount() + project.getPledgedAmount();
        if (newPledgedAmount > project.getPledgeGoal()) {
            throw InvalidRequestException.pledgeGoalExceeded();
        }

        PledgeEntity pledgeEntity = new PledgeEntity(projectEntity, userEntity, pledge);
        userEntity.reduceBudget(pledge.getAmount());

        userRepository.save(userEntity);

        try {
            pledgeRepository.save(pledgeEntity);
        }
        catch (Exception e) {
            // roll back
            userEntity.increaseBudget(pledge.getAmount());
            userRepository.save(userEntity);
            throw e;
        }

        log.debug("Project pledged: {}", pledgeEntity);
    }


    private Project convertProject(ProjectEntity projectEntity) {

        List<PledgeEntity> pledges = pledgeRepository.findByProject(projectEntity);
        return new Project(projectEntity, pledges);
    }
}
