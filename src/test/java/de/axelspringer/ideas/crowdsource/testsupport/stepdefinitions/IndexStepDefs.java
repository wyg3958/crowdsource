package de.axelspringer.ideas.crowdsource.testsupport.stepdefinitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.CucumberIT;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.IndexPage;
import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.assertEquals;

public class IndexStepDefs {

    @When("^a User visits the index page$")
    public void a_User_visits_the_index_page() throws Throwable {

        CucumberIT.DRIVER.get(CucumberIT.APP_URL);
    }

    @Then("^the message \"([^\"]*)\" is shown$")
    public void the_message_is_shown(String message) throws Throwable {

        final IndexPage indexPage = PageFactory.initElements(CucumberIT.DRIVER, IndexPage.class);
        assertEquals(message, indexPage.helloText());
    }
}
