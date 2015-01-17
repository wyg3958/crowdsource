package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.StatusBar;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class StatusBarSteps {

    @Autowired
    private StatusBar statusBar;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Then("^the status bar is not visible$")
    public void the_status_bar_is_not_visible() {
        PageFactory.initElements(webDriverProvider.provideDriver(), statusBar);
        assertThat(statusBar.isVisible(), is(false));
    }

    @And("^the status bar is visible$")
    public void the_status_bar_is_visible() throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), statusBar);
        assertThat(statusBar.isVisible(), is(true));
    }

    @And("^the budget in the status bar is displayed$")
    public void the_budget_in_the_status_bar_is_displayed() throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), statusBar);
        assertThat(statusBar.getBudget(), is(greaterThan(0)));
    }
}
