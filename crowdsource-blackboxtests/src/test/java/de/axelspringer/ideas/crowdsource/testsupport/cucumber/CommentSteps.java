package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectDetailPage;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.CrowdSourceClient;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class CommentSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private ProjectDetailPage projectDetailPage;

    @Autowired
    private CrowdSourceClient crowdSourceClient;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private ProjectDetailSteps projectDetailSteps;

    @And("^a comment for the project was submitted$")
    public void a_comment_for_the_project_was_submitted() throws Throwable {

        crowdSourceClient.comment(projectDetailSteps.getCreatedProject(), "some valid comment is a good thing for a test");
    }

    @And("^a comment is visible$")
    public void a_comment_is_visible() throws Throwable {

        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^the user submits a comment$")
    public void the_user_submits_a_comment() throws Throwable {

        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^The comment is visible as the last in the comments-list$")
    public void The_comment_is_visible_as_the_last_in_the_comments_list() throws Throwable {

        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }
}
