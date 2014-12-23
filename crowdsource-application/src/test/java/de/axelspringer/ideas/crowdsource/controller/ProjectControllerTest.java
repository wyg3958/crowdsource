package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.PledgeEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.PledgeRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.ProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ProjectControllerTest.Config.class)
public class ProjectControllerTest {

    private static final String EXISTING_USER_MAIL = "existing@mail.com";
    private static final String NON_EXISTING_USER_MAIL = "nonexisting@mail.com";
    private static final String EXISTING_PROJECT_ID = "existingProjectId";
    private static final String NON_EXISTING_PROJECT_ID = "nonexistingProjectId";

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PledgeRepository pledgeRepository;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private UserEntity existingUserEntity;
    private ProjectEntity existingProject;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(projectRepository);
        reset(pledgeRepository);
        reset(userRepository);

        existingUserEntity = new UserEntity(EXISTING_USER_MAIL);
        existingUserEntity.setId("existingUserId");

        when(userRepository.findByEmail(EXISTING_USER_MAIL)).thenReturn(existingUserEntity);
        when(userRepository.findByEmail(NON_EXISTING_USER_MAIL)).thenReturn(null);

        existingProject = createProjectEntity(EXISTING_PROJECT_ID, "title", 44, "short description", "description");
        when(projectRepository.findOne(EXISTING_PROJECT_ID)).thenReturn(existingProject);
        when(pledgeRepository.findByProject(eq(existingProject))).thenReturn(Arrays.asList(
                new PledgeEntity(existingProject, existingUserEntity, new Pledge(3))));

        ProjectEntity project1 = createProjectEntity("projectId1", "title1", 11, "short description1", "description1");
        ProjectEntity project2 = createProjectEntity("projectId2", "title2", 22, "short description2", "description2");
        ProjectEntity project3 = createProjectEntity("projectId3", "title3", 33, "short description3", "description3");
        List<ProjectEntity> projects = Arrays.asList(project1, project2, project3);

        when(projectRepository.findByPublicationStatusOrderByCreatedDateDesc(any())).thenReturn(projects);

        when(pledgeRepository.findByProject(eq(project1))).thenReturn(Arrays.asList(
                new PledgeEntity(project1, existingUserEntity, new Pledge(11))));
        when(pledgeRepository.findByProject(eq(project2))).thenReturn(Arrays.asList(
                new PledgeEntity(project2, existingUserEntity, new Pledge(2)),
                new PledgeEntity(project2, existingUserEntity, new Pledge(20))));
        when(pledgeRepository.findByProject(eq(project3))).thenReturn(Arrays.asList());
    }

    @Test
    public void addProject_shouldReturnSuccessfully() throws Exception {
        final Project project = new Project();
        project.setTitle("myTitle");
        project.setDescription("theFullDescription");
        project.setShortDescription("theShortDescription");
        project.setPledgeGoal(50);

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isCreated());

        ProjectEntity projectEntity = new ProjectEntity(existingUserEntity, project);
        verify(projectRepository).save(eq(projectEntity));
    }

    @Test
    public void addProject_shouldRespondWith401IfUserWasNotFound() throws Exception {
        final Project project = new Project();
        project.setTitle("myTitle");
        project.setDescription("theFullDescription");
        project.setShortDescription("theShortDescription");
        project.setPledgeGoal(50);

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken(NON_EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void addProject_shouldRespondWith400IfRequestWasInvalid() throws Exception {
        final Project project = new Project();

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(project)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getProject_shouldReturnSuccessfully() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}", EXISTING_PROJECT_ID))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{" +
                "\"id\":\"existingProjectId\"," +
                "\"title\":\"title\"," +
                "\"shortDescription\":\"short description\"," +
                "\"description\":\"description\"," +
                "\"pledgeGoal\":44," +
                "\"pledgedAmount\":3," +
                "\"backers\":1," +
                "\"creator\":{\"id\":\"existingUserId\",\"name\":\"Existing\"}}"));
    }

    @Test
    public void getProject_shouldRespondWith404OnInvalidProjectId() throws Exception {
        mockMvc.perform(get("/project/{projectId}", NON_EXISTING_PROJECT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProjects_shouldReturnSuccessfully() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[" +
                "{\"id\":\"projectId1\",\"title\":\"title1\",\"shortDescription\":\"short description1\",\"pledgeGoal\":11,\"pledgedAmount\":11,\"backers\":1}," +
                "{\"id\":\"projectId2\",\"title\":\"title2\",\"shortDescription\":\"short description2\",\"pledgeGoal\":22,\"pledgedAmount\":22,\"backers\":1}," +
                "{\"id\":\"projectId3\",\"title\":\"title3\",\"shortDescription\":\"short description3\",\"pledgeGoal\":33,\"pledgedAmount\":0,\"backers\":0}]"));

        verify(projectRepository).findByPublicationStatusOrderByCreatedDateDesc(PublicationStatus.PUBLISHED);
    }

    @Test
    public void pledgeProject_shouldCallTheProjectServiceCorrectly() throws Exception {
        Pledge pledge = new Pledge(35);

        mockMvc.perform(post("/project/{projectId}/pledge", EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pledge)))
                .andExpect(status().isCreated());

        PledgeEntity pledgeEntity = new PledgeEntity(existingProject, existingUserEntity, pledge);
        verify(pledgeRepository).save(pledgeEntity);
    }

    @Test
    public void pledgeProject_shouldRespondWith401IfTheUserWasNotFound() throws Exception {

        mockMvc.perform(post("/project/{projectId}/pledge", EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(NON_EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(35))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void pledgeProject_shouldRespondWith404IfTheProjectWasNotFound() throws Exception {

        mockMvc.perform(post("/project/{projectId}/pledge", NON_EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(35))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfTheRequestObjectIsInvalid() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(0))))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"amount\":\"must be greater than or equal to 1\"}}"));
    }

    @Test
    public void pledgeProject_shouldRespondWith400IfThePledgeGoalIsExceeded() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/project/{projectId}/pledge", EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(42)))) // pledge goal is 44, 3 was already pledged
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"pledge_goal_exceeded\",\"fieldViolations\":{}}"));
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
    }
}
