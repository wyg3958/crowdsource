package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private PledgeRepository pledgeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FinancingRoundRepository financingRoundRepository;

    @InjectMocks
    private ProjectService projectService;

    @Before
    public void init() {
        reset(projectRepository);
        when(pledgeRepository.findByProjectAndFinancingRound(any(ProjectEntity.class), any(FinancingRoundEntity.class))).thenReturn(new ArrayList<>());
        when(userRepository.findAll()).thenReturn(Arrays.asList(admin("some@other.mail"), admin("my@admin.com"), user("some@mail.com")));
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void testUpdateProjectWithUpdatedStateTriggersUserNotification() throws Exception {

        final ProjectEntity projectEntity = project("some_id", ProjectStatus.PROPOSED, user("some@mail.com"));
        final Project updateObject = project(projectEntity);
        updateObject.setStatus(ProjectStatus.PUBLISHED);
        projectService.updateProject("some_id", updateObject);
        verify(userNotificationService).notifyUserOnProjectUpdate(any(ProjectEntity.class), anyString());
    }

    @Test
    public void testUpdateProjectWithNonUpdatedStateDoesNotTriggerUserNotification() throws Exception {

        final ProjectEntity projectEntity = project("some_id", ProjectStatus.PROPOSED, user("some@mail.com"));
        final Project updateObject = project(projectEntity);
        updateObject.setStatus(ProjectStatus.PROPOSED);
        projectService.updateProject("some_id", updateObject);
        verify(userNotificationService, never()).notifyUserOnProjectUpdate(any(ProjectEntity.class), anyString());
    }

    @Test
    public void testCreateProjectTriggersAdminNotification() throws Exception {

        final Project newProject = new Project();
        projectService.addProject(newProject, user("some@mail.com"));
        verify(userNotificationService, times(2)).notifyAdminOnProjectCreation(any(ProjectEntity.class), anyString());
    }

    private Project project(ProjectEntity projectEntity) {
        return new Project(projectEntity, new ArrayList<>());
    }

    private ProjectEntity project(String id, ProjectStatus status, UserEntity user) {
        final ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setCreator(user);
        project.setStatus(status);
        when(projectRepository.findOne(id)).thenReturn(project);
        return project;
    }

    private UserEntity user(String email) {
        return new UserEntity(email);
    }

    private UserEntity admin(String email) {
        final UserEntity userEntity = new UserEntity(email);
        userEntity.setRoles(Arrays.asList(Roles.ROLE_ADMIN));
        return userEntity;
    }
}