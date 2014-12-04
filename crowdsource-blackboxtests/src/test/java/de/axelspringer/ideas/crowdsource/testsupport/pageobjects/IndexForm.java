package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class IndexForm {

    @Autowired
    private SeleniumWait wait;

    @Autowired
    private WebDriverProvider webDriverProvider;

    public String getContentHeadingText() {
        final WebDriver webDriver = webDriverProvider.provideDriver();

        final By locator = By.xpath("//h1[contains(.,'Crowdsource says hi')]");
        wait.until(presenceOfElementLocated(locator));
        return webDriver.findElement(locator).getText();
    }
}
