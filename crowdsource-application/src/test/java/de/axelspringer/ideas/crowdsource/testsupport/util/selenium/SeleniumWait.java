package de.axelspringer.ideas.crowdsource.testsupport.util.selenium;

import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeleniumWait {

    private static final int TIME_OUT_IN_SECONDS = 15;

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void until(Predicate<WebDriver> isTrue) {
        log.debug("Waitung until predicate becomes true: {}", isTrue);
        new WebDriverWait(webDriverProvider.provideDriver(), TIME_OUT_IN_SECONDS).until(isTrue);
    }

    public void until(ExpectedCondition<WebElement> isTrue) {
        log.debug("Waitung until expected condition becomes true: {}", isTrue);
        new WebDriverWait(webDriverProvider.provideDriver(), TIME_OUT_IN_SECONDS).until(isTrue);
    }
}
