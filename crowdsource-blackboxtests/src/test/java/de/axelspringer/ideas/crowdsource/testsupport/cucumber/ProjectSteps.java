package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.ProjectDetailPage;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.ProjectsPage;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.AddProjectConfirmationView;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.AddProjectForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.CrowdSourceClient;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ProjectSteps {

    @Autowired
    private NavigationBar navigationBar;

    @Autowired
    private AddProjectForm addProjectForm;

    @Autowired
    private AddProjectConfirmationView addProjectConfirmationView;

    @Autowired
    private ProjectsPage projectsPage;

    @Autowired
    private ProjectDetailPage projectDetailPage;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private CrowdSourceClient crowdSourceClient;

    @Autowired
    private UrlProvider urlProvider;

    private WebDriver webDriver;
    private String randomProjectTitlePrefix;
    private String randomProjectShortDescriptionPrefix;
    private Project createdProject;
    private int savedPageYOffset;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }

    @Given("^a published project is available$")
    public void a_published_project_is_available() throws Throwable {
        createdProject = new Project();
        createdProject.setTitle("T" + RandomStringUtils.randomAlphanumeric(6));
        createdProject.setShortDescription("Short description " + RandomStringUtils.randomAlphanumeric(16));
        createdProject.setPledgeGoal(25);
        createdProject.setDescription("This is the project description text." + RandomStringUtils.random(1000, "\nabc"));

        CrowdSourceClient.AuthToken authToken = crowdSourceClient.authorizeWithDefaultUser();
        crowdSourceClient.createProject(createdProject, authToken);
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

    @When("^the user clicks on the tile of this published project$")
    public void the_user_clicks_on_the_tile_of_this_published_project() throws Throwable {
        PageFactory.initElements(webDriver, projectsPage);
        projectsPage.waitForPageLoad();
        projectsPage.clickProjectTileWithTitle(createdProject.getTitle());
    }

    @Then("^the project detail page of this project is displayed$")
    public void the_project_detail_page_of_this_project_is_displayed() throws Throwable {
        PageFactory.initElements(webDriver, projectDetailPage);
        projectDetailPage.waitForTitleToBeAvailable(createdProject.getTitle());
        projectDetailPage.waitForShortDescriptionToBeAvailable(createdProject.getShortDescription());
        projectDetailPage.waitForDescriptionToBeAvailable(createdProject.getDescription());
        projectDetailPage.waitForStatusWidgetToBeAvailable();

        assertThat(projectDetailPage.getStatusWidgetProgressBarValue(), is("0px"));
        assertThat(projectDetailPage.getStatusWidgetPledgedAmount(), is("$0"));
        assertThat(projectDetailPage.getStatusWidgetPledgeGoal(), is("$25"));
        assertThat(projectDetailPage.getStatusWidgetBackers(), is("0"));
        assertThat(projectDetailPage.getStatusWidgetUserName(), is("Crowdsource"));
    }

    @Given("^the user requests the project detail page with a non existant project id$")
    public void the_user_requests_the_project_detail_page_with_a_non_existant_project_id() throws Throwable {
        projectDetailPage.open("i-dont-exist-project-id");
    }

    @Given("^the user is on a project detail page$")
    public void the_user_is_on_a_project_detail_page() throws Throwable {
        a_published_project_is_available();

        webDriver.get(urlProvider.applicationUrl());
        projectsPage.waitForPageLoad();

        the_user_clicks_on_the_tile_of_this_published_project();
    }

    @When("^the user clicks the funding button in status widget$")
    public void the_user_clicks_the_funding_button_in_status_widget() throws Throwable {
        PageFactory.initElements(webDriver, projectDetailPage);
        savedPageYOffset = projectDetailPage.getPageYOffset();
        projectDetailPage.clickFundingButton();
    }

    @Then("^the browser scrolls to the funding widget$")
    public void the_browser_scrolls_to_the_funding_widget() throws Throwable {
        assertThat(projectDetailPage.getPageYOffset(), greaterThan(savedPageYOffset));
    }

    @And("^the tooltip for currency conversion is not visible$")
    public void the_tooltip_for_currency_conversion_is_not_visible() throws Throwable {
        assertFalse(projectDetailPage.currencyConversionTooltipVisible());
    }

    @When("^he hovers the currency element$")
    public void he_hovers_the_currency_element() throws Throwable {
        projectDetailPage.hoverCurrency();
    }

    @Then("^the tooltip for currency conversion is visible$")
    public void the_tooltip_for_currency_conversion_is_visible() throws Throwable {
        assertTrue(projectDetailPage.currencyConversionTooltipVisible());
    }
}
