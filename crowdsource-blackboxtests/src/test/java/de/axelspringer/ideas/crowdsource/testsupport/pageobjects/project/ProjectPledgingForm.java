package de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
public class ProjectPledgingForm {

    @FindBy(css = ".pledging-form .notification")
    private WebElement notificationBox;

    @FindBy(css = ".pledging-form .range-slider")
    private WebElement slider;

    @FindBy(css = ".pledging-form .pledged-amount")
    private WebElement pledgedAmountLabel;

    @FindBy(css = ".pledging-form .pledge-goal")
    private WebElement pledgeGoalLabel;

    @FindBy(css = ".pledging-form .finance__section .budget")
    private WebElement budgetLabel;

    @FindBy(css = ".pledging-form .finance__btn")
    private WebElement pledgingButton;

    @FindBy(css = ".pledging-form .finance__input")
    private WebElement amountInputField;

    public String getNotificationMessage() {
        return notificationBox.getText();
    }

    public String getUserBudget() {
        return budgetLabel.getText();
    }

    public WebElement getPledgingButton() {
        return pledgingButton;
    }

    public boolean isSliderEnabled() {
        return !slider.getAttribute("class").contains("disabled");
    }

    public WebElement getAmountInputField() {
        return amountInputField;
    }
}
