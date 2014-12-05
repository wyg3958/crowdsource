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
        // logout
        webDriver.get(urlProvider.applicationUrl() + "/#/logout");
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
}
