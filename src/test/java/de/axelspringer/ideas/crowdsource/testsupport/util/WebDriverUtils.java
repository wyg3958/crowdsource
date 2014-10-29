package de.axelspringer.ideas.crowdsource.testsupport.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WebDriverUtils {

    private final static Logger log = LoggerFactory.getLogger(WebDriverUtils.class);

    /**
     * @param phantomJsBinaryPath
     * @param chromeBinaryPath
     * @return {@link org.openqa.selenium.phantomjs.PhantomJSDriver}-instance if binary specified, {@link org.openqa.selenium.chrome.ChromeDriver} if binary specified (and not phantomjs), fallback to firefox
     */
    public static RemoteWebDriver provideDriver(String phantomJsBinaryPath, String chromeBinaryPath) {

        if (new File(phantomJsBinaryPath).exists()) {
            log.info("providing phantomjs driver");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("phantomjs.binary.path", phantomJsBinaryPath);
            return new PhantomJSDriver(capabilities);
        }

        if (new File(chromeBinaryPath).exists()) {
            log.info("providing chromedriver");
            System.setProperty("webdriver.chrome.driver", chromeBinaryPath);
            return new ChromeDriver();
        }

        log.info("providing firefox driver as phantomjs-binary was not specified or does not resolve in file system.");
        return new FirefoxDriver();
    }

    /**
     * @param driver will be shut-down (in case of phantom .quit() - else .(close))
     */
    public static void closeWebDriver(WebDriver driver) {

        try {
            if (driver instanceof PhantomJSDriver) {
                driver.quit();
            }
            driver.close();
        } catch (Exception e) {
            log.debug("exception closing webdriver", e);
        }
    }
}
