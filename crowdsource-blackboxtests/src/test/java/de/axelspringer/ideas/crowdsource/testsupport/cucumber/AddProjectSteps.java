package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.testsupport.CrowdSourceTestConfig;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.NavigationBar;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.ProjectsPage;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.AddProjectConfirmationView;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project.AddProjectForm;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@ContextConfiguration(classes = CrowdSourceTestConfig.class)
public class AddProjectSteps {

    @Autowired
    private NavigationBar navigationBar;

    @Autowired
    private AddProjectForm addProjectForm;

    @Autowired
    private AddProjectConfirmationView addProjectConfirmationView;

    @Autowired
    private ProjectsPage projectsPage;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    private WebDriver webDriver;
    private String randomProjectTitlePrefix;
    private String randomProjectShortDescriptionPrefix;

    @Before
    public void init() {
        webDriver = webDriverProvider.provideDriver();

        // logout
        webDriver.get(urlProvider.applicationUrl() + "/#/logout");
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

        addProjectForm.setTitle(randomProjectTitlePrefix + " Lorem ipsum dolor sit amet, consetetu");
        addProjectForm.setShortDescription(randomProjectShortDescriptionPrefix + " Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore");
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
        assertThat(projects, hasItem(Matchers.<Project>hasProperty("title", is(randomProjectTitlePrefix + " Lorem ipsum dolor sit\u2026"))));
        assertThat(projects, hasItem(Matchers.<Project>hasProperty("shortDescription", is(randomProjectShortDescriptionPrefix + " Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt\u2026"))));
    }

    @When("^he moves through the project creation process$")
    public void he_moves_through_the_project_creation_process() throws Throwable {
        he_clicks_on_the_New_Project_link_in_the_navigation_bar();
        he_is_redirected_to_the_project_creation_page();
        he_submits_the_form_with_valid_project_data();
        the_project_creation_success_page_is_shown();
    }
}
