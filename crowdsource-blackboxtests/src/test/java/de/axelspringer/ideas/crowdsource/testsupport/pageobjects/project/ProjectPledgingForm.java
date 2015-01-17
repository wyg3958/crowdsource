package de.axelspringer.ideas.crowdsource.testsupport.pageobjects.project;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectPledgingForm {

    @FindBy(css = ".pledging-form .notification")
    private WebElement notificationBox;

    @FindBy(css = ".pledging-form .general-error")
    private WebElement errorMessageBox;

    @FindBy(css = ".pledging-form .range-slider")
    private WebElement slider;

    @FindBy(css = ".pledging-form .range-slider-handle")
    private WebElement sliderHandle;

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

    @FindBy(css = ".pledging-form .finance__btn")
    private WebElement submitButton;

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private SeleniumWait wait;

    public String getNotificationMessage() {
        return notificationBox.getText();
    }

    public String getErrorMessage() {
        return errorMessageBox.getText();
    }

    public int getUserBudget() {
        return parseCurrency(budgetLabel);
    }

    public int getPledgedAmount() {
        return parseCurrency(pledgedAmountLabel);
    }

    public int getPledgeGoalAmount() {
        return parseCurrency(pledgeGoalLabel);
    }

    private int parseCurrency(WebElement element) {
        String value = element.getText();
        value = value.substring(1); // cut off currency
        value = value.replaceAll("\\.", "");
        return Integer.parseInt(value);
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

    public void setAmountInputValue(int value) {
        amountInputField.clear();
        amountInputField.sendKeys(Integer.toString(value));
    }

    public int getAmountFromInputField() {
        return Integer.parseInt(amountInputField.getAttribute("value"));
    }

    public void moveSliderBy(int value) {
        new Actions(webDriverProvider.provideDriver())
                .dragAndDropBy(sliderHandle, value, 0)
                .perform();
    }

    public void submitForm() {
        submitButton.click();
    }

    public void waitUntilANotificationOrEerrorMessageIsDisplayed() {
        wait.until(driver -> getNotificationMessage().length() > 0 || getErrorMessage().length() > 0);
    }
}
