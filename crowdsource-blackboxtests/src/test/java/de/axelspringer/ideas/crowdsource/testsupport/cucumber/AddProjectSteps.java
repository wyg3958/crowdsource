package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.AddProjectConfirmationView;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.AddProjectForm;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectsPage;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddProjectSteps {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private NavigationBar navigationBar;

    @Autowired
    private AddProjectForm addProjectForm;

    @Autowired
    private AddProjectConfirmationView addProjectConfirmationView;

    @Autowired
    private ProjectsPage projectsPage;


    private WebDriver webDriver;

    private String randomProjectTitlePrefix;
    private String randomProjectShortDescriptionPrefix;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }


    @When("^he clicks on the New Project link in the navigation bar$")
    public void he_clicks_on_the_New_Project_link_in_the_navigation_bar() throws Throwable {
        PageFactory.initElements(webDriver, navigationBar);
        navigationBar.clickNewProject();
    }

    @Then("^he is redirected to the project creation page$")
    public void he_is_redirected_to_the_project_creation_page() throws Throwable {
        PageFactory.initElements(webDriver, addProjectForm);
        addProjectForm.waitForPageLoad();
    }

    @When("^he submits the form with valid project data$")
    public void he_submits_the_form_with_valid_project_data() throws Throwable {
        PageFactory.initElements(webDriver, addProjectForm);

        randomProjectTitlePrefix = "Title " + RandomStringUtils.randomAlphanumeric(16);
        randomProjectShortDescriptionPrefix = "Short description " + RandomStringUtils.randomAlphanumeric(16);

        // makes exactly 60 characters, to cause the text to be abbreviated on purpose
        addProjectForm.setTitle(randomProjectTitlePrefix + " Mmmmm mmmmm mmmmm mmm mmmm, mmmmmmmmm");

        // makes exactly 140 characters, to cause the text to be abbreviated on purpose
        addProjectForm.setShortDescription(randomProjectShortDescriptionPrefix +
                " Mmmmm mmmmm mmmmm mmm mmmm, mmmmmmmmmm mmmmmmmmmm mmmmm, mmm mmmm mmmmmm mmmmmm mmmmmm mmmmmmmm mm mmmmmm");

        addProjectForm.setPledgeGoal("25000");
        addProjectForm.setDescription("Loooong description\nwith newlines");
        addProjectForm.submit();
    }

    @Then("^the project creation success page is shown$")
    public void the_project_creation_success_page_is_shown() throws Throwable {
        PageFactory.initElements(webDriver, addProjectConfirmationView);
        addProjectConfirmationView.waitForPageLoad();
    }

    @When("^he clicks the project overview link$")
    public void he_clicks_the_project_overview_link() throws Throwable {
        PageFactory.initElements(webDriver, addProjectConfirmationView);
        addProjectConfirmationView.clickLinkToProject();
    }

    @Then("^the project overview page shows the new project$")
    public void the_project_overview_page_shows_the_new_project() throws Throwable {
        PageFactory.initElements(webDriver, projectsPage);
        projectsPage.waitForPageLoad();

        List<Project> projects = projectsPage.getProjects();
        assertThat(projects, hasItem(Matchers.<Project>hasProperty("title", startsWith(randomProjectTitlePrefix))));
        assertThat(projects, hasItem(Matchers.<Project>hasProperty("title", endsWith("\u2026")))); // u2026 is the ellipsis unicode character '...'
        assertThat(projects, hasItem(Matchers.<Project>hasProperty("shortDescription", startsWith(randomProjectShortDescriptionPrefix))));
        assertThat(projects, hasItem(Matchers.<Project>hasProperty("shortDescription", endsWith("\u2026"))));
    }

    @And("^the tooltip for currency conversion is not visible$")
    public void the_tooltip_for_currency_conversion_is_not_visible() throws Throwable {
        assertFalse(addProjectForm.currencyConversionTooltipVisible());
    }

    @When("^he hovers the currency element$")
    public void he_hovers_the_currency_element() throws Throwable {
        addProjectForm.hoverCurrency();
    }

    @Then("^the tooltip for currency conversion is visible$")
    public void the_tooltip_for_currency_conversion_is_visible() throws Throwable {
        assertTrue(addProjectForm.currencyConversionTooltipVisible());
    }
}