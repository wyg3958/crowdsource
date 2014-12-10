package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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

    @Given("^a user is logged in")
    public void a_user_is_logged_in() throws Throwable {
        the_index_page_is_visited();
        he_is_redirected_to_the_login_page();
        he_enters_valid_credentials();
        he_is_redirected_to_the_index_page();
    }

    @Then("^he is redirected to the login page$")
    public void he_is_redirected_to_the_login_page() throws Throwable {

        PageFactory.initElements(webDriver, loginForm);
        loginForm.waitForPageLoad();
    }

    @When("^he enters valid credentials$")
    public void he_enters_valid_credentials() throws Throwable {

        PageFactory.initElements(webDriver, loginForm);
        final String email = MongoUserDetailsService.DEFAULT_EMAIL.substring(0, MongoUserDetailsService.DEFAULT_EMAIL.indexOf("@"));
        loginForm.login(email, MongoUserDetailsService.DEFAULT_PASS);
    }

    @When("^he enters invalid credentials$")
    public void he_enters_invalid_credentials() throws Throwable {

        PageFactory.initElements(webDriver, loginForm);
        loginForm.login("foooooooaaaahhhh", MongoUserDetailsService.DEFAULT_PASS);
    }

    @Then("^he is redirected to the index page$")
    public void he_is_redirected_to_the_index_page() throws Throwable {

        PageFactory.initElements(webDriver, indexPage);
        indexPage.waitForPageLoad();
    }

    @And("^the text \"([^\"]*)\" is displayed$")
    public void the_text_is_displayed(String text) throws Throwable {

        PageFactory.initElements(webDriver, indexPage);
        assertEquals(text, indexPage.getHeadlineText());
    }

    @When("^he reloads the page$")
    public void he_reloads_the_page() throws Throwable {

        webDriver.get(webDriver.getCurrentUrl());
    }

    @Given("^the index page is visited$")
    public void the_index_page_is_visited() throws Throwable {

        webDriver.get(urlProvider.applicationUrl());
    }

    @Then("^the error \"([^\"]*)\" is displayed$")
    public void the_error_is_displayed(String errorText) throws Throwable {
        assertThat(loginForm.getErrorText(), is(errorText));
    }
}