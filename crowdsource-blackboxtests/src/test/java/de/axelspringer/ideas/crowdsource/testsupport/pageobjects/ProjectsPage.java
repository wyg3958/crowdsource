package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Component
public class ProjectsPage {

    @Autowired
    private SeleniumWait wait;

    public void waitForProjectTileWithTitleToBePresent(String text) {
        final By tile = By.xpath("//h1[.='" + text + "']");
        wait.until(presenceOfElementLocated(tile));
    }

    public void waitForProjectTileWithShortDescriptionToBePresent(String text) {
        final By tile = By.xpath("//p[.='" + text + "']");
        wait.until(presenceOfElementLocated(tile));
    }
}
