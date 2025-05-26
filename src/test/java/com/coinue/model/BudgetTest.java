package com.coinue.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {

    private static final double DELTA = 0.001; // For double comparisons

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        Budget budget = new Budget("Groceries", 200.0, "USD");
        assertEquals("Groceries", budget.getCategory());
        assertEquals(200.0, budget.getAmount(), DELTA);
        assertEquals("USD", budget.getCurrency());
        assertEquals(0.0, budget.getSpentAmount(), DELTA, "Initial spent amount should be 0.");
    }

    @Test
    void addExpense_shouldIncreaseSpentAmount() {
        Budget budget = new Budget("Entertainment", 100.0, "EUR");
        budget.addExpense(25.5);
        assertEquals(25.5, budget.getSpentAmount(), DELTA);

        budget.addExpense(10.0);
        assertEquals(35.5, budget.getSpentAmount(), DELTA);
    }

    @Test
    void addExpense_withZero_shouldNotChangeSpentAmount() {
        Budget budget = new Budget("Transport", 50.0, "GBP");
        budget.addExpense(0.0);
        assertEquals(0.0, budget.getSpentAmount(), DELTA);
    }

    @Test
    void getUsagePercentage_withNoExpenses_shouldReturnZero() {
        Budget budget = new Budget("Utilities", 150.0, "USD");
        assertEquals(0.0, budget.getUsagePercentage(), DELTA);
    }

    @Test
    void getUsagePercentage_withSomeExpenses_shouldCalculateCorrectly() {
        Budget budget = new Budget("Dining", 100.0, "USD");
        budget.addExpense(50.0);
        assertEquals(50.0, budget.getUsagePercentage(), DELTA);
    }

    @Test
    void getUsagePercentage_withFullExpenses_shouldReturnHundred() {
        Budget budget = new Budget("Rent", 500.0, "EUR");
        budget.addExpense(500.0);
        assertEquals(100.0, budget.getUsagePercentage(), DELTA);
    }

    @Test
    void getUsagePercentage_withExpensesExceedingBudget_shouldReturnOverHundred() {
        Budget budget = new Budget("Shopping", 80.0, "GBP");
        budget.addExpense(100.0); // Spent 100 on an 80 budget
        assertEquals(125.0, budget.getUsagePercentage(), DELTA);
    }

    @Test
    void getUsagePercentage_withZeroBudgetAndZeroSpent_shouldReturnNaN() {
        // (0.0 / 0.0) results in NaN
        Budget budget = new Budget("ZeroBudget", 0.0, "USD");
        assertTrue(Double.isNaN(budget.getUsagePercentage()), "0 spent / 0 budget should be NaN.");
    }

    @Test
    void getUsagePercentage_withZeroBudgetAndSomeSpent_shouldReturnInfinity() {
        // (positive / 0.0) results in Infinity
        Budget budget = new Budget("ZeroBudgetPositiveSpent", 0.0, "USD");
        budget.addExpense(10.0);
        assertTrue(Double.isInfinite(budget.getUsagePercentage()), "Positive spent / 0 budget should be Infinity.");
        assertEquals(Double.POSITIVE_INFINITY, budget.getUsagePercentage());
    }
    
    @Test
    void getUsagePercentage_withNegativeBudget_shouldCalculateWithAbsoluteOrAsIs() {
        // Current implementation will use the negative budget as is.
        // If amount is -100 and spent is 10, percentage is (10 / -100) * 100 = -10%
        Budget budget = new Budget("NegativeBudget", -100.0, "USD");
        budget.addExpense(10.0);
        assertEquals(-10.0, budget.getUsagePercentage(), DELTA);
    }
} 