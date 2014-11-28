package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.util.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class RegistrationForm {

    @FindBy(css = "form[name='signupForm'] .form-controls-email input[name='email']")
    private WebElement emailInputField;

    @FindBy(css = "form[name='signupForm'] .form-controls-email .error")
    private WebElement emailFieldError;

    @FindBy(css = "form[name='signupForm'] .form-controls-termsofservice input[name='termsOfServiceAccepted']")
    private WebElement acceptTosCheckbox;

    @FindBy(css = "form[name='signupForm'] button[type='submit']")
    private WebElement registerButton;

    @Autowired
    private SeleniumWait wait;

    public void setEmailText(String text) {
        wait.until(visibilityOf(emailInputField));
        emailInputField.sendKeys(text);
    }

    public void checkAcceptTosCheckbox(){
        wait.until(visibilityOf(acceptTosCheckbox));
        if(!acceptTosCheckbox.isSelected()){
            acceptTosCheckbox.click();
        }
    }

    public void uncheckAcceptTosCheckbox(){
        wait.until(visibilityOf(acceptTosCheckbox));
        if(acceptTosCheckbox.isSelected()){
            acceptTosCheckbox.click();
        }
    }

    public void submit(){
        wait.until(visibilityOf(registerButton));
        registerButton.click();
    }

    public String getEmailFieldErrorText() {
        return emailFieldError.getText();
    }
}
