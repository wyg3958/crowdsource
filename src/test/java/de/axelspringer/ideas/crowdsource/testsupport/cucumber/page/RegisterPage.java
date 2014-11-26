package de.axelspringer.ideas.crowdsource.testsupport.cucumber.page;

import de.axelspringer.ideas.crowdsource.testsupport.util.WebDriverProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class RegisterPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    public void open() {
        //webDriverProvider.provideDriver().get();
    }
}
