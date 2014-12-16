package de.axelspringer.ideas.crowdsource.resource;

import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ProjectIT {

    @Autowired
    private UrlProvider urlProvider;

    @Test
    public void testAccessDenied() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        Project project = new Project();
        project.setTitle("title");
        project.setShortDescription("short description");
        project.setPledgeGoal(1);
        project.setDescription("description");

        try {
            restTemplate.postForObject(urlProvider.applicationUrl() + "/project", project, Void.class);
            Assert.fail("Accessing a protected resource should fail");
        }
        catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        }
    }

    @Test
    public void testNotFound() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        try {
            restTemplate.getForObject(urlProvider.applicationUrl() + "/project/{projectId}", Project.class, "non-existant-project-id");
            Assert.fail("Accessing a protected resource should fail");
        }
        catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND));
        }
    }

}
