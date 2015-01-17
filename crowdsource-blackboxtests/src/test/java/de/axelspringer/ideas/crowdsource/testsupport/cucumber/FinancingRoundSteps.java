package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.FinancingRoundsPage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class FinancingRoundSteps {

    @Autowired
    private FinancingRoundsPage financingRoundsPage;

    /**
     * will be set by create step and read right after for the assertion
     */
    private int budget;

    /**
     * will be set by create step and read right after for the assertion
     */
    private DateTime endDate;

    @And("^he visits the financingrounds-page$")
    public void he_visits_the_financingrounds_page() throws Throwable {
        financingRoundsPage.open();
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
        financingRoundsPage.getFinancingRounds().forEach(financingRound -> {
            if (financingRound.isActive()) {
                financingRoundsPage.cancelFinancingRound(financingRound);
            }
        });
    }

    @And("^he starts a new financing round$")
    public void he_starts_a_new_financing_round() throws Throwable {

        // set budget some value between 10 and 1000
        budget = new Random().nextInt(990) + 10;

        // set end date some value between tomorrow and next year
        endDate = new DateTime().plusDays(new Random().nextInt(364) + 1);

        // start round
        financingRoundsPage.startFinancingRound(endDate, budget);
    }

    @Then("^he sees the new financing round in the list of financing rounds$")
    public void he_sees_the_new_financing_round_in_the_list_of_financing_rounds() throws Throwable {

        assertNotNull(financingRoundsPage.findFinancingRound(endDate, budget));
    }

    @And("^the new financing round is marked active$")
    public void the_new_financing_round_is_marked_active() throws Throwable {

        FinancingRound financingRound = financingRoundsPage.findFinancingRound(endDate, budget);
        assertTrue(financingRound.isActive());
    }

    @And("^he stops the financing round$")
    public void he_stops_the_financing_round() throws Throwable {

        final FinancingRound financingRound = financingRoundsPage.findFinancingRound(endDate, budget);
        assertNotNull(financingRound);
        financingRoundsPage.cancelFinancingRound(financingRound);
    }

    @Then("^the financing round is not marked active any more$")
    public void the_financing_round_is_not_marked_active_any_more() throws Throwable {

        final FinancingRound financingRound = financingRoundsPage.findFinancingRound(new DateTime(), budget);
        assertNotNull(financingRound);
        assertFalse(financingRound.isActive());
    }

    @And("^the option to start a new financing round is not available$")
    public void the_option_to_start_a_new_financing_round_is_not_available() throws Throwable {

        assertFalse(financingRoundsPage.canStartFinancingRound());
    }

    @And("^the option to start a new financing round is available$")
    public void the_option_to_start_a_new_financing_round_is_available() throws Throwable {

        assertTrue(financingRoundsPage.canStartFinancingRound());
    }
}
