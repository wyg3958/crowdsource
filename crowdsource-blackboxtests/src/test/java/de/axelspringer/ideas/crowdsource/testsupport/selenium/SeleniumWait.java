package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.security.Credentials;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeleniumWait {

    private static final int TIME_OUT_IN_SECONDS = 30;

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void until(ExpectedCondition<?> isTrue) {
        new WebDriverWait(webDriverProvider.provideDriver(), TIME_OUT_IN_SECONDS).until(isTrue);
    }

    public Alert waitForAlert() {

        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        if (webDriver instanceof PhantomJSDriver) {
            return new Alert() {
                @Override
                public void dismiss() {

                }

                @Override
                public void accept() {

                }

                @Override
                public String getText() {
                    return null;
                }

                @Override
                public void sendKeys(String keysToSend) {

                }

                @Override
                public void authenticateUsing(Credentials credentials) {

                }
            };
        }
        int i = 0;
        while (true) {
            try {
                return webDriver.switchTo().alert();
            } catch (NoAlertPresentException e) {
                if (i == 10) {
                    throw e;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e);
                }
                i++;
            }
        }
    }
}
