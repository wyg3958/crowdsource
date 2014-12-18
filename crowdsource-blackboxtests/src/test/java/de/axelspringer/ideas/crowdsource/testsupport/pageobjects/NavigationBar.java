package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class NavigationBar {

    @FindBy(css = "nav.top-bar a[href='#/login']")
    private WebElement loginLink;

    @FindBy(css = "nav.top-bar a[href='#/signup']")
    private WebElement signupLink;

    @FindBy(css = "nav.top-bar a[href='#/project/new']")
    private WebElement newProjectLink;

    @Autowired
    private SeleniumWait wait;

    public void clickSignup() {
        wait.until(visibilityOf(signupLink));
        signupLink.click();
    }

    public void clickLogin() {
        wait.until(visibilityOf(loginLink));
        loginLink.click();
    }

    public void clickNewProject() {
        wait.until(visibilityOf(newProjectLink));
        newProjectLink.click();
    }
}
