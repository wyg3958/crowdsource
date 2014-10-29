package de.axelspringer.ideas.crowdsource.testsupport.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class IndexSteps {

    @When("^a User visits the index page$")
    public void a_User_visits_the_index_page() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new cucumber.api.PendingException();
    }

    @Then("^the message \"([^\"]*)\" is shown$")
    public void the_message_is_shown(String message) throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }
}
