package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.util.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class RegistrationConfirmationView {

    @FindBy(css = ".registration-success .email-recipient")
    private WebElement confirmedEmailAddress;

    @Autowired
    private SeleniumWait wait;

    public String getConfirmedEmailAddress() {
        wait.until(visibilityOf(confirmedEmailAddress));
        return confirmedEmailAddress.getText();
    }
}
