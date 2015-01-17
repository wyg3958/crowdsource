package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.CommentEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.ProjectEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.Comment;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.repository.CommentRepository;
import de.axelspringer.ideas.crowdsource.repository.ProjectRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = CommentControllerTest.Config.class)
public class CommentControllerTest {

    private final static String TEST_PROJECT_ID = "TEST_PROJECT_ID";

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void init() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        reset(projectRepository);

        final UserEntity userEntity = new UserEntity("test@test.de", "password");
        final ProjectEntity projectEntity = new ProjectEntity(userEntity, new Project());
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        when(projectRepository.findOne(TEST_PROJECT_ID)).thenReturn(projectEntity);
        when(commentRepository.findByProject(projectEntity)).thenReturn(Arrays.asList(new CommentEntity(projectEntity, userEntity, "some comment")));
    }

    @Test
    public void testComments() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(get("/project/" + TEST_PROJECT_ID + "/comments"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[{\"created\":null,\"userName\":\"test@test.de\",\"comment\":\"some comment\"}]"));

        verify(projectRepository).findOne(TEST_PROJECT_ID);
        verify(commentRepository).findByProject(any(ProjectEntity.class));
    }

    @Test
    public void testStoreCommentValidComment() throws Exception {

        Comment comment = new Comment();
        comment.setComment("this is an example comment that respects the length constraint");
        mockMvc.perform(post("/project/" + TEST_PROJECT_ID + "/comment")
                .principal(new UsernamePasswordAuthenticationToken("test@test.de", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk());

        verify(projectRepository).findOne(TEST_PROJECT_ID);
        verify(userRepository).findByEmail("test@test.de");
        verify(commentRepository).save(any(CommentEntity.class));
    }

    @Test
    public void testStoreCommentCommentTooShort() throws Exception {

        Comment comment = new Comment();
        comment.setComment("this is too short");

        final MvcResult mvcResult = mockMvc.perform(post("/project/" + TEST_PROJECT_ID + "/comment")
                .principal(new UsernamePasswordAuthenticationToken("test@test.de", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest()).andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"comment\":\"length-constraint-respected\"}}"));

        verify(projectRepository, times(0)).findOne(TEST_PROJECT_ID);
        verify(userRepository, times(0)).findByEmail("test@test.de");
        verify(commentRepository, times(0)).save(any(CommentEntity.class));
    }

    @Test
    public void testStoreCommentCommentTooLong() throws Exception {

        Comment comment = new Comment();

        StringBuilder tooLong = new StringBuilder();
        String myComment = "this is too long";
        for (int i = 0; i < 1100 / myComment.length(); i++) {
            tooLong.append(myComment);
        }
        comment.setComment(tooLong.toString());

        final MvcResult mvcResult = mockMvc.perform(post("/project/" + TEST_PROJECT_ID + "/comment")
                .principal(new UsernamePasswordAuthenticationToken("test@test.de", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest()).andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"field_errors\",\"fieldViolations\":{\"comment\":\"length-constraint-respected\"}}"));

        verify(projectRepository, times(0)).findOne(TEST_PROJECT_ID);
        verify(userRepository, times(0)).findByEmail("test@test.de");
        verify(commentRepository, times(0)).save(any(CommentEntity.class));
    }


    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ControllerExceptionAdvice controllerExceptionAdvice() {
            return new ControllerExceptionAdvice();
        }

        @Bean
        public CommentController commentController() {
            return new CommentController();
        }

        @Bean
        public CommentRepository commentRepository() {
            return mock(CommentRepository.class);
        }

        @Bean
        public ProjectRepository projectRepository() {
            return mock(ProjectRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        public UserService userService() {
            return new UserService();
        }
    }
}