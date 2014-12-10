package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.IdeaEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.idea.IdeaStorage;
import de.axelspringer.ideas.crowdsource.repository.IdeaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IdeaControllerTest.Config.class)
public class IdeaControllerTest {

    @Autowired
    private IdeaController ideaController;

    @Autowired
    private IdeaRepository ideaRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(ideaRepository);
    }

    @Test
    public void shouldReturnSuccessfullyOnSave() throws Exception {
        final IdeaStorage ideaStorage = new IdeaStorage();
        ideaStorage.setTitle("myTitle");
        ideaStorage.setFullDescription("theFullDescription");
        ideaStorage.setShortDescription("theShortDescription");

        mockMvc.perform(post("/idea")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ideaStorage)))
                .andExpect(status().isCreated());

        ArgumentCaptor<IdeaEntity> ideaEntityCaptor = ArgumentCaptor.forClass(IdeaEntity.class);
        verify(ideaRepository).save(ideaEntityCaptor.capture());
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public IdeaController ideaController() {
            return new IdeaController();
        }

        @Bean
        public IdeaRepository ideaRepository() {
            return mock(IdeaRepository.class);
        }
    }
}
