package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.User;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import de.axelspringer.ideas.crowdsource.service.UserService;
import de.axelspringer.ideas.crowdsource.testsupport.UserControllerTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = UserControllerTestConfig.class)
public class UserControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private User user = new User();
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldReturnSuccessfullyWhenEmailAndTosOkOnSave() throws Exception {

        user.setEmail("test@axelspringer.de");
        user.setTermsOfServiceAccepted(true);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldCallAllRelevantMethodsOnSave() throws Exception {

        user.setEmail("test@axelspringer.de");
        user.setTermsOfServiceAccepted(true);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)));

        verify(userService).generateActivationToken();
        verify(userService).sendActivationMail(any());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void shouldReturnErroneouslyWhenEmailNotAxelspringerOnSave() throws Exception {

        user.setEmail("test@test.de");
        user.setTermsOfServiceAccepted(true);

        final MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("", "{\"fieldViolations\":{\"email\":\"not_eligible\"}}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnErroneouslyWhenTocNotAcceptedOnSave() throws Exception {

        user.setEmail("test@axelspringer.de");
        user.setTermsOfServiceAccepted(false);

        final MvcResult mvcResult = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("", "{\"fieldViolations\":{\"termsOfServiceAccepted\":\"must be true\"}}", mvcResult.getResponse().getContentAsString());
    }
}