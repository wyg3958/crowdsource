package de.axelspringer.ideas.crowdsource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.axelspringer.ideas.crowdsource.model.Hello;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class AuthenticationIT {

    @Autowired
    private UrlProvider urlProvider;

    @Test
    public void unauthorizedRequest() {
        RestTemplate restTemplate = createRestTemplate();

        try {
            restTemplate.getForObject(urlProvider.applicationUrl() + "/hello", Hello.class);
            Assert.fail("Accessing a protected resource without access token should fail");
        }
        catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(UNAUTHORIZED));
        }
    }

    @Test
    public void authorizedRequest() throws IOException {
        TokenResponse tokenResponse = requestToken("crowdsource@axelspringer.de", "test");
        assertThat(tokenResponse.getAccessToken(), is(notNullValue()));

        ResponseEntity<Hello> response = getHello(tokenResponse.getAccessToken());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void invalidCredentials() throws IOException {
        try {
            requestToken("wrong", "credentials");
            Assert.fail("Requesting an access token with wrong credentials should fail");
        }
        catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(BAD_REQUEST));
        }
    }

    @Test
    public void invalidAccessToken() throws IOException {
        try {
            getHello("some-invalid-access-token");
            Assert.fail("Accessing a protected resource with an invalid token should fail");
        }
        catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(UNAUTHORIZED));
        }
    }

    private TokenResponse requestToken(String username, String password) {
        RestTemplate restTemplate = createRestTemplate();

        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.put("username", Arrays.asList(username));
        tokenRequest.put("password", Arrays.asList(password));
        tokenRequest.put("client_id", Arrays.asList("web"));
        tokenRequest.put("grant_type", Arrays.asList("password"));

        return restTemplate.postForObject(urlProvider.applicationUrl() + "/oauth/token", tokenRequest, TokenResponse.class);
    }

    private ResponseEntity<Hello> getHello(String accessToken) {
        MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
        requestHeaders.put("Authorization", Arrays.asList("Bearer " + accessToken));

        HttpEntity<?> request = new HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = createRestTemplate();
        return restTemplate.exchange(urlProvider.applicationUrl() + "/hello", HttpMethod.GET, request, Hello.class);
    }

    private RestTemplate createRestTemplate() {
        // use apache http client to be able to see the requests and responses in the log (if configured, see logback.xml)
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        restTemplate.setMessageConverters(Arrays.asList(
                new FormHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()));

        return restTemplate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }
}
