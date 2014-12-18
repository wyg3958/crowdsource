package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class ActivationForm {

    @FindBy(css = ".activation-form .form-controls-password input[type='password']")
    private WebElement passwordInputField;

    @FindBy(css = ".activation-form .form-controls-repeated-password input[type='password']")
    private WebElement repeatPasswordInputField;

    @FindBy(css = ".activation-form button[type='submit']")
    private WebElement saveButton;

    @FindBy(css = ".activation-form .general-error span")
    private WebElement errorText;

    @Autowired
    private SeleniumWait wait;

    public void setPasswordText(String text) {
        wait.until(visibilityOf(passwordInputField));
        passwordInputField.sendKeys(text);
    }

    public void setRepeatPasswordText(String text) {
        wait.until(visibilityOf(repeatPasswordInputField));
        repeatPasswordInputField.sendKeys(text);
    }

    public void submit() {
        wait.until(visibilityOf(saveButton));
        saveButton.click();
    }

    public String getErrorText(){
        wait.until(visibilityOf(errorText));
        return errorText.getText();
    }
}
