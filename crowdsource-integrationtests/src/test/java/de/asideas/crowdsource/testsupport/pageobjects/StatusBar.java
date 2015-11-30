package de.asideas.crowdsource.testsupport.pageobjects;

import de.asideas.crowdsource.testsupport.selenium.ElementUtils;
import de.asideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusBar {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @FindBy(css = "status-bar .user-budget .sbar__pill")
    private WebElement budgetLabelUser;

    @FindBy(css = "status-bar .postroundbudget-remaining .sbar__pill")
    private WebElement budgetLabelPostRound;

    public boolean isUserBudgetDisplayed() {
        return webDriverProvider.provideDriver().findElements(By.cssSelector("status-bar .user-budget")).size() == 1;
    }
    public boolean isPostRoundBudgetDisplayed() {
        return webDriverProvider.provideDriver().findElements(By.cssSelector("status-bar .postroundbudget-remaining")).size() == 1;
    }

    public int getUserBudget() {
        return ElementUtils.parseCurrency(budgetLabelUser);
    }

    public int getPostRoundBudget() {
        return ElementUtils.parseCurrency(budgetLabelPostRound);
    }
}
