package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.config.security.Roles;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
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
import de.axelspringer.ideas.crowdsource.service.ProjectService;
import de.axelspringer.ideas.crowdsource.service.UserNotificationService;
import de.axelspringer.ideas.crowdsource.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ProjectControllerTest.Config.class)
public class ProjectControllerTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PledgeRepository pledgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancingRoundRepository financingRoundRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(projectRepository);
        reset(pledgeRepository);
        reset(userRepository);
        reset(financingRoundRepository);

        // make sure that the project is returned that is request to save
        when(projectRepository.save(any(ProjectEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
    }

    @Test
    public void addProject_shouldReturnSuccessfully() throws Exception {

        final Project project = project("myTitle", "theFullDescription", "theShortDescription", 50);

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        MvcResult mvcResult = mockMvc.perform(post("/project")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isCreated())
                .andReturn();

        ProjectEntity projectEntity = new ProjectEntity(user, project);
        verify(projectRepository).save(eq(projectEntity));

        assertThat(mvcResult.getResponse().getContentAsString(), is("{" +
                "\"id\":null," + // actually this is non null, but the projectRepository is a mock and does not generate an id
                "\"status\":\"PROPOSED\"," +
                "\"title\":\"myTitle\"," +
                "\"shortDescription\":\"theShortDescription\"," +
                "\"description\":\"theFullDescription\"," +
                "\"pledgeGoal\":50,\"pledgedAmount\":0," +
                "\"backers\":0," +
                "\"creator\":{\"id\":\"id_" + email + "\",\"name\":\"Some\",\"email\":\"" + email + "\"}}"));
    }

    @Test
    public void addProject_shouldRespondWith401IfUserWasNotFound() throws Exception {

        final Project project = project("myTitle", "theFullDescription", "theShortDescription", 50);

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken("foo@bar.de", "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void addProject_shouldRespondWith400IfRequestWasInvalid() throws Exception {

        final Project project = new Project();

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        mockMvc.perform(post("/project")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getProject_shouldRespondWith404OnInvalidProjectId() throws Exception {
        mockMvc.perform(get("/project/{projectId}", "foo bah bah"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProject_shouldReturnSingleProjectSuccessfullyWhenProjectIsPublished() throws Exception {

        final String email = "some@mail.com";
        final UserEntity userEntity = userEntity(email, Roles.ROLE_USER);

        final String projectId = "existingProjectId";
        projectEntity(userEntity, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED);

        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{" +
                "\"id\":\"" + projectId + "\"," +
                "\"status\":\"PUBLISHED\"," +
                "\"title\":\"title\"," +
                "\"shortDescription\":\"short description\"," +
                "\"description\":\"description\"," +
                "\"pledgeGoal\":44," +
                "\"pledgedAmount\":0," +
                "\"backers\":0," +
                "\"creator\":{\"id\":\"id_" + email + "\",\"name\":\"Some\",\"email\":\"" + email + "\"}}"));
    }

    @Test
    public void shouldReturnProjectInProjectsQueryWhenProjectIsPublished() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        final String projectId = "existingProjectId";
        final ProjectEntity projectEntity = projectEntity(user, projectId, "title", 44, "short description", "description", ProjectStatus.PUBLISHED);
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(projectEntity));

        MvcResult mvcResult = mockMvc.perform(get("/projects", projectId)
                .principal(authentication(user)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[{" +
                "\"id\":\"" + projectId + "\"," +
                "\"status\":\"PUBLISHED\"," +
                "\"title\":\"title\"," +
                "\"shortDescription\":\"short description\"," +
                "\"pledgeGoal\":44," +
                "\"pledgedAmount\":0," +
                "\"backers\":0," +
                "\"creator\":{\"name\":\"Some\",\"email\":\"" + email + "\"}}]"));
    }

    @Test
    public void shouldReturnNothingInProjectsQueryWhenProjectNotPublishedAndProjectCreatorNotRequestor() throws Exception {

        final String creatorEmail = "some@mail.com";
        final UserEntity creator = userEntity(creatorEmail, Roles.ROLE_USER);

        final String projectId = "existingProjectId";
        final ProjectEntity projectEntity = projectEntity(creator, projectId, "title", 44, "short description", "description", ProjectStatus.PROPOSED);
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(projectEntity));

        final String requestorEmail = "other@mail.com";
        userEntity(requestorEmail, Roles.ROLE_USER);

        final MvcResult mvcResult = mockMvc.perform(get("/projects")
                .principal(new UsernamePasswordAuthenticationToken(requestorEmail, "somepassword")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[]"));
    }

    @Test
    public void shouldReturnUnpublishedProjectInProjectsQueryWhenRequestorIsAdmin() throws Exception {

        final String creatorEmail = "some@mail.com";
        final UserEntity creator = userEntity(creatorEmail, Roles.ROLE_USER);

        final String projectId = "existingProjectId";
        final ProjectEntity projectEntity = projectEntity(creator, projectId, "title", 44, "short description", "description", ProjectStatus.PROPOSED);
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(projectEntity));

        final String requestorEmail = "other@mail.com";
        final UserEntity requestor = userEntity(requestorEmail, Roles.ROLE_USER, Roles.ROLE_ADMIN);

        final MvcResult mvcResult = mockMvc.perform(get("/projects")
                .principal(authentication(requestor)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[{\"id\":\"existingProjectId\",\"status\":\"PROPOSED\",\"title\":\"title\",\"shortDescription\":\"short description\",\"pledgeGoal\":44,\"pledgedAmount\":0,\"backers\":0,\"creator\":{\"name\":\"Some\",\"email\":\"some@mail.com\"}}]"));
    }

    @Test
    public void shouldReturnUnpublishedProjectInProjectsQueryWhenRequestorIsCreator() throws Exception {

        final String creatorEmail = "some@mail.com";
        final UserEntity creator = userEntity(creatorEmail, Roles.ROLE_USER);

        final String projectId = "existingProjectId";
        final ProjectEntity projectEntity = projectEntity(creator, projectId, "title", 44, "short description", "description", ProjectStatus.PROPOSED);
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(projectEntity));

        final MvcResult mvcResult = mockMvc.perform(get("/projects")
                .principal(new UsernamePasswordAuthenticationToken(creatorEmail, "somepassword")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[{\"id\":\"existingProjectId\",\"status\":\"PROPOSED\",\"title\":\"title\",\"shortDescription\":\"short description\",\"pledgeGoal\":44,\"pledgedAmount\":0,\"backers\":0,\"creator\":{\"name\":\"Some\",\"email\":\"some@mail.com\"}}]"));
    }

    @Test
    public void pledgeProject_shouldPledgeCorrectly() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);
        final ProjectEntity project = projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PUBLISHED);
        Pledge pledge = new Pledge(project.getPledgeGoal() - 4);

        int budgetBeforePledge = user.getBudget();

        when(financingRoundRepository.findActive(any())).thenReturn(new FinancingRoundEntity());

        mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pledge)))
                .andExpect(status().isCreated());

        PledgeEntity pledgeEntity = new PledgeEntity(project, user, pledge);
        verify(pledgeRepository).save(pledgeEntity);
        verify(userRepository).save(user);
        verify(projectRepository, never()).save(any(ProjectEntity.class));

        assertThat(user.getBudget(), is(budgetBeforePledge - pledge.getAmount()));
        assertThat(project.getStatus(), is(not(ProjectStatus.FULLY_PLEDGED)));
    }

    @Test
    public void pledgeProject_shouldSetTheProjectStatusToFullyPledged() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);
        final ProjectEntity project = projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PUBLISHED);

        final int budgetBeforePledge = user.getBudget();
        pledgeProject(project, user, project.getPledgeGoal() - 1);

        assertThat(project.getStatus(), is(not(ProjectStatus.FULLY_PLEDGED)));

        when(financingRoundRepository.findActive(any())).thenReturn(new FinancingRoundEntity());

        mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1)))
                .andExpect(status().isCreated());

        verify(pledgeRepository).save(any(PledgeEntity.class));
        verify(userRepository).save(user);
        verify(projectRepository).save(project);

        assertThat(user.getBudget(), is(budgetBeforePledge - 1));
        assertThat(project.getStatus(), is(ProjectStatus.FULLY_PLEDGED));
    }

    @Test
    public void pledgeProject_shouldRespondWith401IfTheUserWasNotFound() throws Exception {

        mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(new UsernamePasswordAuthenticationToken("foo@bar.com", "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void pledgeProject_shouldRespondWith404IfTheProjectWasNotFound() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        mockMvc.perform(post("/project/{projectId}/pledge", "some_foo_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(1))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfTheRequestObjectIsInvalid() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(0))))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"amount\":\"must be greater than or equal to 1\"}}"));
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfThePledgeGoalIsExceeded() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        final ProjectEntity project = projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PUBLISHED);

        // pledge project nearly fully
        pledgeProject(project, user, project.getPledgeGoal() - 1);

        when(financingRoundRepository.findActive(any())).thenReturn(new FinancingRoundEntity());

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(2))))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"pledge_goal_exceeded\",\"fieldViolations\":{}}"));
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfTheProjectIsAlreadyFullyPledged() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        final ProjectEntity project = projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PROPOSED);

        when(financingRoundRepository.findActive(any())).thenReturn(new FinancingRoundEntity());

        // fully pledge
        pledgeProject(project, user, project.getPledgeGoal());

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(1))))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"project_already_fully_pledged\",\"fieldViolations\":{}}"));
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfTheProjectIsNotPublished() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        final ProjectEntity project = projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PROPOSED);

        when(financingRoundRepository.findActive(any())).thenReturn(new FinancingRoundEntity());
        when(pledgeRepository.findByProject(project)).thenReturn(Collections.singletonList(new PledgeEntity(project, user, new Pledge(1))));

        project.setStatus(ProjectStatus.REJECTED);
        MvcResult mvcResult = getMvcResultForPledgedProject(user);
        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"project_not_published\",\"fieldViolations\":{}}"));

        project.setStatus(ProjectStatus.PROPOSED);
        mvcResult = getMvcResultForPledgedProject(user);
        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"project_not_published\",\"fieldViolations\":{}}"));
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfTheUserBudgetIsExceeded() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);
        user.setBudget(1);

        projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PUBLISHED);

        when(financingRoundRepository.findActive(any())).thenReturn(new FinancingRoundEntity());

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(2)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"user_budget_exceeded\",\"fieldViolations\":{}}"));
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfThereIsNoActiveFinancingRound() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER);

        projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PUBLISHED);

        when(financingRoundRepository.findActive(any())).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(user.getBudget() + 1))))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"no_financing_round_currently_active\",\"fieldViolations\":{}}"));
    }

    @Test
    public void testUpdateProjectToPublish() throws Exception {

        final String email = "some@mail.com";
        final UserEntity user = userEntity(email, Roles.ROLE_USER, Roles.ROLE_ADMIN);
        final ProjectEntity projectEntity = projectEntity(user, "some_id", "title", 44, "short description", "description", ProjectStatus.PROPOSED);
        final Project project = new Project(projectEntity, new ArrayList<>());
        project.setStatus(ProjectStatus.PUBLISHED);

        mockMvc.perform(patch("/project/{projectId}", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isOk());

        verify(projectRepository).save(projectEntity);
    }

    private MvcResult getMvcResultForPledgedProject(UserEntity user) throws Exception {
        return mockMvc.perform(post("/project/{projectId}/pledge", "some_id")
                .principal(authentication(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(1))))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private Principal authentication(UserEntity userEntity) {

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        userEntity.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return new UsernamePasswordAuthenticationToken(userEntity.getEmail(), "somepassword", authorities);
    }

    private void pledgeProject(ProjectEntity project, UserEntity user, int amount) {

        when(pledgeRepository.findByProject(project)).thenReturn(Collections.singletonList(new PledgeEntity(project, user, new Pledge(amount))));
        if (project.getPledgeGoal() == amount) {
            project.setStatus(ProjectStatus.FULLY_PLEDGED);
        }
    }

    private UserEntity userEntity(String email, String... roles) {

        UserEntity userEntity = new UserEntity(email);
        userEntity.setId("id_" + email);
        userEntity.setRoles(Arrays.asList(roles));
        userEntity.setBudget(4000);
        when(userRepository.findByEmail(email)).thenReturn(userEntity);
        return userEntity;
    }

    private Project project(String title, String description, String shortDescription, int pledgeGoal) {

        final Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setShortDescription(shortDescription);
        project.setPledgeGoal(pledgeGoal);
        return project;
    }

    private ProjectEntity projectEntity(UserEntity userEntity, String id, String title, int pledgeGoal, String shortDescription, String description, ProjectStatus status) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(id);
        projectEntity.setTitle(title);
        projectEntity.setPledgeGoal(pledgeGoal);
        projectEntity.setShortDescription(shortDescription);
        projectEntity.setDescription(description);
        projectEntity.setCreator(userEntity);
        projectEntity.setStatus(status);
        when(projectRepository.findOne(id)).thenReturn(projectEntity);
        return projectEntity;
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ControllerExceptionAdvice controllerExceptionAdvice() {
            return new ControllerExceptionAdvice();
        }

        @Bean
        public ProjectController projectController() {
            return new ProjectController();
        }

        @Bean
        public ProjectService projectService() {
            return new ProjectService();
        }

        @Bean
        public ProjectRepository projectRepository() {
            return mock(ProjectRepository.class);
        }

        @Bean
        public PledgeRepository pledgeRepository() {
            return mock(PledgeRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        public FinancingRoundRepository financingRoundRepository() {
            return mock(FinancingRoundRepository.class);
        }

        @Bean
        public UserNotificationService userNotificationService() {
            return mock(UserNotificationService.class);
        }

        @Bean
        public JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }

        @Bean
        public UserService userService() {
            return new UserService();
        }
    }
}
