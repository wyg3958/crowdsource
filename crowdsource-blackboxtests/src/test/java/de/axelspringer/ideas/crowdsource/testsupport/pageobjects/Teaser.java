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

    @FindBy(css = "teaser .test-remaining-budget")
    private WebElement remainingBudget;

    @FindBy(css = "teaser .test-remaining-time")
    private WebElement remainingTime;

    @FindBy(css = "teaser .test-user-count")
    private WebElement userCount;

    @Autowired
    private SeleniumWait wait;

    public void waitForContentLoaded() {
        wait.until(interpolationCompletedOfElementLocated(By.cssSelector("teaser .test-remaining-time")));
        wait.until(interpolationCompletedOfElementLocated(By.cssSelector("teaser .test-user-count")));
    }

    public boolean isVisible() {
        return ElementUtils.hasClass(teaserContainer, "teaser--hero");
    }

    public int getRemainingBudget() {
        return ElementUtils.parseCurrency(remainingBudget);
    }

    public String getRemainingTimeString() {
        return remainingTime.getText();
    }

    public int getUserCount() {
        return ElementUtils.parseNumber(userCount.getText());
    }
}
