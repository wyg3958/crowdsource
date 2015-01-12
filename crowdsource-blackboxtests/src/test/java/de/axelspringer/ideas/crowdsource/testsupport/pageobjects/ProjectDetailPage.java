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
        wait.until(presenceOfElementLocated(By.xpath("//h2[.='" + shortDescription + "']")));
    }

    public void open(String projectId) {
        webDriverProvider.provideDriver().get(urlProvider.applicationUrl() + "#/project/" + projectId);
    }

    public void waitForStatusWidgetToBeAvailable() {
        //check div containers
        wait.until(presenceOfElementLocated(By.cssSelector(".project-status")));
    }

    public String getStatusWidgetProgressBarValue() {
        return webDriverProvider.provideDriver().findElement(By.cssSelector(".project-status__funding progress-bar .meter")).getCssValue("width");
    }

    public String getStatusWidgetPledgedAmount() {
        return webDriverProvider.provideDriver().findElement(By.className("project-status__pledged-amount")).getText();
    }

    public String getStatusWidgetPledgeGoal() {
        return webDriverProvider.provideDriver().findElement(By.className("project-status__pledge-goal")).getText();
    }

    public String getStatusWidgetBackers() {
        return webDriverProvider.provideDriver().findElement(By.className("project-status__backers")).getText();
    }

    public String getStatusWidgetUserName() {
        return webDriverProvider.provideDriver().findElement(By.cssSelector(".project-status__creator strong")).getText();
    }

    public void clickFundingButton() {
        webDriverProvider.provideDriver().findElement(By.className("to-pledging-form-button")).click();
    }

    public int getPageYOffset() {
        Object pageYOffset = webDriverProvider.provideDriver().executeScript("return window.pageYOffset;");
        return ((Long) pageYOffset).intValue();
    }
}
