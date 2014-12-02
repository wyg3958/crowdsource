package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import cucumber.api.Scenario;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

@Slf4j
public class WebDriverUtils {

    public static void makeScreenshot(WebDriver driver, Scenario scenario) {
        log.debug("Capturing screenshot for scenario {}", scenario.getName());
        if (driver instanceof TakesScreenshot) {
            TakesScreenshot screenshotableDriver = (TakesScreenshot) driver;
            try {
                byte[] screenshot = screenshotableDriver.getScreenshotAs(OutputType.BYTES);
                scenario.embed(screenshot, "image/png");
            } catch (WebDriverException somePlatformsDontSupportScreenshots) {
                log.error(somePlatformsDontSupportScreenshots.getMessage());
            }
        } else {
            log.warn("This web driver implementation {} cannot create screenshots", driver.getClass().getName());
        }
    }

}
