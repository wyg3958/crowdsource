package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.Teaser;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class TeaserSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private Teaser teaser;

    private int remainingBudget;
    private String remainingTimeString;

    @Then("^the teaser is displayed$")
    public void the_teaser_is_displayed() throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), teaser);
        assertThat(teaser.isVisible(), is(true));
    }

    @Then("^the teaser is hidden$")
    public void the_teaser_is_hidden() throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), teaser);
        assertThat(teaser.isVisible(), is(false));
    }

    @Then("^the teaser shows the metrics of the active financing round$")
    public void the_teaser_shows_the_metrics_of_the_active_financing_round() throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), teaser);
        assertThat(teaser.isVisible(), is(true));
        teaser.waitForContentLoaded();

        remainingTimeString = teaser.getRemainingTimeString();
        remainingBudget = teaser.getRemainingBudget();

        assertThat(teaser.isRemainingBudgetItemVisible(), is(true));
        assertThat(remainingBudget, is(greaterThan(0)));
        assertThat(remainingTimeString, is(not("Keine aktive Runde")));
        assertThat(teaser.getUserCount(), is(greaterThan(0)));
    }

    @Then("^the teaser only shows the number of active users$")
    public void the_teaser_only_shows_the_number_of_active_users() throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), teaser);
        assertThat(teaser.isVisible(), is(true));
        teaser.waitForContentLoaded();

        assertThat(teaser.isRemainingBudgetItemVisible(), is(false));
        assertThat(teaser.getRemainingTimeString(), is("Keine aktive Runde"));
        assertThat(teaser.getUserCount(), is(greaterThan(0)));
    }

    @Then("^the remaining budget is (\\d+) less than before$")
    public void the_remaining_budget_is_less_than_before(int amount) throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), teaser);
        assertThat(teaser.isVisible(), is(true));
        teaser.waitForContentLoaded();

        assertThat(teaser.getRemainingBudget(), is(remainingBudget - amount));
    }

    @When("^one second elapses$")
    public void one_second_passes_by() throws Throwable {
        Thread.sleep(1500);
    }

    @Then("^the remaining time is less than before$")
    public void the_remaining_time_is_less_than_before() throws Throwable {
        assertThat(teaser.getRemainingTimeString(), is(not("Keine aktive Runde")));
        assertThat(teaser.getRemainingTimeString(), is(not(remainingTimeString)));
    }
}
