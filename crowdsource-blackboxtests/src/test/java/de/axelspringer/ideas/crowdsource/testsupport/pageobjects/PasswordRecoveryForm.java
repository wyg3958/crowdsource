package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class PasswordRecoveryForm {

    @FindBy(css = ".password-recovery-form input[name='email']")
    private WebElement emailInputField;

    @FindBy(css = ".password-recovery-form .form-controls-email .error")
    private WebElement emailFieldError;

    @FindBy(css = ".password-recovery-form [type=\"submit\"]")
    private WebElement submitButton;

    @Autowired
    private SeleniumWait wait;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;


    public void open() {
        webDriverProvider.provideDriver().get(urlProvider.applicationUrl() + "#/login/password-recovery");
        waitForPageLoad();
    }

    public void waitForPageLoad() {
        wait.until(presenceOfElementLocated(By.className("password-recovery-form")));
    }

    public void submitForm() {
        submitButton.click();
    }

    public void setEmailText(String emailName) {
        emailInputField.sendKeys(emailName);
    }
}
