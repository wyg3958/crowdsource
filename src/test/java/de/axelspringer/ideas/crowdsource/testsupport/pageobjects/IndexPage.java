package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for index page
 */
public class IndexPage {

    @FindBy(className = "hello")
    private WebElement helloDiv;

    public String helloText() {
        return helloDiv.getText();
    }
}
