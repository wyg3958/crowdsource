package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.FinancingRoundsPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class FinancingRoundSteps {

    @Autowired
    private FinancingRoundsPage financingRoundsPage;

    @And("^he visits the financingrounds-page$")
    public void he_visits_the_financingrounds_page() throws Throwable {
        financingRoundsPage.visitPage();
    }

    @Then("^he gets displayed an error message$")
    public void he_gets_displayed_an_error_message() throws Throwable {
        financingRoundsPage.confirmErrorAlert();
    }

    @Then("^he sees a list of financing rounds$")
    public void he_sees_a_list_of_financing_rounds() throws Throwable {
        financingRoundsPage.waitForPageLoad();
    }

    @And("^no financing round is currently active$")
    public void no_financing_round_is_currently_active() throws Throwable {
        financingRoundsPage.waitForPageLoad();
        financingRoundsPage.getFinancingRounds().stream().forEach(financingRound -> {
            if (financingRound.isActive()) {
                // TODO: cancel

            }
        });
    }

    @And("^he starts a new financing round$")
    public void he_starts_a_new_financing_round() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^he sees the new financing round in the list of financing rounds$")
    public void he_sees_the_new_financing_round_in_the_list_of_financing_rounds() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the new financing round is marked active$")
    public void the_new_financing_round_is_marked_active() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^he stops the financing round$")
    public void he_stops_the_financing_round() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the financing round is not marked active any more$")
    public void the_financing_round_is_not_marked_active_any_more() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the option to start a new financing round is not available$")
    public void the_option_to_start_a_new_financing_round_is_not_available() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }
}
