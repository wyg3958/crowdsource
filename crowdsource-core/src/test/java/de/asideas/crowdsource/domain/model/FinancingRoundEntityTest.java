package de.asideas.crowdsource.domain.model;

import de.asideas.crowdsource.domain.presentation.FinancingRound;
import org.exparity.hamcrest.date.DateMatchers;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class FinancingRoundEntityTest {

    @Test
    public void budgetPerUserClearRounding() {
        assertThat(newFinancingRound(100, 10).getBudgetPerUser(), is(10));
    }

    @Test
    public void budgetPerUserNonClearRounding() {
        assertThat(newFinancingRound(109, 10).getBudgetPerUser(), is(10));
    }

    @Test
    public void newFinancingRoundCorrectlyInitialized() {
        final int countUsers = 10;
        final FinancingRound creationCmd = new FinancingRound();
        creationCmd.setBudget(1000);
        creationCmd.setEndDate(new DateTime(0L));
        creationCmd.setId("test_id");

        final FinancingRoundEntity res = FinancingRoundEntity.newFinancingRound(creationCmd, countUsers);

        assertThat(res.getId(), is(nullValue()));
        assertThat(res.getBudgetPerUser(), is(100));
        assertThat(res.getBudget(), is(creationCmd.getBudget()));
        assertThat(res.getEndDate(), is(creationCmd.getEndDate()));
        assertThat(res.getStartDate().toDate(), DateMatchers.sameSecond(new Date()));
        assertThat(res.getUserCount(), is(countUsers));
    }

    @Test
    public void active_shouldReturnTrueWhenEndDateInFuture() throws Exception {
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().plusDays(1));
        round.setStartDate(new DateTime().minusDays(1));

        assertThat(round.active(), is(true));
    }

    @Test
    public void active_shouldReturnFalseWhenStartDateAndEndDateInFuture() throws Exception {
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().plusDays(2));
        round.setStartDate(new DateTime().plusDays(1));

        assertThat(round.active(), is(false));
    }

    @Test
    public void active_shouldReturnFalseWhenEndDateInPast() throws Exception {
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().minusDays(1));
        round.setStartDate(new DateTime().minusDays(2));

        assertThat(round.active(), is(false));
    }

    @Test
    public void active_shouldReturnFalseWhenStartDateInFuture() throws Exception {
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().plusDays(2));
        round.setStartDate(new DateTime().plusDays(1));

        assertThat(round.active(), is(false));
    }

    @Test
    public void terminated_shouldReturnTrueWhenEndDateInPast(){
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().minusDays(1));
        round.setStartDate(new DateTime().minusDays(2));

        assertThat(round.terminated(), is(true));
    }

    @Test
    public void terminated_shouldReturnFalseWhenStillActive(){
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().plusDays(1));
        round.setStartDate(new DateTime().minusDays(1));

        assertThat(round.terminated(), is(false));
    }

    @Test
    public void terminated_shouldReturnFalseWhenNotYetStarted(){
        FinancingRoundEntity round = newFinancingRound(100, 10, new DateTime().plusDays(2));
        round.setStartDate(new DateTime().plusDays(1));

        assertThat(round.terminated(), is(false));
    }

    @Test
    public void terminationPostProcessingRequiredNow_shouldReturnTrueWhenTerminated() throws Exception {
        final FinancingRoundEntity financingRound = newFinancingRound(100, 10, new DateTime().minusHours(1));

        assertThat(financingRound.terminationPostProcessingRequiredNow(), is(true));
    }

    @Test
    public void terminationPostProcessingRequiredNow_shouldReturnFalseWhenTerminatedButAlreadyProcessed() throws Exception {
        final FinancingRoundEntity financingRound = newFinancingRound(100, 10, new DateTime().minusHours(1));
        financingRound.setTerminationPostProcessingDone(true);

        assertThat(financingRound.terminationPostProcessingRequiredNow(), is(false));
    }

    @Test
    public void terminationPostProcessingRequiredNow_shouldReturnFalseWhenActiveButAlreadyProcessed() throws Exception {
        final FinancingRoundEntity financingRound = newFinancingRound(100, 10, new DateTime().plusDays(1));
        financingRound.setTerminationPostProcessingDone(true);

        assertThat(financingRound.terminationPostProcessingRequiredNow(), is(false));
    }

    @Test
    public void initBudgetRemainingAfterRound() throws Exception{
        final FinancingRoundEntity financingRound = newFinancingRound(1000, 10, new DateTime().minusHours(1));

        financingRound.initBudgetRemainingAfterRound(17);
        assertThat(financingRound.getBudgetRemainingAfterRound(), is(983));
    }

    @Test(expected = IllegalStateException.class)
    public void initBudgetRemainingAfterRound_shouldThrowExceptionOnNotTerminatedRound() throws Exception{
        final FinancingRoundEntity financingRound = newFinancingRound(1000, 10, new DateTime().plusDays(2));
        financingRound.setStartDate(new DateTime().plusDays(1));

        financingRound.initBudgetRemainingAfterRound(17);
    }


    private FinancingRoundEntity newFinancingRound(int budget, int countUsers ) {
        return newFinancingRound(budget, countUsers, new DateTime().plusDays(1));
    }

    private FinancingRoundEntity newFinancingRound(int budget, int countUsers, DateTime endDate) {
        final FinancingRound cmd = new FinancingRound();
        cmd.setBudget(budget);
        cmd.setEndDate(endDate);
        return FinancingRoundEntity.newFinancingRound(cmd, countUsers);
    }
}