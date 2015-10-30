package de.asideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AngularJsUtils {

    /**
     * Use this to wait until angularJs finished interpolating the value of the element located by the given locator.
     * It is assumed, that the text of the element being watched is interpolated by angularJs with the double curly brackets,
     * e.g.:
     * <p>
     * <some-element>{{ variable }}</some-element>
     * <p>
     * This method waits until the given element is found in DOM, does not start with "{{" and end with "}}" and is not blank
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

                if (isBlank(webElement.getText())) {
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
