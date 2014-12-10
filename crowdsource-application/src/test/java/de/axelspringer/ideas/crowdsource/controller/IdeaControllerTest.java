package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.IdeaEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.idea.IdeaStorage;
import de.axelspringer.ideas.crowdsource.repository.IdeaRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = IdeaControllerTest.Config.class)
public class IdeaControllerTest {

    private static final String EXISTING_USER_MAIL = "existing@mail.com";
    private static final String NON_EXISTING_USER_MAIL = "nonexisting@mail.com";

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private UserEntity existingUserEntity;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(ideaRepository);
        reset(userRepository);

        existingUserEntity = new UserEntity(EXISTING_USER_MAIL);
        when(userRepository.findByEmail(EXISTING_USER_MAIL)).thenReturn(existingUserEntity);
        when(userRepository.findByEmail(NON_EXISTING_USER_MAIL)).thenReturn(null);
    }

    @Test
    public void shouldReturnSuccessfullyOnSave() throws Exception {
        final IdeaStorage ideaStorage = new IdeaStorage();
        ideaStorage.setTitle("myTitle");
        ideaStorage.setFullDescription("theFullDescription");
        ideaStorage.setShortDescription("theShortDescription");
        ideaStorage.setCurrentFunding(50);

        mockMvc.perform(post("/idea")
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ideaStorage)))
                .andExpect(status().isCreated());

        ArgumentCaptor<IdeaEntity> ideaEntityCaptor = ArgumentCaptor.forClass(IdeaEntity.class);
        verify(ideaRepository).save(ideaEntityCaptor.capture());

        IdeaEntity ideaEntity = ideaEntityCaptor.getValue();
        assertThat(ideaEntity.getTitle(), is("myTitle"));
        assertThat(ideaEntity.getShortDescription(), is("theShortDescription"));
        assertThat(ideaEntity.getFullDescription(), is("theFullDescription"));
        assertThat(ideaEntity.getUser(), is(existingUserEntity));
    }

    @Test
    public void shouldRespondWith401IfUserWasNotFound() throws Exception {
        final IdeaStorage ideaStorage = new IdeaStorage();
        ideaStorage.setTitle("myTitle");
        ideaStorage.setFullDescription("theFullDescription");
        ideaStorage.setShortDescription("theShortDescription");
        ideaStorage.setCurrentFunding(50);

        mockMvc.perform(post("/idea")
                .principal(new UsernamePasswordAuthenticationToken(NON_EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ideaStorage)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRespondWith400IfRequestWasInvalid() throws Exception {
        final IdeaStorage ideaStorage = new IdeaStorage();

        mockMvc.perform(post("/idea")
                .principal(new UsernamePasswordAuthenticationToken(EXISTING_USER_MAIL, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ideaStorage)))
                .andExpect(status().isBadRequest());
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

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }
}
