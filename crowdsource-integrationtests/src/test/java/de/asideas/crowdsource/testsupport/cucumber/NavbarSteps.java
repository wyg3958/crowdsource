package de.asideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.asideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.asideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
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

    @Then("^the navbar toggle icon is visible$")
    public void the_navbar_toggle_icon_is_visible() {
        PageFactory.initElements(webDriverProvider.provideMobileDriver(), navigationBar);
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
