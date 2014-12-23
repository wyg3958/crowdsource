package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.user.User;
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
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private UserEntity existingUserEntity;
    private Project existingProject;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(projectService);
        reset(userRepository);

        existingUserEntity = new UserEntity(EXISTING_USER_MAIL);
        existingUserEntity.setId("existingUserId");

        when(userRepository.findByEmail(EXISTING_USER_MAIL)).thenReturn(existingUserEntity);
        when(userRepository.findByEmail(NON_EXISTING_USER_MAIL)).thenReturn(null);

        existingProject = createProject(EXISTING_PROJECT_ID, "title", "short description", "description", 44, 10, 2);
        when(projectService.getProject(EXISTING_PROJECT_ID)).thenReturn(existingProject);

        List<Project> projects = Arrays.asList(
                createProject("projectId1", "myTitle1", "myShortDescription1", "description1", 1000, 0, 0),
                createProject("projectId2", "myTitle2", "myShortDescription2", "description2", 5000, 2500, 1),
                createProject("projectId3", "myTitle3", "myShortDescription3", "description3", 100, 100, 3)
        );
        when(projectService.getProjects()).thenReturn(projects);
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

        verify(projectService).addProject(eq(project), eq(existingUserEntity));
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
                "\"pledgedAmount\":10," +
                "\"backers\":2," +
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
                "{\"id\":\"projectId1\",\"title\":\"myTitle1\",\"shortDescription\":\"myShortDescription1\",\"pledgeGoal\":1000,\"pledgedAmount\":0,\"backers\":0}," +
                "{\"id\":\"projectId2\",\"title\":\"myTitle2\",\"shortDescription\":\"myShortDescription2\",\"pledgeGoal\":5000,\"pledgedAmount\":2500,\"backers\":1}," +
                "{\"id\":\"projectId3\",\"title\":\"myTitle3\",\"shortDescription\":\"myShortDescription3\",\"pledgeGoal\":100,\"pledgedAmount\":100,\"backers\":3}]"));

        verify(projectService).getProjects();
    }

    @Test
    public void pledgeProject_shouldCallTheProjectServiceCorrectly() throws Exception {
        Pledge pledge = new Pledge(35);

        mockMvc.perform(post("/project/{projectId}/pledge", EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(pledge)))
                .andExpect(status().isCreated());

        verify(projectService).pledgeProject(eq(EXISTING_PROJECT_ID), eq(existingUserEntity), eq(pledge));
    }

    @Test
    public void pledgeProject_shouldRespondWith401IfTheUserWasNotFound() throws Exception {

        mockMvc.perform(post("/project/{projectId}/pledge", EXISTING_PROJECT_ID)
                .principal(new UsernamePasswordAuthenticationToken(NON_EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new Pledge(35))))
                .andExpect(status().isUnauthorized());
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

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ProjectController projectController() {
            return new ProjectController();
        }

        @Bean
        public ProjectService projectService() {
            return mock(ProjectService.class);
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }
}
