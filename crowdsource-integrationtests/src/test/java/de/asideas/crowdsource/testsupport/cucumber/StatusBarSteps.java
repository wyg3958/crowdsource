package de.asideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.And;
import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.pageobjects.StatusBar;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class StatusBarSteps {

    @Autowired
    private StatusBar statusBar;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @And("^the budget in the status bar is (displayed|hidden)$")
    public void the_budget_in_the_status_bar_is_displayed(String displayed) throws Throwable {
        boolean expectedBudgetVisibility = "displayed".equals(displayed);

        PageFactory.initElements(webDriverProvider.provideDriver(), statusBar);
        assertThat(statusBar.isBudgetDisplayed(), is(expectedBudgetVisibility));
    }

    @And("^the displayed budget is (\\d+)$")
    public void the_displayed_budget_is(int expectedBudget) throws Throwable {
        PageFactory.initElements(webDriverProvider.provideDriver(), statusBar);
        assertThat(statusBar.getBudget(), is(expectedBudget));
    }
}
