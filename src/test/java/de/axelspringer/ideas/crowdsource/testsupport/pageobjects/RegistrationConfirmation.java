package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegistrationConfirmation {

    @FindBy(xpath = "//strong[contains(.,'axelspringer.de')]")
    private WebElement confirmationMessage;

    public String getConfirmationMessage(){
        return confirmationMessage.getText();
    }
}
