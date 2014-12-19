package de.axelspringer.ideas.crowdsource.testsupport.cucumber.hooks;

import cucumber.api.java.Before;
import de.axelspringer.ideas.crowdsource.testsupport.pageobjects.LogoutPage;
import org.springframework.beans.factory.annotation.Autowired;

public class LogoutHook {

    @Autowired
    private LogoutPage logoutPage;

    @Before
    public void init() {
        logoutPage.open();
        logoutPage.waitForPageLoad();
    }

}
