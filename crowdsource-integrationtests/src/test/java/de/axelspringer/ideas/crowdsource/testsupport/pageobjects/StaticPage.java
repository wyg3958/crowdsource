package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class StaticPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private SeleniumWait wait;

    public void clickLinkByLinkText(final String linkText) {
        webDriverProvider.provideDriver().findElement(By.xpath("//a[.='" + linkText + "']")).click();
    }

    public void waitForTextToBeAvailable(final String text) {
        wait.until(presenceOfElementLocated(By.xpath("//*[contains(.,'" + text + "')]")));
    }

    public void ensureTextInParagraphIsNotAvailable(final String text) {
        webDriverProvider.provideDriver().findElements(By.xpath("//p[contains(.,'" + text + "')]"));
    }
}
