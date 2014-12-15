package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.enums.PublicationStatus;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
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
import java.util.ArrayList;
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
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(projectRepository);
        reset(userRepository);
    }

    @Test
    public void shouldReturnAllProjectsOnGet() throws Exception {

        ProjectEntity entity1 = new ProjectEntity();
        final String myTitle1 = "myTitle1";
        final String myShortDescription1 = "myShortDescription1";
        final String myTitle2 = "myTitle2";
        final String myShortDescription2 = "myShortDescription2";
        final String myTitle3 = "myTitle3";
        final String myShortDescription3 = "myShortDescription3";

        entity1.setTitle(myTitle1);
        entity1.setShortDescription(myShortDescription1);

        ProjectEntity entity2 = new ProjectEntity();
        entity2.setTitle(myTitle2);
        entity2.setShortDescription(myShortDescription2);

        ProjectEntity entity3 = new ProjectEntity();
        entity3.setTitle(myTitle3);
        entity3.setShortDescription(myShortDescription3);

        List<ProjectEntity> entities = new ArrayList<>();
        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        when(projectRepository.findByPublicationStatus(any())).thenReturn(entities);

        final MvcResult mvcResult = mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andReturn();

        final List<ProjectEntity> projects = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ProjectEntity>>() {
        });

        verify(projectRepository).findByPublicationStatus(PublicationStatus.PUBLISHED);

        assertThat(projects.get(0).getTitle(), is(myTitle1));
        assertThat(projects.get(1).getTitle(), is(myTitle2));
        assertThat(projects.get(2).getTitle(), is(myTitle3));

        assertThat(projects.get(0).getShortDescription(), is(myShortDescription1));
        assertThat(projects.get(1).getShortDescription(), is(myShortDescription2));
        assertThat(projects.get(2).getShortDescription(), is(myShortDescription3));
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
