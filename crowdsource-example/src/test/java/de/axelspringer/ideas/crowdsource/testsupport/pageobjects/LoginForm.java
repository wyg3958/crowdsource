package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class LoginForm {

    @FindBy(css = ".login-form input[name='email']")
    private WebElement emailInputField;

    @FindBy(css = ".login-form input[type='password']")
    private WebElement passwordInputField;

    @FindBy(css = ".login-form button[type='submit']")
    private WebElement saveButton;

    @FindBy(css = ".login-form .general-error span")
    private WebElement errorText;

    @FindBy(className = "password-recovery-link")
    private WebElement passwordRecoveryLink;

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

    public String getErrorText() {
        wait.until(visibilityOf(errorText));
        return errorText.getText();
    }

    public void clickPasswordRecoveryLink() {
        passwordRecoveryLink.click();
    }
}
