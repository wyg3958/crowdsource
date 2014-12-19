package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class LogoutPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(presenceOfElementLocated(By.cssSelector(".logout-success")));
    }

    public void open() {
        WebDriver webDriver = webDriverProvider.provideDriver();
        // logout
        webDriver.get(urlProvider.applicationUrl() + "/#/logout");
    }
}
