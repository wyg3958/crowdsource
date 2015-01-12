package de.axelspringer.ideas.crowdsource.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetCurrentUserControllerTest extends AbstractUserControllerTest {

    @Test
    public void getCurrentUser_shouldReturnUserSuccessfully() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/user/current")
                .principal(new UsernamePasswordAuthenticationToken(ACTIVATED_USER_MAIL_ADDRESS, "somepassword"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString(),
                is("{\"email\":\"existing.and.activated@axelspringer.de\",\"roles\":[\"ROLE_USER\"],\"budget\":0,\"name\":\"Existing And Activated\"}"));
    }

    @Test
    public void getCurrentUser_shouldRespondWith401IfUserWasNotFound() throws Exception {

        mockMvc.perform(get("/user/current")
                .principal(new UsernamePasswordAuthenticationToken("unknown@user.com", "somepassword"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
