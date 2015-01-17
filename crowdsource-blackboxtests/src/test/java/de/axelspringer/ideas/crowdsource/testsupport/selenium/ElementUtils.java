package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.WebElement;

public class ElementUtils {

    public static int parseCurrency(WebElement element) {
        String value = element.getText();
        value = value.substring(1); // cut off currency
        value = value.replaceAll("\\.", "");
        return Integer.parseInt(value);
    }
}
