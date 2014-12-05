package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class LoginForm {

    @FindBy(css = "form[name='loginForm'] input[name='email']")
    private WebElement emailInputField;

    @FindBy(css = "form[name='loginForm'] input[type='password']")
    private WebElement passwordInputField;

    @FindBy(css = "form[name='loginForm'] button[type='submit']")
    private WebElement saveButton;

    @FindBy(css = "form[name='loginForm'] .general-error span")
    private WebElement errorText;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(visibilityOf(emailInputField));
        wait.until(visibilityOf(passwordInputField));
        wait.until(visibilityOf(saveButton));
    }

    public void login(String email, String password) {
        waitForPageLoad();
        emailInputField.sendKeys(email);
        passwordInputField.sendKeys(password);
        saveButton.click();
    }

    public String getErrorText(){
        wait.until(visibilityOf(errorText));
        return errorText.getText();
    }
}
