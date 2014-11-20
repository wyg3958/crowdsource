package de.axelspringer.ideas.crowdsource.testsupport.cucumber.stepdefinitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.IndexPage;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverProvider;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class IndexStepDefs {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @When("^a User visits the index page$")
    public void a_User_visits_the_index_page() throws Throwable {

        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        webDriver.get(urlProvider.applicationUrl());
    }

    @Then("^the message \"([^\"]*)\" is shown$")
    public void the_message_is_shown(String message) throws Throwable {

        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        final IndexPage indexPage = PageFactory.initElements(webDriver, IndexPage.class);
        assertEquals(message, indexPage.helloText());
    }
}
