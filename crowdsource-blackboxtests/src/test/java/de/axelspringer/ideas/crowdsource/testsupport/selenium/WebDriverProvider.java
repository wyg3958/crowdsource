package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.logging.Level;

/**
 * Stateful! Holds WebDriver-Instance
 */
@Service
public class WebDriverProvider {

    public static final int DESKTOP_WIDTH = 1680;
    public static final int MOBILE_WIDTH = 400;

    private final static Logger LOG = LoggerFactory.getLogger(WebDriverProvider.class);

    private static RemoteWebDriver DRIVER_INSTANCE;

    @Value("${de.axelspringer.ideas.crowdsource.test.phantomjs.binary:unset}")
    private String phantomBinaryPath;

    @Value("${de.axelspringer.ideas.crowdsource.test.chrome.binary:unset}")
    private String chromeBinaryPath;

    /**
     * will close driver instance
     */
    public static void closeWebDriver() {

        if (DRIVER_INSTANCE == null) {
            return;
        }
        // get handle of driver
        RemoteWebDriver driverHandle = DRIVER_INSTANCE;

        // break class reference
        DRIVER_INSTANCE = null;

        // close old driver
        try {
            if (driverHandle instanceof FirefoxDriver) {
                driverHandle.close();
            } else {
                driverHandle.quit();
            }
        } catch (Exception e) {
            LOG.warn("exception closing webdriver: {}", e.getMessage());
        }
    }

    /**
     * @return {@link org.openqa.selenium.phantomjs.PhantomJSDriver}-instance if binary specified, {@link org.openqa.selenium.chrome.ChromeDriver} if binary specified (and not phantomjs), fallback to firefox
     */
    public RemoteWebDriver provideDriver() {

        if (DRIVER_INSTANCE == null) {
            if (new File(phantomBinaryPath).exists()) {
                LOG.info("providing phantomjs driver");

                DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomBinaryPath);
                enableLogging(capabilities);

                DRIVER_INSTANCE = new PhantomJSDriver(capabilities);
            } else if (new File(chromeBinaryPath).exists()) {
                LOG.info("providing chromedriver");
                System.setProperty("webdriver.chrome.driver", chromeBinaryPath);

                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                enableLogging(capabilities);

                DRIVER_INSTANCE = new ChromeDriver(capabilities);
            } else {
                LOG.info("providing firefox driver as phantomjs-binary was not specified or does not resolve in file system.");

                DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                enableLogging(capabilities);

                DRIVER_INSTANCE = new FirefoxDriver(capabilities);
            }
        }

        DRIVER_INSTANCE.manage().window().setSize(new Dimension(DESKTOP_WIDTH, 800));
        return DRIVER_INSTANCE;
    }

    public RemoteWebDriver provideMobileDriver() {

        RemoteWebDriver driver = provideDriver();
        driver.manage().window().setSize(new Dimension(WebDriverProvider.MOBILE_WIDTH, 800));
        return driver;
    }

    private void enableLogging(DesiredCapabilities capabilities) {
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
    }

    public boolean hasActiveWebDriver() {
        return DRIVER_INSTANCE != null;
    }
}
