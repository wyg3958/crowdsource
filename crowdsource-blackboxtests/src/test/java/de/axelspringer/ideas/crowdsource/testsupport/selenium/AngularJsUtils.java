package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

public class AngularJsUtils {

    public static ExpectedCondition<Boolean> interpolationCompletedOfElementLocated(final By locator) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                List<WebElement> elements = webDriver.findElements(locator);
                if (elements.size() != 1) {
                    return false;
                }

                WebElement webElement = elements.get(0);
                if (webElement.getText().startsWith("{{") && webElement.getText().endsWith("}}")) {
                    return false;
                }
                return true;
            }

            @Override
            public String toString() {
                return "interpolation completed of element located by: " + locator;
            }
        };
    }
}
