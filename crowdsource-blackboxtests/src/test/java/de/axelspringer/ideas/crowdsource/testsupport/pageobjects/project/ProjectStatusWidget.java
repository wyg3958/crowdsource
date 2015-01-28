package de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectStatusWidget {

    @Autowired
    private SeleniumWait wait;

    @FindBy(css = ".project-status__funding progress-bar .cs-progress__meter")
    private WebElement progressBar;

    @FindBy(className = "project-status__pledged-amount")
    private WebElement pledgeAmountLabel;

    @FindBy(className = "project-status__pledge-goal")
    private WebElement pledgeGoalLabel;

    @FindBy(className = "project-status__backers")
    private WebElement backersLabel;

    @FindBy(css = ".project-status__creator strong")
    private WebElement userLabel;

    @FindBy(className = "to-pledging-form-button")
    private WebElement scrollToPledgingFormButton;


    public String getProgressBarValue() {
        return progressBar.getCssValue("width");
    }

    public String getPledgedAmount() {
        return pledgeAmountLabel.getText();
    }

    public String getPledgeGoal() {
        return pledgeGoalLabel.getText();
    }

    public String getBackers() {
        return backersLabel.getText();
    }

    public String getUserName() {
        return userLabel.getText();
    }

    public void clickFundingButton() {
        scrollToPledgingFormButton.click();
    }
}
