package de.axelspringer.ideas.crowdsource.testsupport.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.axelspringer.ideas.crowdsource.config.security.MongoUserDetailsService;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class CrowdSourceClient {

    private static final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Autowired
    private UrlProvider urlProvider;

    public AuthToken authorizeWithDefaultUser() {
        return authorize(MongoUserDetailsService.DEFAULT_EMAIL, MongoUserDetailsService.DEFAULT_PASS);
    }

    public AuthToken authorize(String email, String password) {
        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.put("username", Arrays.asList(email));
        tokenRequest.put("password", Arrays.asList(password));
        tokenRequest.put("client_id", Arrays.asList("web"));
        tokenRequest.put("grant_type", Arrays.asList("password"));

        return restTemplate.postForObject(urlProvider.applicationUrl() + "/oauth/token", tokenRequest, AuthToken.class);
    }

    public void createProject(Project project, AuthToken authToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Authorization", Arrays.asList("Bearer " + authToken.accessToken));

        HttpEntity<Project> requestEntity = new HttpEntity<>(project, headers);
        restTemplate.exchange(urlProvider.applicationUrl() + "/project", HttpMethod.POST, requestEntity, Void.class);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthToken {
        @JsonProperty("access_token")
        private String accessToken;
    }
}
