package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class ProjectDetailPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private SeleniumWait wait;

    public void waitForTitleToBeAvailable(String title) {
        wait.until(presenceOfElementLocated(By.xpath("//h1[.='" + title + "']")));
    }

    public void waitForDescriptionToBeAvailable(String description) {
        wait.until(presenceOfElementLocated(By.xpath("//div[.='" + description + "']")));
    }

    public void waitForShortDescriptionToBeAvailable(String shortDescription) {
        wait.until(presenceOfElementLocated(By.xpath("//blockquote[.='" + shortDescription + "']")));
    }

    public void open(String projectId) {
        webDriverProvider.provideDriver().get(urlProvider.applicationUrl() + "#/project/" + projectId);
    }
}
