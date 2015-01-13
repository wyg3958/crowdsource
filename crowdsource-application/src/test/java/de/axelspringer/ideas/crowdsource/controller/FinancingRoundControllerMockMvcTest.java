package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = FinancingRoundControllerMockMvcTest.Config.class)
public class FinancingRoundControllerMockMvcTest {

    @Autowired
    private FinancingRoundRepository financingRoundRepository;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void init() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        List<FinancingRoundEntity> financingRoundEntities = new ArrayList<>();
        financingRoundEntities.add(financingRoundEntity(new DateTime().minusDays(100), new DateTime().minusDays(50)));
        financingRoundEntities.add(financingRoundEntity(new DateTime().minusDays(40), new DateTime().minusDays(30)));
        when(financingRoundRepository.findAll()).thenReturn(financingRoundEntities);

        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(new UserEntity("test1@mail.com"));
        userEntities.add(new UserEntity("test2@mail.com"));
        when(userRepository.findAll()).thenReturn(userEntities);

        objectMapper.registerModule(new JodaModule());
    }

    @Test
    public void testFinancingRounds() throws Exception {

        final MvcResult mvcResult = mockMvc
                .perform(get("/financingrounds"))
                .andExpect(status().isOk())
                .andReturn();

        final String contentAsString = mvcResult.getResponse().getContentAsString();
        final List<FinancingRound> financingRounds = objectMapper.readValue(contentAsString, new TypeReference<List<FinancingRound>>() {
        });

        assertEquals(2, financingRounds.size());
        verify(financingRoundRepository, times(1)).findAll();
    }

    @Test
    public void testStartFinancingRound() throws Exception {

        // create round
        mockMvc.perform(post("/financingrounds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financingRound(new DateTime().plusDays(1), 99))))
                .andExpect(status().isCreated());

        verify(financingRoundRepository, times(1)).save(any(FinancingRoundEntity.class));
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(2)).save(any(UserEntity.class));
    }

    @Test
    public void testStartFinancingRoundEndDateNotInFuture() throws Exception {

        // attempt to start a round that ends in the past
        final MvcResult mvcResult = mockMvc.perform(post("/financingrounds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financingRound(new DateTime(), 99))))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundRepository, times(0)).save(any(FinancingRoundEntity.class));
        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(0)).save(any(UserEntity.class));

        final String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, containsString("end-date-in-future"));
    }

    @Test
    public void testStartFinancingRoundBudgetTooLow() throws Exception {

        // attempt to create round with 0-budget
        final MvcResult mvcResult = mockMvc.perform(post("/financingrounds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financingRound(new DateTime().plusDays(1), 0))))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundRepository, times(0)).save(any(FinancingRoundEntity.class));
        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(0)).save(any(UserEntity.class));

        final String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, containsString("at-least-one-dollar"));
    }

    @Test
    public void testStartFinancingRoundCollidingRounds() throws Exception {

        // create currently running financing round
        when(financingRoundRepository.findAll()).thenReturn(Collections.singletonList(financingRoundEntity(new DateTime().minusDays(5), new DateTime().plusDays(1))));

        // attempt to create a new (otherwise valid) one
        final MvcResult mvcResult = mockMvc.perform(post("/financingrounds")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financingRound(new DateTime().plusDays(1), 99))))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundRepository, times(0)).save(any(FinancingRoundEntity.class));
        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(0)).save(any(UserEntity.class));

        final String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, containsString("non-colliding"));
    }

    private FinancingRound financingRound(DateTime end, Integer value) {
        FinancingRound financingRound = new FinancingRound();
        financingRound.setEnd(end);
        financingRound.setValue(value);
        return financingRound;
    }

    private FinancingRoundEntity financingRoundEntity(DateTime start, DateTime end) {

        FinancingRoundEntity reference = new FinancingRoundEntity();
        reference.setStartDate(start);
        reference.setEndDate(end);
        return reference;
    }

    @Configuration
    @EnableWebMvc
    static class Config {

        @Bean
        public ControllerExceptionAdvice controllerExceptionAdvice() {
            return new ControllerExceptionAdvice();
        }

        @Bean
        public FinancingRoundController financingRoundController() {
            return new FinancingRoundController();
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        public FinancingRoundRepository financingRoundRepository() {
            return mock(FinancingRoundRepository.class);
        }
    }
}