package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.StaticPage;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class StaticPagesSteps {

    @Autowired
    private StaticPage staticPage;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @And("^he visits the \"([^\"]*)\" page$")
    public void he_visits_the_page(final String text) throws Throwable {
        staticPage.clickLinkByLinkText(text);
    }

    @Then("^he sees the text \"([^\"]*)\"$")
    public void then_he_sees_the_text(final String text) throws Throwable {
        staticPage.waitForTextToBeAvailable(text);
    }

    @And("^hee sees the text \"([^\"]*)\"$")
    public void hee_sees_the_text(final String text) throws Throwable {
        staticPage.waitForTextToBeAvailable(text);
    }

    @And("^the content text \"([^\"]*)\" is not visible$")
    public void the_content_text_is_not_visible(final String text) throws Throwable {
        staticPage.ensureTextInParagraphIsNotAvailable(text);
    }

    @When("^he clicks on text \"([^\"]*)\"$")
    public void he_clicks_on_text(final String text) throws Throwable {
        staticPage.clickLinkByLinkText(text);
    }

    @Then("^the content text \"([^\"]*)\" is visible$")
    public void the_content_text_is_visible(final String text) throws Throwable {
        staticPage.waitForTextToBeAvailable(text);
    }

    @When("^the current page is reloaded$")
    public void the_current_page_is_reloaded() {
        webDriverProvider.provideDriver().navigate().refresh();
    }
}
