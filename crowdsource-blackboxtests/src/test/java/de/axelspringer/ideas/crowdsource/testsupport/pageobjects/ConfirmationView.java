package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class ConfirmationView {

    @FindBy(css = ".confirmation-view h1")
    private WebElement headline;

    @FindBy(css = ".confirmation-view .email-recipient")
    private WebElement confirmedEmailAddress;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".confirmation-view .email-recipient")));
    }

    public String getHeadline() {
        return headline.getText();
    }

    public String getConfirmedEmailAddress() {
        wait.until(visibilityOf(confirmedEmailAddress));
        return confirmedEmailAddress.getText();
    }
}
