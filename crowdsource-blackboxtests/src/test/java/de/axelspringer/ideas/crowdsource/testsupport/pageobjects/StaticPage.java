package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void clickLinkByLinkText(final String linkText) {
        webDriverProvider.provideDriver().findElement(By.xpath("//a[.='" + linkText + "']")).click();
    }

    public void waitForTextToBeAvailable(final String text) {
        webDriverProvider.provideDriver().findElement(By.xpath("//*[contains(.,'" + text + "')]"));
    }

    public void ensureTextInParagraphIsNotAvailable(final String text) {
        webDriverProvider.provideDriver().findElements(By.xpath("//p[contains(.,'" + text + "')]"));
    }
}
