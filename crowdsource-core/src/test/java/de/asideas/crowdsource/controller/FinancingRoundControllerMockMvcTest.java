package de.asideas.crowdsource.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import de.asideas.crowdsource.domain.exception.InvalidRequestException;
import de.asideas.crowdsource.domain.exception.ResourceNotFoundException;
import de.asideas.crowdsource.domain.model.FinancingRoundEntity;
import de.asideas.crowdsource.domain.model.UserEntity;
import de.asideas.crowdsource.domain.presentation.FinancingRound;
import de.asideas.crowdsource.repository.FinancingRoundRepository;
import de.asideas.crowdsource.service.FinancingRoundService;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = FinancingRoundControllerMockMvcTest.Config.class)
public class FinancingRoundControllerMockMvcTest {

    @Autowired
    private FinancingRoundRepository financingRoundRepository;

    @Autowired
    private FinancingRoundService financingRoundService;

    private ObjectMapper mapper = new ObjectMapper();

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private DateTime fixedDate;

    @Before
    public void init() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        reset(financingRoundRepository, financingRoundService);

        List<FinancingRoundEntity> financingRoundEntities = new ArrayList<>();

        fixedDate = DateTime.parse("2015-01-10T10:10:10Z");
        financingRoundEntities.add(financingRoundEntity(fixedDate.minusDays(100), fixedDate.minusDays(50)));
        financingRoundEntities.add(financingRoundEntity(fixedDate.minusDays(40), fixedDate.minusDays(30)));
        when(financingRoundRepository.findAll()).thenReturn(financingRoundEntities);

        List<UserEntity> userEntities = new ArrayList<>();
        userEntities.add(new UserEntity("test1@mail.com"));
        userEntities.add(new UserEntity("test2@mail.com"));

        mapper.registerModule(new JodaModule());

        when(financingRoundRepository.save(any(FinancingRoundEntity.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
    }

    @Test
    public void allFinancingRounds() throws Exception {

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
    public void startFinancingRound() throws Exception {

        // create round
        final FinancingRound financingRoundCreationCmd = financingRound(new DateTime().plusDays(1), 99);
        final FinancingRound expectedFinancingRound = anExpectedFinancingRound();

        ArgumentCaptor<FinancingRound> cmdCaptor = ArgumentCaptor.forClass(FinancingRound.class);

        when(financingRoundService.startNewFinancingRound(
                cmdCaptor.capture())).thenReturn(expectedFinancingRound);

        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(financingRoundCreationCmd)))
                .andExpect(status().isCreated())
                .andReturn();

        assertFinancingRoundsEqual(cmdCaptor.getValue(), financingRoundCreationCmd);

        final FinancingRound actRes = mapper.readValue(mvcResult.getResponse().getContentAsString(), FinancingRound.class);
        assertFinancingRoundsEqual(expectedFinancingRound, actRes);
    }

    @Test
    public void startFinancingRoundEndDateNotInFuture() throws Exception {
        // attempt to start a round that ends in the past
        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(financingRound(new DateTime(), 99))))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundService, times(0)).startNewFinancingRound(any(FinancingRound.class));

        final String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, containsString("end-date-in-future"));
    }

    @Test
    public void startFinancingRoundBudgetTooLow() throws Exception {

        // attempt to create round with 0-budget
        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(financingRound(new DateTime().plusDays(1), 0))))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundService, times(0)).startNewFinancingRound(any(FinancingRound.class));
        final String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, containsString("at-least-one-dollar"));
    }

    @Test
    public void startFinancingRoundCollidingRounds() throws Exception {

        // create currently running financing round
        when(financingRoundRepository.findAll()).thenReturn(Collections.singletonList(financingRoundEntity(new DateTime().minusDays(5), new DateTime().plusDays(1))));

        // attempt to create a new (otherwise valid) one
        final MvcResult mvcResult = mockMvc.perform(post("/financinground")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(financingRound(new DateTime().plusDays(1), 99))))
                .andExpect(status().isBadRequest())
                .andReturn();

        verify(financingRoundService, times(0)).startNewFinancingRound(any(FinancingRound.class));

        final String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, containsString("non-colliding"));
    }

    @Test
    public void stopFinancingRound() throws Exception {
        final String roundId = "4711";

        FinancingRound expectedFinancingRound = anExpectedFinancingRound();
        when(financingRoundService.stopFinancingRound(roundId)).thenReturn(expectedFinancingRound);

        // stop round
        final MvcResult mvcResult = mockMvc.perform(put("/financinground/4711/cancel"))
                .andExpect(status().isOk())
                .andReturn();

        final FinancingRound actRes = mapper.readValue(mvcResult.getResponse().getContentAsString(), FinancingRound.class);
        assertFinancingRoundsEqual(expectedFinancingRound, actRes);
    }

    @Test
    public void stopFinancingRoundMissingRound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(financingRoundService).stopFinancingRound("4711");

        // stop round
        mockMvc.perform(put("/financinground/4711/cancel"))
                .andExpect(status().isNotFound());

        verify(financingRoundRepository, times(0)).save(any(FinancingRoundEntity.class));
    }

    @Test
    public void stopFinancingRoundAlreadyStoppedRound() throws Exception {
        when(financingRoundService.stopFinancingRound("4711")).thenAnswer(i -> {
            throw InvalidRequestException.financingRoundAlreadyStopped();
        });

        // stop round
        final MvcResult mvcResult = mockMvc.perform(put("/financinground/4711/cancel"))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(), is("{\"errorCode\":\"financing_round_already_stopped\",\"fieldViolations\":{}}"));
    }

    private FinancingRound financingRound(DateTime end, Integer budget) {
        FinancingRound financingRound = new FinancingRound();
        financingRound.setEndDate(end);
        financingRound.setBudget(budget);
        return financingRound;
    }

    private FinancingRound anExpectedFinancingRound() {
        final FinancingRound res = financingRound(new DateTime().plusDays(1), 99);
        res.setActive(true);
        res.setId("test_Id");
        res.setStartDate(new DateTime());
        return res;
    }

    private FinancingRoundEntity financingRoundEntity(DateTime start, DateTime end) {
        FinancingRoundEntity reference = new FinancingRoundEntity();
        reference.setStartDate(start);
        reference.setEndDate(end);
        return reference;
    }

    private void assertFinancingRoundsEqual(FinancingRound expectedFinancingRound, FinancingRound actRes) {
        assertThat(actRes.getStartDate().getMillis(), is(expectedFinancingRound.getStartDate().getMillis()));
        assertThat(actRes.getId(), is(expectedFinancingRound.getId()));
        assertThat(actRes.getEndDate().getMillis(), is(expectedFinancingRound.getEndDate().getMillis()));
        assertThat(actRes.getBudget(), is(expectedFinancingRound.getBudget()));
        assertThat(actRes.isActive(), is(expectedFinancingRound.isActive()));
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
        public FinancingRoundRepository financingRoundRepository() {
            return mock(FinancingRoundRepository.class);
        }

        @Bean
        public FinancingRoundService financingRoundService() {
            return mock(FinancingRoundService.class);
        }
    }
}