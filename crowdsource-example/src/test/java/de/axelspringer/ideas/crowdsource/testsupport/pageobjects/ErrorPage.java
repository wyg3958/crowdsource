package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class ErrorPage {

    @Autowired
    private SeleniumWait wait;

    public void waitForNotFoundPageLoad() {
        wait.until(presenceOfElementLocated(By.className("error-notfound")));
    }
}
