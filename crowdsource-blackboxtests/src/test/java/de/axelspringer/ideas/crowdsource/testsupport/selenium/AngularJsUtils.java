package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

public class AngularJsUtils {

    /**
     * Use this to wait until angularJs finished interpolating the value of the element located by the given locator.
     * It is assumed, that the text of the element being watched is interpolated by angularJs with the double curly brackets,
     * e.g.:
     *
     * <some-element>{{ variable }}</some-element>
     *
     * This method waits until the given element is found in DOM and it does not start with "{{" and end with "}}"
     */
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
