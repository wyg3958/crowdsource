package de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project;

import de.axelspringer.ideas.crowdsource.model.presentation.project.Project;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class ProjectsPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(presenceOfElementLocated(By.cssSelector(".project-tile")));
    }

    public List<Project> getProjects() {
        RemoteWebDriver webDriver = webDriverProvider.provideDriver();

        List<WebElement> projectTiles = webDriver.findElements(By.cssSelector(".project-tile"));

        return projectTiles.stream()
                .map(projectTile -> {
                    Project project = new Project();
                    project.setTitle(projectTile.findElement(By.cssSelector("h1")).getText());
                    project.setShortDescription(projectTile.findElement(By.cssSelector("p")).getText());
                    return project;
                }).collect(toList());
    }

    public void clickProjectTileWithTitle(String title) {
        RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        final WebElement projectTile = webDriver.findElement(By.xpath("//h1[text()='" + title + "']"));
        projectTile.click();
    }
}
