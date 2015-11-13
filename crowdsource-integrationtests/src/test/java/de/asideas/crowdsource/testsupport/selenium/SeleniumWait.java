package de.asideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Component
public class SeleniumWait {

    private static final Logger LOG = LoggerFactory.getLogger(SeleniumWait.class);
    private static final int TIME_OUT_IN_SECONDS = 10;

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void until(ExpectedCondition<?> isTrue) {
        new WebDriverWait(webDriverProvider.provideDriver(), TIME_OUT_IN_SECONDS).until(isTrue);
    }

    public void until(ExpectedCondition<?> isTrue, int timeoutSeconds, int pollingMillis){
        WebDriver driver = webDriverProvider.provideDriver();
        FluentWait<WebDriver> w = new FluentWait<>(driver)
                .withTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .pollingEvery(pollingMillis, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class);
        w.until(isTrue);
    }

    public void until(ExpectedCondition<?> isTrue, int timeoutSeconds, int pollingMillis, List<Class<? extends Throwable>> ignores){
        WebDriver driver = webDriverProvider.provideDriver();
        FluentWait<WebDriver> w = new FluentWait<>(driver)
                .withTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .pollingEvery(pollingMillis, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoreAll(ignores)
        ;
        w.until(isTrue);
    }
}
