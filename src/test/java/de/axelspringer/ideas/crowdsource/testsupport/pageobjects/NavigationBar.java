package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NavigationBar {

    @FindBy(name = "faq")
    private WebElement faqLink;

    @FindBy(name = "login")
    private WebElement loginLink;

    @FindBy(xpath = "//a[@href='#/signup']")
    private WebElement signupLink;

    public void clickFaq() {
        faqLink.click();
    }

    public void clickLogin() {
        loginLink.click();
    }

    public void clickSignup() {
        signupLink.click();
    }
}
