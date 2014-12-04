package de.axelspringer.ideas.crowdsource.testsupport.cucumber.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.config.security.MongoUserDetailsService;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.IndexPage;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.LoginForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class LoginSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private IndexPage indexPage;

    @Autowired
    private LoginForm loginForm;

    private WebDriver webDriver;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }

    @Given("^an anonymous user visits the index page$")
    public void an_anonymous_user_visits_the_index_page() throws Throwable {
        throw new PendingException();
    }

    @Then("^he is redirected to the login page$")
    public void he_is_redirected_to_the_login_page() throws Throwable {
        throw new PendingException();
    }

    @When("^he enters valid credentials$")
    public void he_enters_valid_credentials() throws Throwable {
        throw new PendingException();
    }

    @Then("^he is redirected to the index page$")
    public void he_is_redirected_to_the_index_page() throws Throwable {
        throw new PendingException();
    }

    @And("^the text \"([^\"]*)\" is displayed$")
    public void the_text_is_displayed(String arg1) throws Throwable {
        throw new PendingException();
    }

    @When("^he closes his browser$")
    public void he_closes_his_browser() throws Throwable {
        throw new PendingException();
    }

    @And("^he visits the index page$")
    public void he_visits_the_index_page() throws Throwable {
        throw new PendingException();
    }
}
