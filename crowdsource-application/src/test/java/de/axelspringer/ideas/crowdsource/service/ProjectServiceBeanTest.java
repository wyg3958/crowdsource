package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.user.User;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceBeanTest {

    private static final String EXISTING_PROJECT_ID = "existing_project_id";
    private static final String NON_EXISTING_PROJECT_ID = "non_existing_project_id";

    @InjectMocks
    private ProjectServiceBean projectServiceBean;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PledgeRepository pledgeRepository;

    private UserEntity existingUserEntity;
    private ProjectEntity existingProjectEntity;


    @Before
    public void setUp() throws Exception {
        existingUserEntity = new UserEntity("email1@domain.com");

        existingProjectEntity = createProjectEntity("projectId1", "myTitle1", 123, "myShortDescription1", "description3");
        when(projectRepository.findOne(EXISTING_PROJECT_ID)).thenReturn(existingProjectEntity);

        List<PledgeEntity> pledgeEntities = Arrays.asList(
                new PledgeEntity(existingProjectEntity, existingUserEntity, new Pledge(50)),
                new PledgeEntity(existingProjectEntity, existingUserEntity, new Pledge(20))
        );
        when(pledgeRepository.findByProject(any(ProjectEntity.class))).thenReturn(pledgeEntities);
    }

    @Test
    public void getProject_shouldReturnProject() throws Exception {
        Project project = projectServiceBean.getProject(EXISTING_PROJECT_ID);

        assertThat(project, is(notNullValue()));
        assertThat(project.getPledgedAmount(), is(70));
        assertThat(project.getBackers(), is(1L));
    }

    @Test
    public void getProject_shouldReturnNullOnInvalidProjectId() throws Exception {
        Project project = projectServiceBean.getProject(NON_EXISTING_PROJECT_ID);

        assertThat(project, is(nullValue()));
    }

    @Test
    public void getProjects_shouldReturnProjects() throws Exception {
        List<ProjectEntity> entities = Arrays.asList(
                createProjectEntity("projectId1", "myTitle1", 123, "myShortDescription1", "description1"),
                createProjectEntity("projectId2", "myTitle2", 456, "myShortDescription2", "description2"),
                createProjectEntity("projectId3", "myTitle3", 789, "myShortDescription3", "description3")
        );
        when(projectRepository.findByPublicationStatusOrderByCreatedDateDesc(any(PublicationStatus.class))).thenReturn(entities);

        List<Project> projects = projectServiceBean.getProjects();

        assertThat(projects, contains(
                createProject("projectId1", "myTitle1", "myShortDescription1", "description1", 123, 70, 1),
                createProject("projectId2", "myTitle2", "myShortDescription2", "description2", 456, 70, 1),
                createProject("projectId3", "myTitle3", "myShortDescription3", "description3", 789, 70, 1)
        ));

        verify(projectRepository).findByPublicationStatusOrderByCreatedDateDesc(PublicationStatus.PUBLISHED);
    }

    @Test
    public void addProject_shouldSave() throws Exception {

        Project project = createProject("projectId1", "myTitle1", "myShortDescription1", "description1", 123, 70, 1);
        projectServiceBean.addProject(project, existingUserEntity);

        ProjectEntity projectEntity = new ProjectEntity(existingUserEntity, project);
        verify(projectRepository).save(eq(projectEntity));
    }

    @Test
    public void pledgeProject_shouldPledge() throws Exception {

        Pledge pledge = new Pledge(33);
        projectServiceBean.pledgeProject(EXISTING_PROJECT_ID, existingUserEntity, pledge);

        verify(projectRepository).findOne(eq(EXISTING_PROJECT_ID));
        verify(pledgeRepository).save(eq(new PledgeEntity(existingProjectEntity, existingUserEntity, pledge)));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void pledgeProject_shouldThrowExceptionOnInvalidProjectId() throws Exception {

        projectServiceBean.pledgeProject(NON_EXISTING_PROJECT_ID, existingUserEntity, new Pledge(33));
    }

    private ProjectEntity createProjectEntity(String id, String title, int pledgeGoal, String shortDescription, String description) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(id);
        projectEntity.setTitle(title);
        projectEntity.setPledgeGoal(pledgeGoal);
        projectEntity.setShortDescription(shortDescription);
        projectEntity.setDescription(description);
        projectEntity.setCreator(existingUserEntity);
        return projectEntity;
    }

    private Project createProject(String projectId, String title, String shortDescription, String description, int pledgeGoal, int pledgedAmount, int backers) {
        Project project = new Project();
        project.setId(projectId);
        project.setTitle(title);
        project.setShortDescription(shortDescription);
        project.setDescription(description);
        project.setPledgeGoal(pledgeGoal);
        project.setPledgedAmount(pledgedAmount);
        project.setBackers(backers);
        project.setCreator(new User(existingUserEntity));
        return project;
    }
}