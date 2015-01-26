package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectDetailPage;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectStatusWidget;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.ProjectsPage;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverUtils;
import de.axelspringer.ideas.crowdsource.testsupport.util.CrowdSourceClient;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class ProjectDetailSteps {

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
    private Project createdProject;
    private int savedPageYOffset;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();
    }

    @Given("^a project is available$")
    public void a_project_is_available() throws Throwable {
        a_project_is_available(25);
    }

    @Given("^a project with a pledge goal of (\\d+) is available$")
    public void a_project_is_available(int pledgeGoal) throws Throwable {
        createdProject = new Project();
        createdProject.setTitle("T" + RandomStringUtils.randomAlphanumeric(6));
        createdProject.setShortDescription("Short description " + RandomStringUtils.randomAlphanumeric(16));
        createdProject.setPledgeGoal(pledgeGoal);
        createdProject.setDescription("This is the project description text." + RandomStringUtils.random(1000, "\nabc").trim());

        CrowdSourceClient.AuthToken authToken = crowdSourceClient.authorizeWithDefaultUser();
        createdProject = crowdSourceClient.createProject(createdProject, authToken).getBody();
    }

    @When("^the user clicks on the tile of this project$")
    public void the_user_clicks_on_the_tile_of_this_project() throws Throwable {
        PageFactory.initElements(webDriver, projectsPage);
        projectsPage.waitForPageLoad();
        projectsPage.clickProjectTileWithTitle(createdProject.getTitle());
    }

    @Then("^the project detail page of this project is displayed$")
    public void the_project_detail_page_of_this_project_is_displayed() throws Throwable {
        projectDetailPage.waitForDetailsToBeLoaded();

        assertThat(projectDetailPage.getTitle(), is(createdProject.getTitle()));
        assertThat(projectDetailPage.getShortDescription(), is(createdProject.getShortDescription()));
        assertThat(projectDetailPage.getDescription(), is(createdProject.getDescription()));

        ProjectStatusWidget projectStatusWidget = projectDetailPage.getProjectStatusWidget();
        assertThat(projectStatusWidget.getProgressBarValue(), is("0px"));
        assertThat(projectStatusWidget.getPledgedAmount(), is("$0"));
        assertThat(projectStatusWidget.getPledgeGoal(), is("$25"));
        assertThat(projectStatusWidget.getBackers(), is("0"));
        assertThat(projectStatusWidget.getUserName(), is("Crowdsource"));
    }

    @Given("^the user requests the project detail page with a non existant project id$")
    public void the_user_requests_the_project_detail_page_with_a_non_existant_project_id() throws Throwable {
        projectDetailPage.openWithoutWaiting("i-dont-exist-project-id");
    }

    @Given("^the user is on a project detail page$")
    public void the_user_is_on_a_project_detail_page() throws Throwable {
        a_project_is_available();

        webDriver.get(urlProvider.applicationUrl());
        projectsPage.waitForPageLoad();

        the_user_clicks_on_the_tile_of_this_project();
    }

    @When("^the user clicks the funding button in status widget$")
    public void the_user_clicks_the_funding_button_in_status_widget() throws Throwable {
        ProjectStatusWidget projectStatusWidget = projectDetailPage.getProjectStatusWidget();
        PageFactory.initElements(webDriver, projectStatusWidget);

        savedPageYOffset = WebDriverUtils.getPageYOffset(webDriver);
        projectStatusWidget.clickFundingButton();
    }

    @Then("^the browser scrolls to the funding widget$")
    public void the_browser_scrolls_to_the_funding_widget() throws Throwable {
        assertThat(WebDriverUtils.getPageYOffset(webDriver), greaterThan(savedPageYOffset));
    }

    @And("^the project detail page of this project is (requested|reloaded)$")
    public void the_project_detail_page_of_this_project_is_requested(String dummy) throws Throwable {
        projectDetailPage.open(createdProject.getId());
    }

    public Project getCreatedProject() {
        return createdProject;
    }

    @And("^the \"([^\"]*)\"-button is not visible$")
    public void the_button_is_not_visible(String buttonName) throws Throwable {
        assertTrue(webDriver.findElements(By.className("project-" + buttonName)).size() == 0);
    }

    @And("^the \"([^\"]*)\"-button is visible$")
    public void the_button_is_visible(String buttonName) throws Throwable {
        assertTrue(webDriver.findElements(By.className("project-" + buttonName)).size() == 1);
    }

    @When("^the \"([^\"]*)\"-button is clicked$")
    public void the_button_is_clicked(String buttonName) throws Throwable {
        webDriver.findElement(By.className("project-" + buttonName)).click();
    }
}
