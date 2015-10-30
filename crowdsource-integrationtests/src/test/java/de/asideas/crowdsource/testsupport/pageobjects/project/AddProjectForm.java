package de.asideas.crowdsource.testsupport.pageobjects.project;

import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

@Component
public class AddProjectForm {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @FindBy(css = ".project-form input[name='title']")
    private WebElement titleInputField;

    @FindBy(css = ".project-form textarea[name='shortDescription']")
    private WebElement shortDescriptionInputField;

    @FindBy(css = ".project-form input[name='pledgeGoal']")
    private WebElement pledgeGoalInputField;

    @FindBy(className = "currency")
    private WebElement currencyLabel;

    @FindBy(css = ".project-form textarea[name='description']")
    private WebElement descriptionInputField;

    @FindBy(css = ".project-form button[type='submit']")
    private WebElement submitButton;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(visibilityOfElementLocated(By.cssSelector(".project-form input[name='title']")));
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

    public boolean currencyConversionTooltipVisible() {
        final String tooltipSpanId = currencyLabel.getAttribute("data-selector");
        final WebElement tooltip = webDriverProvider.provideDriver().findElement(By.id(tooltipSpanId));
        return tooltip.getCssValue("display").equals("block");
    }

    public void hoverCurrency() {
        new Actions(webDriverProvider.provideDriver()).moveToElement(currencyLabel).perform();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
