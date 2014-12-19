package de.axelspringer.ideas.crowdsource.controller;

import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ProjectsControllerTest.Config.class)
public class ProjectsControllerTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(projectRepository);
        reset(userRepository);

        List<ProjectEntity> entities = Arrays.asList(
                createProjectEntity("projectId1", "myTitle1", 123, "myShortDescription1", "user1@domain.com"),
                createProjectEntity("projectId2", "myTitle2", 456, "myShortDescription2", "user2@domain.com"),
                createProjectEntity("projectId3", "myTitle3", 789, "myShortDescription3", "user3@domain.com")
        );

        when(projectRepository.findByPublicationStatusOrderByCreatedDateDesc(any())).thenReturn(entities);
    }

    private ProjectEntity createProjectEntity(String id, String title, int pledgeGoal, String shortDescription, String userEmail) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(id);
        projectEntity.setTitle(title);
        projectEntity.setPledgeGoal(pledgeGoal);
        projectEntity.setShortDescription(shortDescription);
        projectEntity.setUser(new UserEntity(userEmail));
        return projectEntity;
    }

    @Test
    public void shouldReturnAllPublishedProjectsOnGet() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[" +
                "{\"id\":\"projectId1\",\"title\":\"myTitle1\",\"shortDescription\":\"myShortDescription1\",\"pledgeGoal\":123}," +
                "{\"id\":\"projectId2\",\"title\":\"myTitle2\",\"shortDescription\":\"myShortDescription2\",\"pledgeGoal\":456}," +
                "{\"id\":\"projectId3\",\"title\":\"myTitle3\",\"shortDescription\":\"myShortDescription3\",\"pledgeGoal\":789}]"));

        verify(projectRepository).findByPublicationStatusOrderByCreatedDateDesc(PublicationStatus.PUBLISHED);
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ProjectsController projectsController() {
            return new ProjectsController();
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
