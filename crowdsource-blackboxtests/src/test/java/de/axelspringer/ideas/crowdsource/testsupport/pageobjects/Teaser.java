package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.ElementUtils;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static de.axelspringer.ideas.crowdsource.testsupport.selenium.AngularJsUtils.interpolationCompletedOfElementLocated;

@Component
public class Teaser {

    @FindBy(css = "teaser > div")
    private WebElement teaserContainer;

    @FindBy(css = "teaser .remaining-budget")
    private WebElement remainingBudgetContainer;

    @FindBy(css = "teaser .remaining-budget .metrics__heading")
    private WebElement remainingBudget;

    @FindBy(css = "teaser .remaining-time .metrics__heading")
    private WebElement remainingTime;

    @FindBy(css = "teaser .user-count .metrics__heading")
    private WebElement userCount;

    @Autowired
    private SeleniumWait wait;

    public void waitForContentLoaded() {
        wait.until(interpolationCompletedOfElementLocated(By.cssSelector("teaser .remaining-time .metrics__heading")));
        wait.until(interpolationCompletedOfElementLocated(By.cssSelector("teaser .user-count .metrics__heading")));
    }

    public boolean isVisible() {
        return ElementUtils.hasClass(teaserContainer, "teaser--hero");
    }

    public boolean isRemainingBudgetItemVisible() {
        return remainingBudgetContainer.isDisplayed();
    }

    public int getRemainingBudget() {
        return ElementUtils.parseCurrency(remainingBudget);
    }

    public String getRemainingTimeString() {
        return remainingTime.getText();
    }

    public int getUserCount() {
        String[] words = userCount.getText().split(" ");
        if (words.length != 3) {
            throw new IllegalStateException("Expected 3 words, was: " + userCount.getText());
        }
        return Integer.parseInt(words[0]);
    }
}
