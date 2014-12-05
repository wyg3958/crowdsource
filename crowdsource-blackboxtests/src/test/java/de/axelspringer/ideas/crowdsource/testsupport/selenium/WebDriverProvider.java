package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
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
        WebDriver driverHandle = DRIVER_INSTANCE;

        // break class reference
        DRIVER_INSTANCE = null;

        // close old driver
        try {
            if (driverHandle instanceof PhantomJSDriver) {
                //((PhantomJSDriver) driverHandle).executeScript("window.localStorage.clear()");
                driverHandle.quit();
            }
            driverHandle.close();
        } catch (Exception e) {
            LOG.debug("exception closing webdriver: {}", e.getMessage());
        }
    }

    /**
     * @return {@link org.openqa.selenium.phantomjs.PhantomJSDriver}-instance if binary specified, {@link org.openqa.selenium.chrome.ChromeDriver} if binary specified (and not phantomjs), fallback to firefox
     */
    public RemoteWebDriver provideDriver() {

        if (DRIVER_INSTANCE != null) {
            return DRIVER_INSTANCE;
        }

        if (new File(phantomBinaryPath).exists()) {
            LOG.info("providing phantomjs driver");
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomBinaryPath);
//            final File phantomJsLocalStorage;
//            try {
//                phantomJsLocalStorage = Files.createTempDirectory("phantomJsLocalStorage").toFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[]{"--local-storage-path=" + phantomJsLocalStorage.getAbsolutePath()});
            DRIVER_INSTANCE = new PhantomJSDriver(capabilities);
        } else if (new File(chromeBinaryPath).exists()) {
            LOG.info("providing chromedriver");
            System.setProperty("webdriver.chrome.driver", chromeBinaryPath);
            DRIVER_INSTANCE = new ChromeDriver();
        } else {
            LOG.info("providing firefox driver as phantomjs-binary was not specified or does not resolve in file system.");
            DRIVER_INSTANCE = new FirefoxDriver();
        }

        DRIVER_INSTANCE.manage().window().setSize(new Dimension(1280, 800));

        return DRIVER_INSTANCE;
    }

    public boolean hasActiveWebDriver() {
        return DRIVER_INSTANCE != null;
    }
}
