package de.axelspringer.ideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.axelspringer.ideas.crowdsource.model.persistence.FinancingRoundEntity;
import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.repository.FinancingRoundRepository;
import de.axelspringer.ideas.crowdsource.repository.UserRepository;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private DateTime fixedDate;

    @Before
    public void init() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(financingRoundRepository);
        reset(userRepository);

        List<FinancingRoundEntity> financingRoundEntities = new ArrayList<>();

        fixedDate = DateTime.parse("2015-01-10T10:10:10Z");
        financingRoundEntities.add(financingRoundEntity(fixedDate.minusDays(100), fixedDate.minusDays(50)));
        financingRoundEntities.add(financingRoundEntity(fixedDate.minusDays(40), fixedDate.minusDays(30)));
        when(financingRoundRepository.findAll()).thenReturn(financingRoundEntities);

        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(new UserEntity("test1@mail.com"));
        userEntities.add(new UserEntity("test2@mail.com"));
        when(userRepository.findAll()).thenReturn(userEntities);

        objectMapper.registerModule(new JodaModule());
    }

    @Test
    public void testFinancingRounds() throws Exception {

        when(financingRoundRepository.findActive(any()))
                .thenReturn(financingRoundEntity(fixedDate.minusDays(100), fixedDate.minusDays(50)));

        final MvcResult mvcResult = mockMvc
                .perform(get("/financingrounds"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("[" +
                "{\"id\":null,\"startDate\":1412244610000,\"endDate\":1416564610000,\"budget\":null,\"active\":false}," +
                "{\"id\":null,\"startDate\":1417428610000,\"endDate\":1418292610000,\"budget\":null,\"active\":false}]"));
        verify(financingRoundRepository, times(1)).findAll();
    }

    @Test
    public void testGetActiveFinancingRound() throws Exception {

        when(financingRoundRepository.findActive(any()))
                .thenReturn(financingRoundEntity(fixedDate.minusDays(100), fixedDate.minusDays(50)));

        final MvcResult mvcResult = mockMvc
                .perform(get("/financinground/active"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"id\":null,\"startDate\":1412244610000,\"endDate\":1416564610000,\"active\":false}"));
        verify(financingRoundRepository, times(1)).findActive(any());
    }

    @Test
    public void testGetActiveFinancingRoundShouldReturn404IfNoneIsActive() throws Exception {

        when(financingRoundRepository.findActive(any())).thenReturn(null);

        mockMvc.perform(get("/financinground/active"))
                .andExpect(status().isNotFound());

        verify(financingRoundRepository, times(1)).findActive(any());
    }

    @Test
    public void testStartFinancingRound() throws Exception {

        // create round
        mockMvc.perform(post("/financinground")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(financingRound(new DateTime().plusDays(1), 99))))
                .andExpect(status().isCreated());

        verify(financingRoundRepository).save(any(FinancingRoundEntity.class));

        verify(financingRoundRepository, times(1)).save(any(FinancingRoundEntity.class));
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(2)).save(any(UserEntity.class));
    }

    @Test
    public void testStartFinancingRoundEndDateNotInFuture() throws Exception {

        // attempt to start a round that ends in the past
        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
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
        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
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
        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
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

    @Test
    public void testStopFinancingRound() throws Exception {
        final DateTime futureDate = fixedDate.plusDays(5000);
        when(financingRoundRepository.findOne(anyString())).thenReturn(financingRoundEntity(fixedDate.minusDays(100), futureDate));

        // stop round
        final MvcResult mvcResult = mockMvc.perform(put("/financinground/4711/cancel"))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<FinancingRoundEntity> entityCaptor = ArgumentCaptor.forClass(FinancingRoundEntity.class);
        verify(financingRoundRepository, times(1)).save(entityCaptor.capture());
        verify(financingRoundRepository, times(1)).findOne("4711");

        assertThat(entityCaptor.getValue().getEndDate(), not(futureDate));
        assertThat(mvcResult.getResponse().getContentAsString(), Matchers.containsString("{\"id\":null,\"startDate\":1412244610000,"));
        assertThat(mvcResult.getResponse().getContentAsString(), Matchers.containsString(",\"budget\":null,\"active\":false}"));
    }

    @Test
    public void testStopFinancingRoundMissingRound() throws Exception {
        when(financingRoundRepository.findOne(anyString())).thenReturn(null);

        // stop round
        mockMvc.perform(put("/financinground/4711/cancel"))
                .andExpect(status().isNotFound());

        verify(financingRoundRepository, times(0)).save(any(FinancingRoundEntity.class));
        verify(financingRoundRepository, times(1)).findOne("4711");
    }

    @Test
    public void testStopFinancingRoundAlreadyStoppedRound() throws Exception {
        when(financingRoundRepository.findOne(anyString())).thenReturn(financingRoundEntity(fixedDate.minusDays(100), fixedDate.minusDays(50)));

        // stop round
        final MvcResult mvcResult = mockMvc.perform(put("/financinground/4711/cancel"))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundRepository, times(0)).save(any(FinancingRoundEntity.class));
        verify(financingRoundRepository, times(1)).findOne("4711");

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"financing_round_already_stopped\",\"fieldViolations\":{}}"));

    }

    private FinancingRound financingRound(DateTime end, Integer value) {
        FinancingRound financingRound = new FinancingRound();
        financingRound.setEndDate(end);
        financingRound.setBudget(value);
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