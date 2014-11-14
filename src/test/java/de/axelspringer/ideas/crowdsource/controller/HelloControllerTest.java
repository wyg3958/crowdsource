package de.axelspringer.ideas.crowdsource.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HelloControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();

    @Test
    public void testHello() throws Exception {
        mockMvc.perform(
                get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"message\":\"hi\"}"));
    }
}