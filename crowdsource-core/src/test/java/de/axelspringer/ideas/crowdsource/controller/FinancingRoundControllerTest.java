package de.axelspringer.ideas.crowdsource.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FinancingRoundControllerTest {

    private FinancingRoundController financingRoundController = new FinancingRoundController();

    @Test
    public void testBudgetPerUserClearRounding() {
        assertEquals(10, financingRoundController.budgetPerUser(100, 10));
    }

    @Test
    public void testBudgetPerUserNonClearRounding() {
        assertEquals(10, financingRoundController.budgetPerUser(109, 10));
    }
}