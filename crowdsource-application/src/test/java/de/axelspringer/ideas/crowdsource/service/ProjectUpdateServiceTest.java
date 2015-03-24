package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectUpdateServiceTest {

    private final static String USER_EMAIL = "user@some.host";
    private final static String ADMIN1_EMAIL = "admin1@some.host";
    private final static String ADMIN2_EMAIL = "admin2@some.host";

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private ProjectUpdateService projectUpdateService;

    @Before
    public void init() {
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void updateProjectStatus_WithUpdatedStateTriggersUserNotification() throws Exception {
        final ProjectEntity projectEntity = project("some_id", ProjectStatus.PROPOSED, user(USER_EMAIL));
        final Project updateObject = project(projectEntity);
        updateObject.setStatus(ProjectStatus.PUBLISHED);
        projectUpdateService.updateProject("some_id", updateObject);
        verify(userNotificationService).notifyCreatorOnProjectStatusUpdate(any(ProjectEntity.class));
    }

    @Test
    public void updateProjectStatus_WithNonUpdatedStateDoesNotTriggerUserNotification() throws Exception {
        final ProjectEntity projectEntity = project("some_id", ProjectStatus.PROPOSED, user(USER_EMAIL));
        final Project updateObject = project(projectEntity);
        updateObject.setStatus(ProjectStatus.PROPOSED);
        projectUpdateService.updateProject("some_id", updateObject);
        verify(userNotificationService, never()).notifyCreatorOnProjectStatusUpdate(any(ProjectEntity.class));
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
}