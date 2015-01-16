package de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.axelspringer.ideas.crowdsource.testsupport.selenium.AngularJsUtils.interpolationCompletedOfElementLocated;

@Component
public class ProjectDetailPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private SeleniumWait wait;

    @FindBy(css = ".project-details h1")
    private WebElement title;

    @FindBy(css = ".project-details h2")
    private WebElement shortDescription;

    @FindBy(css = ".project-details .project-description")
    private WebElement description;

    @Autowired
    private ProjectStatusWidget projectStatusWidget;


    public void openWithoutWaiting(String projectId) {
        webDriverProvider.provideDriver().get(urlProvider.applicationUrl() + "#/project/" + projectId);
    }

    public void open(String projectId) {
        openWithoutWaiting(projectId);
        waitForDetailsToBeLoaded();
    }

    public void waitForDetailsToBeLoaded() {
        wait.until(interpolationCompletedOfElementLocated(By.cssSelector(".project-details h1")));

        RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        PageFactory.initElements(webDriver, this);
        PageFactory.initElements(webDriver, projectStatusWidget);
    }

    public String getTitle() {
        return title.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public String getShortDescription() {
        return shortDescription.getText();
    }

    public ProjectStatusWidget getProjectStatusWidget() {
        return projectStatusWidget;
    }
}
