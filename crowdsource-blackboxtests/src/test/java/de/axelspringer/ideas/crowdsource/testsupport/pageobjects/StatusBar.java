package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.ElementUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
public class StatusBar {

    @FindBy(css = "status-bar .row")
    private WebElement statusBar;

    @FindBy(css = "status-bar .budget .sbar__pill")
    private WebElement budgetLabel;

    public boolean isVisible() {
        return statusBar.isDisplayed();
    }

    public int getBudget() {
        return ElementUtils.parseCurrency(budgetLabel);
    }
}
