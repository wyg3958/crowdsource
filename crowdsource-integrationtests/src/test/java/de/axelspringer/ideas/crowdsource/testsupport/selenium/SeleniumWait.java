package de.axelspringer.ideas.crowdsource.testsupport.selenium;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeleniumWait {

    private static final Logger log = LoggerFactory.getLogger(SeleniumWait.class);
    private static final int TIME_OUT_IN_SECONDS = 10;

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void until(ExpectedCondition<?> isTrue) {
        new WebDriverWait(webDriverProvider.provideDriver(), TIME_OUT_IN_SECONDS).until(isTrue);
    }
}
