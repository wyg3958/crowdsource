package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        reset(userRepository);

        existingUserEntity = new UserEntity(EXISTING_USER_MAIL);
        existingUserEntity.setId("existingUserId");
        when(userRepository.findByEmail(EXISTING_USER_MAIL)).thenReturn(existingUserEntity);
        when(userRepository.findByEmail(NON_EXISTING_USER_MAIL)).thenReturn(null);

        existingProject = new ProjectEntity();
        existingProject.setId(EXISTING_PROJECT_ID);
        existingProject.setTitle("title");
        existingProject.setPublicationStatus(PublicationStatus.PUBLISHED);
        existingProject.setPledgeGoal(44);
        existingProject.setDescription("description");
        existingProject.setShortDescription("short description");
        existingProject.setUser(existingUserEntity);
        when(projectRepository.findOne(EXISTING_PROJECT_ID)).thenReturn(existingProject);
    }

    @Test
    public void shouldReturnSuccessfullyOnSave() throws Exception {
        final Project projectStorage = new Project();
        projectStorage.setTitle("myTitle");
        projectStorage.setDescription("theFullDescription");
        projectStorage.setShortDescription("theShortDescription");
        projectStorage.setPledgeGoal(50);

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectStorage)))
                .andExpect(status().isCreated());

        ArgumentCaptor<ProjectEntity> ideaEntityCaptor = ArgumentCaptor.forClass(ProjectEntity.class);
        verify(projectRepository).save(ideaEntityCaptor.capture());

        ProjectEntity projectEntity = ideaEntityCaptor.getValue();
        assertThat(projectEntity.getTitle(), is("myTitle"));
        assertThat(projectEntity.getShortDescription(), is("theShortDescription"));
        assertThat(projectEntity.getDescription(), is("theFullDescription"));
        assertThat(projectEntity.getUser(), is(existingUserEntity));
    }

    @Test
    public void shouldRespondWith401IfUserWasNotFound() throws Exception {
        final Project projectStorage = new Project();
        projectStorage.setTitle("myTitle");
        projectStorage.setDescription("theFullDescription");
        projectStorage.setShortDescription("theShortDescription");
        projectStorage.setPledgeGoal(50);

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken(NON_EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectStorage)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRespondWith400IfRequestWasInvalid() throws Exception {
        final Project projectStorage = new Project();

        mockMvc.perform(post("/project")
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectStorage)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnProject() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}", EXISTING_PROJECT_ID))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{" +
                "\"title\":\"title\"," +
                "\"shortDescription\":\"short description\"," +
                "\"description\":\"description\"," +
                "\"pledgeGoal\":44," +
                "\"creator\":{\"id\":\"existingUserId\",\"name\":\"Existing\"}}"));
    }

    @Test
    public void shouldRespondWith404IfNoProjectFound() throws Exception {
        mockMvc.perform(get("/project/{projectId}", NON_EXISTING_PROJECT_ID))
                .andExpect(status().isNotFound());
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ProjectController projectController() {
            return new ProjectController();
        }

        @Bean
        public ProjectRepository projectRepository() {
            return mock(ProjectRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }
}
