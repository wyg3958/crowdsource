package de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class AddProjectForm {

    @FindBy(css = ".project-form input[name='title']")
    private WebElement titleInputField;

    @FindBy(css = ".project-form textarea[name='shortDescription']")
    private WebElement shortDescriptionInputField;

    @FindBy(css = ".project-form input[name='pledgeGoal']")
    private WebElement pledgeGoalInputField;

    @FindBy(css = ".project-form textarea[name='description']")
    private WebElement descriptionInputField;

    @FindBy(css = ".project-form button[type='submit']")
    private WebElement submitButton;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(visibilityOf(titleInputField));
    }

    public void setTitle(String title) {
        titleInputField.sendKeys(title);
    }

    public void setShortDescription(String shortDescription) {
        shortDescriptionInputField.sendKeys(shortDescription);
    }

    public void setPledgeGoal(String pledgeGoal) {
        pledgeGoalInputField.sendKeys(pledgeGoal);
    }

    public void setDescription(String description) {
        descriptionInputField.sendKeys(description);
    }

    public void submit() {
        submitButton.click();
    }
}
