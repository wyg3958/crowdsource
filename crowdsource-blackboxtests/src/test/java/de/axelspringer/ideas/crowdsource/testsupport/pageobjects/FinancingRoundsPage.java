package de.axelspringer.ideas.crowdsource.testsupport.pageobjects;

import de.axelspringer.ideas.crowdsource.model.presentation.FinancingRound;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.SeleniumWait;
import de.axelspringer.ideas.crowdsource.testsupport.selenium.WebDriverProvider;
import de.axelspringer.ideas.crowdsource.testsupport.util.UrlProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static de.axelspringer.ideas.crowdsource.testsupport.selenium.AngularJsUtils.interpolationCompletedOfElementLocated;

@Component
public class FinancingRoundsPage {

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yy HH:mm");
    @Autowired
    private WebDriverProvider webDriverProvider;
    @Autowired
    private UrlProvider urlProvider;
    @Autowired
    private SeleniumWait seleniumWait;

    public void visitPage() {
        webDriverProvider.provideDriver().get(urlProvider.applicationUrl() + "/#/financingrounds");
    }

    public void confirmErrorAlert() {
        webDriverProvider.provideDriver().switchTo().alert().accept();
    }

    public void waitForPageLoad() {
        seleniumWait.until(interpolationCompletedOfElementLocated(By.className("financingrounds")));
    }

    public List<FinancingRound> getFinancingRounds() {

        final List<FinancingRound> financingRounds = new ArrayList<>();
        webDriverProvider.provideDriver().findElementsByClassName("financinground").forEach(financingRoundElement -> {
            FinancingRound financingRound = new FinancingRound();
            // format: 16.01.15 13:35
            final String startDateString = financingRoundElement.findElement(By.className("startDate")).getText();
            financingRound.setStartDate(DateTime.parse(startDateString, formatter));
            final String endDateString = financingRoundElement.findElement(By.className("endDate")).getText();
            financingRound.setEndDate(DateTime.parse(endDateString, formatter));
            financingRound.setBudget(Integer.valueOf(financingRoundElement.findElement(By.className("budget")).getText()));
            financingRound.setActive("ja".equals(financingRoundElement.findElement(By.className("active")).getText()));
            financingRounds.add(financingRound);
        });
        return financingRounds;
    }
}
