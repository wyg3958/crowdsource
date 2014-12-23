package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ProjectServiceBean implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PledgeRepository pledgeRepository;


    @Override
    public Project getProject(String projectId) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            return null;
        }

        return convertProject(projectEntity);
    }

    @Override
    public List<Project> getProjects() {

        final List<ProjectEntity> projects = projectRepository.findByPublicationStatusOrderByCreatedDateDesc(PublicationStatus.PUBLISHED);
        return projects.stream().map(this::convertProject).collect(toList());
    }

    @Override
    public void addProject(Project project, UserEntity userEntity) {

        ProjectEntity projectEntity = new ProjectEntity(userEntity, project);
        projectRepository.save(projectEntity);

        log.debug("Project added: {}", projectEntity);
    }

    @Override
    public void pledgeProject(String projectId, UserEntity userEntity, Pledge pledge) {

        ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }

        // TODO: may the creator of the project pledge on his own project?
        // TODO: check that the pledgedAmount does not exceed the pledgeGoal

        PledgeEntity pledgeEntity = new PledgeEntity(projectEntity, userEntity, pledge);
        pledgeRepository.save(pledgeEntity);

        log.debug("Project pledged: {}", pledgeEntity);
    }


    private Project convertProject(ProjectEntity projectEntity) {

        List<PledgeEntity> pledges = pledgeRepository.findByProject(projectEntity);
        return new Project(projectEntity, pledges);
    }
}
