package com.coinue.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class UserAnalysisDataTest {

    private UserAnalysisData analysisData;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        analysisData = new UserAnalysisData();
    }

    @Test
    void defaultConstructor_shouldInitializeFieldsCorrectly() {
        assertNull(analysisData.getLastAnalysisDate());
        assertEquals(0.0, analysisData.getTotalExpenses(), DELTA);
        assertEquals(0.0, analysisData.getTotalIncome(), DELTA);
        assertNotNull(analysisData.getCategoryExpenses());
        assertTrue(analysisData.getCategoryExpenses().isEmpty());
        assertNotNull(analysisData.getMonthlyTrends());
        assertTrue(analysisData.getMonthlyTrends().isEmpty());
        assertNotNull(analysisData.getExpenseTags());
        assertTrue(analysisData.getExpenseTags().isEmpty());
        assertNotNull(analysisData.getBudgetUsage());
        assertTrue(analysisData.getBudgetUsage().isEmpty());
        assertEquals(LocalDate.now(), analysisData.getCreatedDate());
        assertEquals(LocalDate.now(), analysisData.getUpdatedDate());
    }

    // --- BudgetUsage Inner Class Tests ---
    @Test
    void budgetUsageDefaultConstructor() {
        UserAnalysisData.BudgetUsage usage = new UserAnalysisData.BudgetUsage();
        assertEquals(0.0, usage.getBudgetLimit(), DELTA);
        assertEquals(0.0, usage.getActualSpent(), DELTA);
        assertEquals(0.0, usage.getRemainingBudget(), DELTA);
        assertEquals(0.0, usage.getUsagePercentage(), DELTA);
    }

    @Test
    void budgetUsageParameterizedConstructor_calculatesCorrectly() {
        UserAnalysisData.BudgetUsage usage = new UserAnalysisData.BudgetUsage(100.0, 50.0);
        assertEquals(100.0, usage.getBudgetLimit(), DELTA);
        assertEquals(50.0, usage.getActualSpent(), DELTA);
        assertEquals(50.0, usage.getRemainingBudget(), DELTA);
        assertEquals(50.0, usage.getUsagePercentage(), DELTA);
    }

    @Test
    void budgetUsage_zeroBudget_calculatesPercentageAsZero() {
        UserAnalysisData.BudgetUsage usage = new UserAnalysisData.BudgetUsage(0.0, 50.0);
        assertEquals(0.0, usage.getUsagePercentage(), DELTA);
    }
    
    @Test
    void budgetUsage_zeroBudgetZeroSpent_calculatesPercentageAsZero() {
        UserAnalysisData.BudgetUsage usage = new UserAnalysisData.BudgetUsage(0.0, 0.0);
        assertEquals(0.0, usage.getUsagePercentage(), DELTA);
    }

    @Test
    void budgetUsage_gettersAndSetters() {
        UserAnalysisData.BudgetUsage usage = new UserAnalysisData.BudgetUsage();
        usage.setBudgetLimit(200.0);
        assertEquals(200.0, usage.getBudgetLimit(), DELTA);
        usage.setActualSpent(100.0);
        assertEquals(100.0, usage.getActualSpent(), DELTA);
        usage.setRemainingBudget(100.0); // Note: this setter might not be typical if calculated
        assertEquals(100.0, usage.getRemainingBudget(), DELTA);
        usage.setUsagePercentage(50.0); // Note: this setter might not be typical if calculated
        assertEquals(50.0, usage.getUsagePercentage(), DELTA);
    }

    // --- UserAnalysisData Method Tests ---
    @Test
    void addCategoryExpense_newCategory_updatesDates() {
        LocalDate initialUpdateDate = analysisData.getUpdatedDate();
        analysisData.addCategoryExpense("Food", 50.0);
        assertEquals(50.0, analysisData.getCategoryExpenses().get("Food"), DELTA);
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
        assertTrue(analysisData.getUpdatedDate().isEqual(LocalDate.now()) || analysisData.getUpdatedDate().isAfter(initialUpdateDate));
    }

    @Test
    void addCategoryExpense_existingCategory_updatesDates() {
        analysisData.addCategoryExpense("Food", 50.0);
        analysisData.addCategoryExpense("Food", 30.0);
        assertEquals(80.0, analysisData.getCategoryExpenses().get("Food"), DELTA);
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void addMonthlyTrend_newTrend_updatesDates() {
        analysisData.addMonthlyTrend("2023-01", 1000.0);
        assertEquals(1000.0, analysisData.getMonthlyTrends().get("2023-01"), DELTA);
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void addExpenseTag_newTag_updatesDates() {
        analysisData.addExpenseTag("Urgent");
        assertTrue(analysisData.getExpenseTags().contains("Urgent"));
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void addExpenseTag_duplicateTag_doesNotAdd() {
        analysisData.addExpenseTag("Work");
        int sizeBefore = analysisData.getExpenseTags().size();
        analysisData.addExpenseTag("Work");
        assertEquals(sizeBefore, analysisData.getExpenseTags().size());
    }

    @Test
    void updateBudgetUsage_newUsage_updatesDates() {
        analysisData.updateBudgetUsage("Groceries", 200.0, 150.0);
        assertTrue(analysisData.getBudgetUsage().containsKey("Groceries"));
        assertEquals(150.0, analysisData.getBudgetUsage().get("Groceries").getActualSpent(), DELTA);
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void getSavingsRate_positiveSavings() {
        analysisData.setTotalIncome(1000.0);
        analysisData.setTotalExpenses(600.0);
        assertEquals(40.0, analysisData.getSavingsRate(), DELTA);
    }

    @Test
    void getSavingsRate_zeroIncome_returnsZero() {
        analysisData.setTotalIncome(0.0);
        analysisData.setTotalExpenses(100.0);
        assertEquals(0.0, analysisData.getSavingsRate(), DELTA);
    }

    @Test
    void getSavingsRate_negativeSavings() {
        analysisData.setTotalIncome(500.0);
        analysisData.setTotalExpenses(800.0);
        assertEquals(-60.0, analysisData.getSavingsRate(), DELTA);
    }

    @Test
    void getTopExpenseCategory_withData_returnsTopCategory() {
        analysisData.addCategoryExpense("Food", 100.0);
        analysisData.addCategoryExpense("Transport", 150.0);
        analysisData.addCategoryExpense("Entertainment", 80.0);
        assertEquals("Transport", analysisData.getTopExpenseCategory());
    }

    @Test
    void getTopExpenseCategory_noData_returnsDefaultString() {
        assertEquals("无数据", analysisData.getTopExpenseCategory());
    }
    
    @Test
    void getTopExpenseCategory_oneCategory_returnsThatCategory() {
        analysisData.addCategoryExpense("Utilities", 200.0);
        assertEquals("Utilities", analysisData.getTopExpenseCategory());
    }

    // --- Getters and Setters for UserAnalysisData ---
    @Test
    void setLastAnalysisDate_works() {
        LocalDate testDate = LocalDate.of(2020, 1, 1);
        analysisData.setLastAnalysisDate(testDate);
        assertEquals(testDate, analysisData.getLastAnalysisDate());
    }

    @Test
    void setTotalExpenses_updatesDates() {
        analysisData.setTotalExpenses(500.0);
        assertEquals(500.0, analysisData.getTotalExpenses(), DELTA);
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
        assertEquals(LocalDate.now(), analysisData.getUpdatedDate());
    }

    @Test
    void setTotalIncome_updatesDates() {
        analysisData.setTotalIncome(1500.0);
        assertEquals(1500.0, analysisData.getTotalIncome(), DELTA);
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
        assertEquals(LocalDate.now(), analysisData.getUpdatedDate());
    }

    @Test
    void setCategoryExpenses_updatesDates() {
        Map<String, Double> expenses = new HashMap<>();
        expenses.put("Test", 10.0);
        analysisData.setCategoryExpenses(expenses);
        assertSame(expenses, analysisData.getCategoryExpenses());
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void setMonthlyTrends_updatesDates() {
        Map<String, Double> trends = new HashMap<>();
        trends.put("2023-02", 20.0);
        analysisData.setMonthlyTrends(trends);
        assertSame(trends, analysisData.getMonthlyTrends());
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void setExpenseTags_updatesDates() {
        List<String> tags = List.of("Tag1", "Tag2");
        analysisData.setExpenseTags(tags);
        assertSame(tags, analysisData.getExpenseTags());
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }

    @Test
    void setBudgetUsage_updatesDates() {
        Map<String, UserAnalysisData.BudgetUsage> usageMap = new HashMap<>();
        usageMap.put("Health", new UserAnalysisData.BudgetUsage(300, 100));
        analysisData.setBudgetUsage(usageMap);
        assertSame(usageMap, analysisData.getBudgetUsage());
        assertEquals(LocalDate.now(), analysisData.getLastAnalysisDate());
    }
    
    @Test
    void setCreatedDate_works(){
        LocalDate newDate = LocalDate.of(2000,1,1);
        analysisData.setCreatedDate(newDate);
        assertEquals(newDate, analysisData.getCreatedDate());
    }

    @Test
    void setUpdatedDate_works(){
        LocalDate newDate = LocalDate.of(2000,1,1);
        analysisData.setUpdatedDate(newDate);
        assertEquals(newDate, analysisData.getUpdatedDate());
    }

    @Test
    void toString_formatTest() {
        analysisData.setTotalExpenses(100.0);
        analysisData.setTotalIncome(200.0);
        analysisData.addCategoryExpense("Food", 50.0);
        analysisData.addCategoryExpense("Games", 50.0);
        LocalDate testDate = LocalDate.of(2022, 1, 1);
        analysisData.setLastAnalysisDate(testDate);
        // Savings rate = (200-100)/200 * 100 = 50.00%
        String expected = "UserAnalysisData{lastAnalysisDate=" + testDate + ", totalExpenses=100.0, totalIncome=200.0, categoryCount=2, savingsRate=50.00%}";
        assertEquals(expected, analysisData.toString());
    }
} 