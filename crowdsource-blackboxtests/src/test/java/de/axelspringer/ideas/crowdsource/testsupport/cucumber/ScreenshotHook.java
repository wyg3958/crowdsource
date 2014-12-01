package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import cucumber.api.Scenario;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class ScreenshotHook {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @cucumber.api.java.After
    public void after(Scenario result) {
        if (webDriverProvider.hasActiveWebDriver()) {
            WebDriver webDriver = webDriverProvider.provideDriver();

            if (result != null) {
                WebDriverUtils.makeScreenshot(webDriver, result);
            }
        }
    }
}
