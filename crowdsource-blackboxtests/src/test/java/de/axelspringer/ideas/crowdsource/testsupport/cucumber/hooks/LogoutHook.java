package de.axelspringer.ideas.crowdsource.testsupport.cucumber.hooks;

import cucumber.api.java.Before;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class LogoutHook {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Before
    public void init() {
        WebDriver webDriver = webDriverProvider.provideDriver();
        // logout
        webDriver.get(urlProvider.applicationUrl() + "/#/logout");
    }

}
