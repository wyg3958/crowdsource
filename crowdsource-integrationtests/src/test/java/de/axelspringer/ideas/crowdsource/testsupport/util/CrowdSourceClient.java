package de.axelspringer.ideas.crowdsource.testsupport.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.axelspringer.ideas.crowdsource.config.security.MongoUserDetailsService;
import de.axelspringer.ideas.crowdsource.enums.ProjectStatus;
import de.axelspringer.ideas.crowdsource.model.presentation.Comment;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.model.presentation.Pledge;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserActivation;
import de.axelspringer.ideas.crowdsource.model.presentation.user.UserRegistration;
import de.axelspringer.ideas.crowdsource.util.validation.email.EligibleEmailValidator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import java.util.List;

import static org.junit.Assert.assertEquals;

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

    public void registerUser(String emailName) {
        // create a user via the REST API
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setEmail(emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN);
        userRegistration.setTermsOfServiceAccepted(true);

        restTemplate.postForObject(urlProvider.applicationUrl() + "/user", userRegistration, Void.class);
    }

    public void activateUser(String emailName, UserActivation userActivation) {
        restTemplate.postForObject(urlProvider.applicationUrl() + "/user/{email}/activation", userActivation, Void.class, emailName + EligibleEmailValidator.ELIGIBLE_EMAIL_DOMAIN);
    }

    public void recoverPassword(String userEmail) {
        restTemplate.getForObject(urlProvider.applicationUrl() + "/user/{email}/password-recovery", Void.class, userEmail);
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
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public ResponseEntity<FinancingRound> stopFinancingRound(String id, AuthToken authToken) {
        HttpEntity requestEntity = createRequestEntity(authToken);
        return restTemplate.exchange(urlProvider.applicationUrl() + "/financinground/{id}/cancel", HttpMethod.PUT, requestEntity, FinancingRound.class, id);
    }

    public ResponseEntity<Void> pledgeProject(Project project, Pledge pledge, AuthToken authToken) {
        HttpEntity<Pledge> requestEntity = createRequestEntity(pledge, authToken);
        return restTemplate.exchange(urlProvider.applicationUrl() + "/project/{id}/pledge", HttpMethod.POST, requestEntity, Void.class, project.getId());
    }

    public void comment(Project project, String comment, AuthToken token) {
        final String commentUrl = urlProvider.applicationUrl() + "/project/{id}/comment";
        restTemplate.exchange(commentUrl, HttpMethod.POST, createRequestEntity(new Comment(null, null, comment), token), Void.class, project.getId());
    }

    private <T> HttpEntity<T> createRequestEntity(T body, AuthToken authToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.put("Authorization", Arrays.asList("Bearer " + authToken.accessToken));

        return new HttpEntity<>(body, headers);
    }

    private HttpEntity createRequestEntity(AuthToken authToken) {
        return createRequestEntity(null, authToken);
    }

    public RestTemplate getUnderlyingClient() {
        return restTemplate;
    }

    public void publish(Project project, AuthToken token) {

        project.setStatus(ProjectStatus.PUBLISHED);
        update(project, token);
    }

    public void reject(Project project, AuthToken token) {

        project.setStatus(ProjectStatus.REJECTED);
        update(project, token);
    }

    private void update(Project project, AuthToken token) {

        final ResponseEntity<Project> exchange = restTemplate.exchange(urlProvider.applicationUrl() + "/project/" + project.getId(), HttpMethod.PATCH, createRequestEntity(project, token), Project.class);
        assertEquals(HttpStatus.OK, exchange.getStatusCode());
    }

    public List<Project> listProjects(AuthToken authToken) {

        ResponseEntity<Project[]> responseEntity = restTemplate.exchange(urlProvider.applicationUrl() + "/projects", HttpMethod.GET, createRequestEntity(authToken), Project[].class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        return Arrays.asList(responseEntity.getBody());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthToken {
        @JsonProperty("access_token")
        private String accessToken;

        public AuthToken() {
        }

        public String getAccessToken() {
            return this.accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}

