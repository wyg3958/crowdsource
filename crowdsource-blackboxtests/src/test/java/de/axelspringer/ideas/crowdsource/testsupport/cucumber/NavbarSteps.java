package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class NavbarSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private NavigationBar navigationBar;

    @Before("@WithMobile")
    public void prepareViewport() {
        webDriverProvider.provideDriver().manage().window().setSize(new Dimension(WebDriverProvider.MOBILE_WIDTH, 800));
    }

    @After("@WithMobile")
    public void restoreViewport() {
        webDriverProvider.provideDriver().manage().window().setSize(new Dimension(WebDriverProvider.DESKTOP_WIDTH, 800));
    }

    @Then("^the navbar toggle icon is visible$")
    public void the_navbar_toggle_icon_is_visible() {
        PageFactory.initElements(webDriverProvider.provideDriver(), navigationBar);
        assertThat(navigationBar.getToggleIcon().isDisplayed(), is(true));
    }

    @When("^the navbar toggle icon is clicked$")
    public void the_navbar_toggle_icon_is_clicked() throws Throwable {
        navigationBar.getToggleIcon().click();
    }

    @Then("^the menu is expanded$")
    public void the_menu_is_expanded() throws Throwable {
        assertThat(navigationBar.isExpanded(), is(true));
    }
}
