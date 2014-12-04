package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class IndexPage {

    private final static By HEADLINE_LOCATOR = By.cssSelector("div .content h1");

    @Autowired
    private SeleniumWait wait;

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void waitForPageLoad() {
        wait.until(presenceOfElementLocated(HEADLINE_LOCATOR));
    }

    public String getHeadlineText() {
        waitForPageLoad();
        final WebDriver webDriver = webDriverProvider.provideDriver();
        return webDriver.findElement(HEADLINE_LOCATOR).getText();
    }
}
