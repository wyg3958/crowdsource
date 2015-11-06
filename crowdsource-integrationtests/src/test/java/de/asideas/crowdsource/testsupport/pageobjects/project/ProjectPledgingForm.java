package de.asideas.crowdsource.testsupport.pageobjects.project;

import de.asideas.crowdsource.testsupport.selenium.ElementUtils;
import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectPledgingForm {

    private static final String SELECTOR_BUDGET = ".pledging-form .finance__section .budget";
    private static final String SELECTOR_PLEDGED_AMOUNT = ".pledging-form .pledged-amount";

    @FindBy(css = ".pledging-form .notification")
    private WebElement notificationBox;

    @FindBy(css = ".pledging-form .general-error")
    private WebElement errorMessageBox;

    @FindBy(css = ".pledging-form .range-slider")
    private WebElement slider;

    @FindBy(css = ".pledging-form .range-slider-handle")
    private WebElement sliderHandle;

    @FindBy(css = SELECTOR_PLEDGED_AMOUNT)
    private WebElement pledgedAmountLabel;

    @FindBy(css = ".pledging-form .pledge-goal")
    private WebElement pledgeGoalLabel;

    @FindBy(css = SELECTOR_BUDGET)
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
        wait.until( d -> (updatedWebElement(d, SELECTOR_BUDGET).getText().length() != 0) );
        return ElementUtils.parseCurrency(updatedWebElement(webDriverProvider.provideDriver(), SELECTOR_BUDGET));
    }

    public int getPledgedAmount() {
        wait.until( d -> (updatedWebElement(d, SELECTOR_PLEDGED_AMOUNT).getText().length() != 0) );
        return ElementUtils.parseCurrency(updatedWebElement(webDriverProvider.provideDriver(), SELECTOR_PLEDGED_AMOUNT));
    }

    public int getPledgeGoalAmount() {
        return ElementUtils.parseCurrency(pledgeGoalLabel);
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

    private WebElement updatedWebElement(WebDriver d, String cssSelector) {
        return d.findElement(By.cssSelector(cssSelector));
    }
}
