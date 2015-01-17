package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static de.axelspringer.ideas.crowdsource.testsupport.selenium.AngularJsUtils.interpolationCompletedOfElementLocated;

@Component
public class FinancingRoundsPage {

    @Autowired
    private WebDriverProvider webDriverProvider;

    @Autowired
    private UrlProvider urlProvider;

    @Autowired
    private SeleniumWait seleniumWait;

    public void open() {
        webDriverProvider.provideDriver().get(urlProvider.applicationUrl() + "/#/financingrounds");
    }

    public void confirmErrorAlert() {
        seleniumWait.waitForAlert().accept();
    }

    public void waitForPageLoad() {
        seleniumWait.until(interpolationCompletedOfElementLocated(By.className("financingrounds")));
    }

    public List<FinancingRound> getFinancingRounds() {

        final List<FinancingRound> financingRounds = new ArrayList<>();
        webDriverProvider.provideDriver().findElementsByClassName("financinground").forEach(financingRoundElement -> {
            financingRounds.add(financingRound(financingRoundElement));
        });
        return financingRounds;
    }

    public void cancelFinancingRound(FinancingRound financingRound) {

        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        webDriver.findElementsByClassName("financinground").forEach(financingRoundElement -> {
            if (financingRound.getId().equals(financingRoundElement.getAttribute("fr_id"))) {
                financingRoundElement.findElement(By.className("cancel")).click();
                // confirm that we want to cancel
                seleniumWait.waitForAlert().accept();
                // click-away the info dialog
                seleniumWait.waitForAlert().accept();
            }
        });
    }

    public void startFinancingRound(DateTime endDate, int budget) {

        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();

        final WebElement newRoundBudget = webDriver.findElement(By.className("newround-budget"));
        newRoundBudget.clear();
        newRoundBudget.sendKeys(Integer.toString(budget));

        final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        webDriver.executeScript("$('.newround-enddate').val('" + endDate.toString(formatter) + "')");
        webDriver.executeScript("$('.newround-enddate').trigger('input')");

        webDriver.findElement(By.className("newround-start")).click();
        seleniumWait.waitForAlert().accept();
    }

    public FinancingRound findFinancingRound(DateTime endDate, int budget) {

        for (WebElement financingRoundElement : webDriverProvider.provideDriver().findElements(By.className("financinground"))) {
            FinancingRound financingRound = financingRound(financingRoundElement);
            if (sameDate(financingRound.getEndDate(), endDate) && financingRound.getBudget().equals(budget)) {
                return financingRound;
            }
        }
        return null;
    }

    public boolean canStartFinancingRound() {

        final RemoteWebDriver webDriver = webDriverProvider.provideDriver();
        return webDriver.findElements(By.className("newround-start")).size() > 0;
    }

    private boolean sameDate(DateTime a, DateTime b) {
        return a.getYear() == b.getYear() && a.getMonthOfYear() == b.getMonthOfYear() && a.getDayOfMonth() == b.getDayOfMonth();
    }

    private FinancingRound financingRound(WebElement financingRoundElement) {

        // format: 16.01.15 13:35
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yy HH:mm");

        FinancingRound financingRound = new FinancingRound();
        final String startDateString = financingRoundElement.findElement(By.className("startdate")).getText();
        financingRound.setStartDate(DateTime.parse(startDateString, formatter));
        final String endDateString = financingRoundElement.findElement(By.className("enddate")).getText();
        financingRound.setEndDate(DateTime.parse(endDateString, formatter));
        financingRound.setBudget(Integer.valueOf(financingRoundElement.findElement(By.className("budget")).getText()));
        financingRound.setActive("ja".equals(financingRoundElement.findElement(By.className("active")).getText()));
        financingRound.setId(financingRoundElement.getAttribute("fr_id"));
        return financingRound;
    }
}
