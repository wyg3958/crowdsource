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

@Service
public class WebDriverUtils {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${de.axelspringer.ideas.crowdsource.test.phantomjs.binary}")
    private String phantomBinaryPath;

    @Value("${de.axelspringer.ideas.crowdsource.test.chrome.binary}")
    private String chromeBinaryPath;

    /**
     * @return {@link org.openqa.selenium.phantomjs.PhantomJSDriver}-instance if binary specified, {@link org.openqa.selenium.chrome.ChromeDriver} if binary specified (and not phantomjs), fallback to firefox
     */
    public RemoteWebDriver provideDriver() {

        System.out.println(phantomBinaryPath);

        if (new File(phantomBinaryPath).exists()) {
            log.info("providing phantomjs driver");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("phantomjs.binary.path", phantomBinaryPath);
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
    public void closeWebDriver(WebDriver driver) {

        if (driver == null) {
            return;
        }
        if (driver instanceof PhantomJSDriver) {
            driver.quit();
        }
        driver.close();
    }
}
