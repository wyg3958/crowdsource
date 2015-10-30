package de.asideas.crowdsource.testsupport.pageobjects.project;

import de.asideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Component
public class AddProjectConfirmationView {

    @FindBy(css = ".project-form-success .link-to-project")
    private WebElement linkToProject;

    @Autowired
    private SeleniumWait wait;

    public void waitForPageLoad() {
        wait.until(visibilityOf(linkToProject));
    }

    public void clickLinkToProject() {
        linkToProject.click();
    }
}
