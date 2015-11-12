package de.asideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

public class ElementUtils {

    public static int parseCurrency(WebElement element) {
        String value = element.getText().replaceAll("€", "").trim();
        return parseNumber(value);
    }

    public static int parseNumber(String value) {
        value = value.replaceAll("\\.", "").trim();
        return Integer.parseInt(value);
    }

    public static boolean hasClass(WebElement webElement, String cssClass) {
        String[] classes = webElement.getAttribute("class").split(" ");
        return Arrays.asList(classes).contains(cssClass);
    }

    public static WebElement expectAndGetFirst(WebDriver webDriver, boolean exists, By selector) {
        List<WebElement> element = webDriver.findElements(selector);
        assertThat(element, hasSize(exists ? 1 : 0));
        return exists ? element.get(0) : null;
    }

    public static List<WebElement> expectAndGetAll(WebDriver webDriver, By selector) {
        List<WebElement> element = webDriver.findElements(selector);
        assertThat(element, is(not(empty())));
        return element;
    }
}
