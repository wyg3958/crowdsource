package de.axelspringer.ideas.crowdsource.testsupport.cucumber.hooks;

import cucumber.api.Scenario;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.joining;

public class BrowserLogFetchHook {

    private static final Logger log = LoggerFactory.getLogger(BrowserLogFetchHook.class);

    @Autowired
    private WebDriverProvider webDriverProvider;

    @cucumber.api.java.After
    public void after(Scenario result) {
        if (webDriverProvider.hasActiveWebDriver()) {
            WebDriver webDriver = webDriverProvider.provideDriver();

            if (result != null) {
                LogEntries logs = webDriver.manage().logs().get(LogType.BROWSER);

                if (log.isInfoEnabled()) {
                    String logOutput = logs.getAll().stream()
                            .map(LogEntry::toString)
                            .collect(joining("\n"));

                    log.info("Browser console.log output: {}", "\n" + logOutput);
                }
            }
        }
    }
}
