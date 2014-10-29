package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for index page
 */
public class IndexPage {

    @FindBy(className = "example")
    private WebElement exampleElement;

    public String exampleElementText() {
        return exampleElement.getText();
    }
}
