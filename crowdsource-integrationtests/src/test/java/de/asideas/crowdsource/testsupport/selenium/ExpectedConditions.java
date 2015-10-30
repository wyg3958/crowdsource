package de.asideas.crowdsource.testsupport.selenium;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.springframework.web.util.UriTemplate;

public class ExpectedConditions {

    public static Predicate<WebDriver> urlEndsWith(String urlPart) {
        return webDriver -> webDriver.getCurrentUrl().endsWith(urlPart);
    }

    public static Predicate<WebDriver> uriTemplateMatches(String s) {
        return webDriver -> new UriTemplate(s).matches(webDriver.getCurrentUrl());
    }
}