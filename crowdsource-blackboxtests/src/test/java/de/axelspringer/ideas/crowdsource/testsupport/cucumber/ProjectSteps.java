package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;

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

    private WebDriver webDriver;
    private String randomProjectTitlePrefix;
    private String randomProjectShortDescriptionPrefix;
    private Project createdProject;

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
        createdProject.setDescription("This is the project description text.");

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
    }

    @Given("^the user requests the project detail page with a non existant project id$")
    public void the_user_requests_the_project_detail_page_with_a_non_existant_project_id() throws Throwable {
        projectDetailPage.open("i-dont-exist-project-id");
    }
}
