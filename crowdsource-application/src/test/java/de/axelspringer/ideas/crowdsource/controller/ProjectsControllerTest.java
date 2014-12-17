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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

        ProjectEntity entity1 = new ProjectEntity();
        entity1.setTitle("myTitle1");
        entity1.setPledgeGoal(123);
        entity1.setShortDescription("myShortDescription1");
        entity1.setUser(new UserEntity("user1@domain.com"));

        ProjectEntity entity2 = new ProjectEntity();
        entity2.setTitle("myTitle2");
        entity2.setPledgeGoal(456);
        entity2.setShortDescription("myShortDescription2");
        entity2.setUser(new UserEntity("user2@domain.com"));

        ProjectEntity entity3 = new ProjectEntity();
        entity3.setTitle("myTitle3");
        entity3.setPledgeGoal(789);
        entity3.setShortDescription("myShortDescription3");
        entity3.setUser(new UserEntity("user3@domain.com"));

        List<ProjectEntity> entities = Arrays.asList(entity1, entity2, entity3);

        when(projectRepository.findByPublicationStatus(any())).thenReturn(entities);
    }

    @Test
    public void shouldReturnAllPublishedProjectsOnGet() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[" +
                "{\"title\":\"myTitle1\",\"shortDescription\":\"myShortDescription1\",\"pledgeGoal\":123}," +
                "{\"title\":\"myTitle2\",\"shortDescription\":\"myShortDescription2\",\"pledgeGoal\":456}," +
                "{\"title\":\"myTitle3\",\"shortDescription\":\"myShortDescription3\",\"pledgeGoal\":789}]"));

        verify(projectRepository).findByPublicationStatus(PublicationStatus.PUBLISHED);
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
