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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    private static final String USER_EMAIL = "user@some.host";
    private static final String ADMIN1_EMAIL = "admin1@some.host";
    private static final String ADMIN2_EMAIL = "admin2@some.host";
    public static final int USER_BUDGED = 4000;

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
        reset(projectRepository, pledgeRepository, userRepository, financingRoundRepository);
        when(pledgeRepository.findByProjectAndFinancingRound(any(ProjectEntity.class), any(FinancingRoundEntity.class))).thenReturn(new ArrayList<>());
        when(userRepository.findAll()).thenReturn(Arrays.asList(admin(ADMIN1_EMAIL), admin(ADMIN2_EMAIL), user(USER_EMAIL)));
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }


    @Test
    public void addProject() throws Exception {
        final Project project = project("myTitle", "theFullDescription", "theShortDescription", 50, ProjectStatus.PROPOSED);
        final ArgumentCaptor<ProjectEntity> projectEntity = ArgumentCaptor.forClass(ProjectEntity.class);

        when(projectRepository.save(projectEntity.capture())).thenAnswer(a -> a.getArgumentAt(0, ProjectEntity.class));
        prepareActiveFinanzingRound();

        Project res = projectService.addProject(project, user(USER_EMAIL));
        assertThat(res, is(new Project(projectEntity.getValue(), new ArrayList<>(), null)));
        verify(userNotificationService, atLeastOnce()).notifyAdminOnProjectCreation(eq(projectEntity.getValue()), anyString());
    }

    @Test
    public void addProject_shouldWorkIfNoFinancingRoundIsCurrentlyActive() throws Exception {
        final Project project = project("myTitle", "theFullDescription", "theShortDescription", 50, ProjectStatus.PROPOSED);
        final ArgumentCaptor<ProjectEntity> projectEntity = ArgumentCaptor.forClass(ProjectEntity.class);

        when(financingRoundRepository.findActive(any())).thenReturn(null);
        when(projectRepository.save(projectEntity.capture())).thenAnswer(a -> a.getArgumentAt(0, ProjectEntity.class));

        Project res = projectService.addProject(project, user(USER_EMAIL));
        assertThat(res, is(new Project(projectEntity.getValue(), new ArrayList<>(), null)));
        verify(userNotificationService, atLeastOnce()).notifyAdminOnProjectCreation(eq(projectEntity.getValue()), anyString());
    }

    @Test
    public void createProjectTriggersAdminNotification() throws Exception {
        final Project newProject = new Project();

        projectService.addProject(newProject, user("some@mail.com"));

        verify(userNotificationService).notifyAdminOnProjectCreation(any(ProjectEntity.class), eq(ADMIN1_EMAIL));
        verify(userNotificationService).notifyAdminOnProjectCreation(any(ProjectEntity.class), eq(ADMIN2_EMAIL));
        verify(userNotificationService, never()).notifyAdminOnProjectCreation(any(ProjectEntity.class), eq(USER_EMAIL));
        verify(userNotificationService, times(2)).notifyAdminOnProjectCreation(any(ProjectEntity.class), anyString());
    }

    @Test
    public void pledge() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final int budgetBeforePledge = user.getBudget();
        final Pledge pledge = new Pledge(project.getPledgeGoal() - 4);

        FinancingRoundEntity financingRound = prepareActiveFinanzingRound();

        projectService.pledge(projectId, user, pledge);

        PledgeEntity pledgeEntity = new PledgeEntity(project, user, pledge, financingRound);
        assertThat(user.getBudget(), is(budgetBeforePledge - pledge.getAmount()));
        assertThat(project.getStatus(), is(not(ProjectStatus.FULLY_PLEDGED)));
        verify(pledgeRepository).save(pledgeEntity);
        verify(userRepository).save(user);
        verify(projectRepository, never()).save(any(ProjectEntity.class));
    }

    @Test
    public void pledge_reverse() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final int budgetBeforePledge = user.getBudget();
        final Pledge pledge = new Pledge(-4);

        pledgedAssertionProject(project, user, 4);
        FinancingRoundEntity financingRound = prepareActiveFinanzingRound();

        projectService.pledge(projectId, user, pledge);

        PledgeEntity pledgeEntity = new PledgeEntity(project, user, pledge, financingRound);
        assertThat(user.getBudget(), is(budgetBeforePledge + 4 ));
        assertThat(project.getStatus(), is(not(ProjectStatus.FULLY_PLEDGED)));
        verify(pledgeRepository).save(pledgeEntity);
        verify(userRepository).save(user);
        verify(projectRepository, never()).save(any(ProjectEntity.class));
    }

    @Test
    public void pledge_shouldSetTheProjectStatusToFullyPledged() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final Pledge pledge = new Pledge(4);
        final int budgetBeforePledge = user.getBudget();

        pledgedAssertionProject(project, user, project.getPledgeGoal() - 4);
        FinancingRoundEntity finanzingRound = prepareActiveFinanzingRound();

        projectService.pledge(projectId, user, pledge);

        PledgeEntity pledgeEntity = new PledgeEntity(project, user, pledge, finanzingRound);
        assertThat(user.getBudget(), is(budgetBeforePledge - pledge.getAmount()));
        assertThat(project.getStatus(), is(ProjectStatus.FULLY_PLEDGED));
        verify(pledgeRepository).save(pledgeEntity);
        verify(userRepository).save(user);
        verify(projectRepository).save(project);
    }

    @Test
    public void pledge_reversePledgeThrowsInvalidRequestExWhenExceedingPledgeAmountAlreadyMade() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final int budgetBeforePledge = user.getBudget();
        final Pledge pledge = new Pledge(-5);

        prepareActiveFinanzingRound();
        pledgedAssertionProject(project, user, 4);

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.reversePledgeExceeded(), project, user, budgetBeforePledge);
    }
    @Test
    public void pledge_reversePledgeThrowsInvalidRequestExWhenAlreadyFullyPledged() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final int budgetBeforePledge = user.getBudget();
        final Pledge pledge = new Pledge(-5);

        project.setStatus(ProjectStatus.FULLY_PLEDGED);
        prepareActiveFinanzingRound();
        pledgedAssertionProject(project, user, 4);

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.projectAlreadyFullyPledged(), project, user, budgetBeforePledge, ProjectStatus.FULLY_PLEDGED);
    }

    @Test
    public void pledge_throwsInvalidRequestExOnPledgeGoalIsExceeded() {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final Pledge pledge = new Pledge(5);
        final int budgetBeforePledge = user.getBudget();
        pledgedAssertionProject(project, user, project.getPledgeGoal() - 4);

        prepareActiveFinanzingRound();

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.pledgeGoalExceeded(), project, user, budgetBeforePledge);
    }

    @Test
    public void pledge_throwsInvalidRequestExOnProjectIsAlreadyFullyPledged() {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final Pledge pledge = new Pledge(5);
        final int budgetBeforePledge = user.getBudget();

        pledgedAssertionProject(project, user, project.getPledgeGoal());
        prepareActiveFinanzingRound();

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.projectAlreadyFullyPledged(), project, user, budgetBeforePledge, ProjectStatus.FULLY_PLEDGED);
    }

    @Test
    public void pledge_throwsInvalidRequestExOnProjectIsNotPublished() {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PROPOSED, null);
        final Pledge pledge = new Pledge(5);
        final int budgetBeforePledge = user.getBudget();

        pledgedAssertionProject(project, user, project.getPledgeGoal() - 4);
        prepareActiveFinanzingRound();

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.projectNotPublished(), project, user, budgetBeforePledge, ProjectStatus.PROPOSED);
    }

    @Test
    public void pledge_throwsInvalidRequestExOnUserBudgetIsExceeded() {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", USER_BUDGED + 20, "short description", "description", ProjectStatus.PUBLISHED, null);
        final Pledge pledge = new Pledge(USER_BUDGED + 10);
        final int budgetBeforePledge = user.getBudget();

        pledgedAssertionProject(project, user, project.getPledgeGoal() - 4);
        prepareActiveFinanzingRound();

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.userBudgetExceeded(), project, user, budgetBeforePledge, ProjectStatus.PUBLISHED);
    }

    @Test
    public void pledge_throwsInvalidRequestExOnNoActiveFinancingRound() {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final Pledge pledge = new Pledge(4);
        final int budgetBeforePledge = user.getBudget();

        pledgedAssertionProject(project, user, project.getPledgeGoal() - 4);
        when(financingRoundRepository.findActive(any(DateTime.class))).thenReturn(null);

        InvalidRequestException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (InvalidRequestException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, InvalidRequestException.noFinancingRoundCurrentlyActive(), project, user, budgetBeforePledge, ProjectStatus.PUBLISHED);
    }

    @Test
    public void pledge_throwsResourceNotFoundExOnNotExistingProject() {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity project = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED, null);
        final Pledge pledge = new Pledge(4);
        final int budgetBeforePledge = user.getBudget();

        pledgedAssertionProject(project, user, project.getPledgeGoal() - 4);
        prepareActiveFinanzingRound();
        when(projectRepository.findOne(anyString())).thenReturn(null);

        ResourceNotFoundException res = null;
        try {
            projectService.pledge(projectId, user, pledge);
            fail("InvalidRequestException expected!");
        } catch (ResourceNotFoundException e) {
            res = e;
        }

        assertPledgeNotExecuted(res, new ResourceNotFoundException(), project, user, budgetBeforePledge, ProjectStatus.PUBLISHED);
    }

    @Test
    public void modifyProjectStatus() throws Exception{
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity projectEntity = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PROPOSED, null);
        final Project updatedProject = project(projectEntity, user);
        final ArgumentCaptor<ProjectEntity> captProjectEntity = ArgumentCaptor.forClass(ProjectEntity.class);

        updatedProject.setStatus(ProjectStatus.PUBLISHED);
        when(projectRepository.save(captProjectEntity.capture())).thenAnswer(a -> a.getArgumentAt(0, ProjectEntity.class));

        Project resProject = projectService.modifyProjectStatus("some_id", updatedProject, user);

        assertThat(resProject, is(updatedProject));
        assertThat(captProjectEntity.getValue().getStatus(), is(updatedProject.getStatus()));
    }

    @Test
    public void modifyProjectStatus_updatedStateTriggersUserNotification() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final ProjectEntity projectEntity = project("some_id", ProjectStatus.PROPOSED, user);
        final Project updatedProject = project(projectEntity, user);
        updatedProject.setStatus(ProjectStatus.PUBLISHED);

        projectService.modifyProjectStatus("some_id", updatedProject, user);

        verify(projectRepository).save(projectEntity);
        verify(userNotificationService).notifyCreatorOnProjectUpdate(any(ProjectEntity.class));
    }

    @Test
    public void modifyProjectStatus_nonUpdatedStateDoesNotTriggerUserNotification() throws Exception {
        UserEntity user = user(USER_EMAIL);
        final ProjectEntity projectEntity = project("some_id", ProjectStatus.PROPOSED, user);
        final Project updateObject = project(projectEntity, user);
        updateObject.setStatus(ProjectStatus.PROPOSED);

        projectService.modifyProjectStatus("some_id", updateObject, user);

        verify(projectRepository, never()).save(any(ProjectEntity.class));
        verify(userNotificationService, never()).notifyCreatorOnProjectUpdate(any(ProjectEntity.class));
    }

    @Test
    public void modifyProjectStatus_settingToPublishedAlthoughFullyPledgedThrowsIvalidRequestEx() throws Exception {
        final UserEntity user = user(USER_EMAIL);
        final String projectId = "some_id";
        final ProjectEntity projectEntity = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.FULLY_PLEDGED, null);
        final Project updatedProject = project(projectEntity, user);

        updatedProject.setStatus(ProjectStatus.PUBLISHED);

        try {
            projectService.modifyProjectStatus("some_id", updatedProject, user);
            fail("Expected InvalidRequestException was not thrown");
        } catch (InvalidRequestException e) {
            assertThat(e.getMessage(), is(InvalidRequestException.projectAlreadyFullyPledged().getMessage()));
            verify(projectRepository, never()).save(any(ProjectEntity.class));
        }
    }

    private void assertPledgeNotExecuted(RuntimeException actualEx, RuntimeException expEx, ProjectEntity project, UserEntity user, int userBudgetBeforePledge) {
        assertPledgeNotExecuted(actualEx, expEx, project, user, userBudgetBeforePledge, ProjectStatus.PUBLISHED);
    }

    private void assertPledgeNotExecuted(RuntimeException actualEx, RuntimeException expEx, ProjectEntity project, UserEntity user, int userBudgetBeforePledge, ProjectStatus expStatus) {
        assertThat(actualEx.getMessage(), is(expEx.getMessage()));
        assertThat(user.getBudget(), is(userBudgetBeforePledge));
        assertThat(project.getStatus(), is(expStatus));
        verify(pledgeRepository, never()).save(any(PledgeEntity.class));
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(projectRepository, never()).save(any(ProjectEntity.class));
    }

    private void pledgedAssertionProject(ProjectEntity project, UserEntity user, int amount) {

        when(pledgeRepository.findByProjectAndFinancingRound(eq(project), any()))
                .thenReturn(Collections.singletonList(new PledgeEntity(project, user, new Pledge(amount), new FinancingRoundEntity())));

        if (project.getPledgeGoal() == amount) {
            project.setStatus(ProjectStatus.FULLY_PLEDGED);
        }
    }

    private ProjectEntity projectEntity(UserEntity userEntity, String id, String title, int pledgeGoal, String shortDescription, String description, ProjectStatus status, DateTime lastModifiedDate) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(id);
        projectEntity.setTitle(title);
        projectEntity.setPledgeGoal(pledgeGoal);
        projectEntity.setShortDescription(shortDescription);
        projectEntity.setDescription(description);
        projectEntity.setCreator(userEntity);
        projectEntity.setStatus(status);
        projectEntity.setLastModifiedDate(lastModifiedDate);
        when(projectRepository.findOne(id)).thenReturn(projectEntity);
        return projectEntity;
    }

    private ProjectEntity project(String id, ProjectStatus status, UserEntity user) {
        final ProjectEntity project = new ProjectEntity();
        project.setId(id);
        project.setCreator(user);
        project.setStatus(status);
        when(projectRepository.findOne(id)).thenReturn(project);
        return project;
    }

    private Project project(String title, String description, String shortDescription, int pledgeGoal, ProjectStatus projectStatus) {
        final Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setShortDescription(shortDescription);
        project.setPledgeGoal(pledgeGoal);
        project.setStatus(projectStatus);

        return project;
    }

    private Project project(ProjectEntity projectEntity, UserEntity requestingUser) {
        return new Project(projectEntity, new ArrayList<>(), requestingUser);
    }

    private UserEntity user(String email) {
        UserEntity userEntity = new UserEntity(email);
        userEntity.setId("id_" + email);
        userEntity.setBudget(USER_BUDGED);
        return userEntity;
    }

    private UserEntity admin(String email) {
        final UserEntity userEntity = new UserEntity(email);
        userEntity.setRoles(Collections.singletonList(Roles.ROLE_ADMIN));
        return userEntity;
    }

    private FinancingRoundEntity prepareActiveFinanzingRound() {
        FinancingRoundEntity financingRound = new FinancingRoundEntity();
        financingRound.setId(UUID.randomUUID().toString());
        when(financingRoundRepository.findActive(any())).thenReturn(financingRound);
        return financingRound;
    }
}