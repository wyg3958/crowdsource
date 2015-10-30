package de.asideas.crowdsource.testsupport.pageobjects;

import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class ActivationForm {

    @FindBy(css = ".activation-form h1")
    private WebElement headline;

    @FindBy(css = ".activation-form .info-text")
    private WebElement infoText;

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

    public void waitForPageLoad() {
        wait.until(presenceOfElementLocated(By.cssSelector(".activation-form")));
    }

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

    public String getErrorText() {
        wait.until(visibilityOf(errorText));
        return errorText.getText();
    }

    public String getHeadline() {
        return headline.getText();
    }

    public String getInfoText() {
        return infoText.getText();
    }

}
