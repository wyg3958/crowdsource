package de.axelspringer.ideas.crowdsource.testsupport.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Stateful! Holds WebDriver-Instance
 */
@Service
public class WebDriverProvider {

    private final static Logger LOG = LoggerFactory.getLogger(WebDriverProvider.class);
    private static RemoteWebDriver driverInstance;
    @Value("${de.axelspringer.ideas.crowdsource.test.phantomjs.binary:unset}")
    private String phantomBinaryPath;
    @Value("${de.axelspringer.ideas.crowdsource.test.chrome.binary:unset}")
    private String chromeBinaryPath;

    /**
     * will close driver instance
     */
    public static void closeWebDriver() {

        if (driverInstance == null) {
            return;
        }
        // get handle of driver
        WebDriver driverHandle = driverInstance;

        // break class reference
        driverInstance = null;

        // close old driver
        try {
            if (driverHandle instanceof PhantomJSDriver) {
                driverHandle.quit();
            }
            driverHandle.close();
        } catch (Exception e) {
            LOG.debug("exception closing webdriver", e);
        }
    }

    /**
     * @return {@link org.openqa.selenium.phantomjs.PhantomJSDriver}-instance if binary specified, {@link org.openqa.selenium.chrome.ChromeDriver} if binary specified (and not phantomjs), fallback to firefox
     */
    public RemoteWebDriver provideDriver() {

        if (driverInstance != null) {
            return driverInstance;
        }

        if (new File(phantomBinaryPath).exists()) {
            LOG.info("providing phantomjs driver");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("phantomjs.binary.path", phantomBinaryPath);
            driverInstance = new PhantomJSDriver(capabilities);
        } else if (new File(chromeBinaryPath).exists()) {
            LOG.info("providing chromedriver");
            System.setProperty("webdriver.chrome.driver", chromeBinaryPath);
            driverInstance = new ChromeDriver();
        } else {
            LOG.info("providing firefox driver as phantomjs-binary was not specified or does not resolve in file system.");
            driverInstance = new FirefoxDriver();
        }

        return driverInstance;
    }
}
