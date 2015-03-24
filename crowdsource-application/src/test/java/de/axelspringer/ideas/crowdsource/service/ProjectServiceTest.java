package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.config.security.Roles;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    private final static String USER_EMAIL = "user@some.host";
    private final static String ADMIN1_EMAIL = "admin1@some.host";
    private final static String ADMIN2_EMAIL = "admin2@some.host";


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
        when(userRepository.findAll()).thenReturn(Arrays.asList(admin(ADMIN1_EMAIL), admin(ADMIN2_EMAIL), user(USER_EMAIL)));
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void testCreateProjectTriggersAdminNotification() throws Exception {

        final Project newProject = new Project();
        projectService.addProject(newProject, user("some@mail.com"));
        verify(userNotificationService).notifyAdminOnProjectCreation(any(ProjectEntity.class), eq(ADMIN1_EMAIL));
        verify(userNotificationService).notifyAdminOnProjectCreation(any(ProjectEntity.class), eq(ADMIN2_EMAIL));
        verify(userNotificationService, never()).notifyAdminOnProjectCreation(any(ProjectEntity.class), eq(USER_EMAIL));
        verify(userNotificationService, times(2)).notifyAdminOnProjectCreation(any(ProjectEntity.class), anyString());
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