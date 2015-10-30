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

    @FindBy(css = "status-bar .budget .sbar__pill")
    private WebElement budgetLabel;

    public boolean isBudgetDisplayed() {
        return webDriverProvider.provideDriver().findElements(By.cssSelector("status-bar .budget")).size() == 1;
    }

    public int getBudget() {
        return ElementUtils.parseCurrency(budgetLabel);
    }
}
