package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.WebElement;

import java.util.Arrays;

public class ElementUtils {

    public static int parseCurrency(WebElement element) {
        String value = element.getText();
        value = value.substring(1); // cut off currency
        return parseNumber(value);
    }

    public static int parseNumber(String value) {
        value = value.replaceAll("\\.", "");
        return Integer.parseInt(value);
    }

    public static boolean hasClass(WebElement webElement, String cssClass) {
        String[] classes = webElement.getAttribute("class").split(" ");
        return Arrays.asList(classes).contains(cssClass);
    }
}
