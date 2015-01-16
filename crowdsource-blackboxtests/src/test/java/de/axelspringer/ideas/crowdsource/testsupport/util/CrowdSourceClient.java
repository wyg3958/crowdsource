package de.axelspringer.ideas.crowdsource.testsupport.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.axelspringer.ideas.crowdsource.config.security.MongoUserDetailsService;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class CrowdSourceClient {

    private static final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    @Autowired
    private UrlProvider urlProvider;

    public AuthToken authorizeWithDefaultUser() {
        return authorize(MongoUserDetailsService.DEFAULT_USER_EMAIL, MongoUserDetailsService.DEFAULT_USER_PASS);
    }

    public AuthToken authorizeWithAdminUser() {
        return authorize(MongoUserDetailsService.DEFAULT_ADMIN_EMAIL, MongoUserDetailsService.DEFAULT_ADMIN_PASS);
    }

    public AuthToken authorize(String email, String password) {
        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.put("username", Arrays.asList(email));
        tokenRequest.put("password", Arrays.asList(password));
        tokenRequest.put("client_id", Arrays.asList("web"));
        tokenRequest.put("grant_type", Arrays.asList("password"));

        return restTemplate.postForObject(urlProvider.applicationUrl() + "/oauth/token", tokenRequest, AuthToken.class);
    }

    public ResponseEntity<Project> createProject(Project project, AuthToken authToken) {
        HttpEntity<Project> requestEntity = createRequestEntity(project, authToken);
        return restTemplate.exchange(urlProvider.applicationUrl() + "/project", HttpMethod.POST, requestEntity, Project.class);
    }

    public ResponseEntity<FinancingRound> startFinancingRound(FinancingRound financingRound, AuthToken authToken) {
        HttpEntity<FinancingRound> requestEntity = createRequestEntity(financingRound, authToken);
        return restTemplate.exchange(urlProvider.applicationUrl() + "/financinground", HttpMethod.POST, requestEntity, FinancingRound.class);
    }

    public FinancingRound getActiveFinanceRound() {
        try {
            return restTemplate.getForObject(urlProvider.applicationUrl() + "/financinground/active", FinancingRound.class);
        }
        catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public ResponseEntity<FinancingRound> stopFinancingRound(String id, AuthToken authToken) {
        HttpEntity<FinancingRound> requestEntity = createRequestEntity(null, authToken);
        return restTemplate.exchange(urlProvider.applicationUrl() + "/financinground/{id}/cancel", HttpMethod.PUT, requestEntity, FinancingRound.class, id);
    }

    private <T> HttpEntity<T> createRequestEntity(T body, AuthToken authToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Authorization", Arrays.asList("Bearer " + authToken.accessToken));

        return new HttpEntity<>(body, headers);
    }

    public RestTemplate getUnderlyingClient() {
        return restTemplate;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthToken {
        @JsonProperty("access_token")
        private String accessToken;
    }
}
