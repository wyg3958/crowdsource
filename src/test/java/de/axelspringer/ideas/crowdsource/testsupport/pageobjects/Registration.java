package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Registration {

    @FindBy(name = "email")
    private WebElement emailInputField;

    @FindBy(name = "termsOfServiceAccepted")
    private WebElement acceptTosCheckbox;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement registerButton;

    public void setEmailText(String text) {
        emailInputField.sendKeys(text);
    }

    public void checkAcceptTosCheckbox(){
        if(!acceptTosCheckbox.isSelected()){
            acceptTosCheckbox.click();
        }
    }

    public void uncheckAcceptTosCheckbox(){
        if(acceptTosCheckbox.isSelected()){
            acceptTosCheckbox.click();
        }
    }

    public void submit(){
        registerButton.click();
    }
}
