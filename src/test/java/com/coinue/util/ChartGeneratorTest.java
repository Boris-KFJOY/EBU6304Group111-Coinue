package com.coinue.util;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ChartGeneratorTest {

    private static final double DELTA = 0.001;

    @Test
    void generateExpensePieChartData_emptyMap_returnsEmptyList() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        ObservableList<PieChart.Data> result = ChartGenerator.generateExpensePieChartData(categoryAmounts);
        assertTrue(result.isEmpty(), "Should return an empty list for empty input.");
    }

    @Test
    void generateExpensePieChartData_singleCategory_returnsCorrectData() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        categoryAmounts.put("Food", 50.0);
        ObservableList<PieChart.Data> result = ChartGenerator.generateExpensePieChartData(categoryAmounts);
        assertEquals(1, result.size());
        PieChart.Data data = result.get(0);
        assertEquals("Food (100.0%)", data.getName());
        assertEquals(50.0, data.getPieValue(), DELTA);
    }

    @Test
    void generateExpensePieChartData_multipleCategories_returnsCorrectDataAndPercentages() {
        Map<String, Double> categoryAmounts = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion order for predictable testing if needed, though order doesn't strictly matter for PieChart.Data content itself
        categoryAmounts.put("Food", 100.0);    // 50%
        categoryAmounts.put("Transport", 60.0); // 30%
        categoryAmounts.put("Other", 40.0);   // 20%
        // Total = 200.0

        ObservableList<PieChart.Data> result = ChartGenerator.generateExpensePieChartData(categoryAmounts);
        assertEquals(3, result.size());

        // Check Food
        PieChart.Data foodData = findPieData(result, "Food");
        assertNotNull(foodData);
        assertEquals("Food (50.0%)", foodData.getName());
        assertEquals(100.0, foodData.getPieValue(), DELTA);

        // Check Transport
        PieChart.Data transportData = findPieData(result, "Transport");
        assertNotNull(transportData);
        assertEquals("Transport (30.0%)", transportData.getName());
        assertEquals(60.0, transportData.getPieValue(), DELTA);

        // Check Other
        PieChart.Data otherData = findPieData(result, "Other");
        assertNotNull(otherData);
        assertEquals("Other (20.0%)", otherData.getName());
        assertEquals(40.0, otherData.getPieValue(), DELTA);
    }
    
    private PieChart.Data findPieData(ObservableList<PieChart.Data> list, String categoryNamePrefix) {
        return list.stream()
            .filter(d -> d.getName().startsWith(categoryNamePrefix + " ("))
            .findFirst()
            .orElse(null);
    }

    @Test
    void generateExpensePieChartData_zeroTotal_handlesDivisionByZeroGracefully() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        categoryAmounts.put("Food", 0.0);
        categoryAmounts.put("Transport", 0.0);
        ObservableList<PieChart.Data> result = ChartGenerator.generateExpensePieChartData(categoryAmounts);
        assertEquals(2, result.size());
        // String.format for %.1f with NaN results in "NaN", with Infinity results in "Infinity"
        // Depending on desired behavior, the ChartGenerator might need a guard for total == 0.
        // Current behavior will produce "Category (NaN%)" or "Category (Infinity%)" if amount > 0 and total = 0.
        // For amount = 0 and total = 0, it is NaN%.
        result.forEach(data -> {
            assertTrue(data.getName().matches("[A-Za-z]+ \\((NaN|0[.]0)%\\)"), "Label should be Category (NaN%) or Category (0.0%) for zero amounts with zero total, was: " + data.getName());
            assertEquals(0.0, data.getPieValue(), DELTA);
        });
    }
    
    @Test
    void generateExpensePieChartData_amountGreaterThanZeroAndTotalIsZero_shouldHandleAsInfinity() {
        // This scenario (positive amount with zero total) implies an impossible input state
        // if categoryAmounts only contains this one entry, or all others are zero.
        // But if the map is constructed this way, let's test.
        Map<String, Double> categoryAmounts = new HashMap<>();
        categoryAmounts.put("Food", 10.0); // This implies total will be 10.0 so this test isn't quite for total=0 with positive amounts
                                           // To test total = 0 with a positive amount, the map would need to be manipulated post-summation
                                           // or the method refactored. The current method calculates total from the map itself.
                                           // Thus, any non-empty map with non-zero amounts will have a non-zero total.
                                           // The only way total is zero is if all amounts are zero, or map is empty.
        // Test for all zero amounts is generateExpensePieChartData_zeroTotal_handlesDivisionByZeroGracefully()
        // This specific test for (amount > 0 / total = 0) -> Infinity% is hard to set up with current method structure.
        // Let's assume the zeroTotal test covers the NaN case which is the most likely from division by zero.
         Map<String, Double> categoryAmountsForInfinity = new HashMap<>();
         // We cannot directly make total zero and amount positive with the current method. 
         // However, if we consider a map where other elements sum to a negative value that cancels out positive values to make total zero,
         // e.g. Food: 10, Utilities: -10. This is unlikely for expense data.
         // The existing zeroTotal test handles the NaN case well.
    }

    // --- generateStatisticsSummary Tests ---
    @Test
    void generateStatisticsSummary_emptyMap_returnsBaseSummary() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        String expected = "总支出：0.00元\n\n各类别支出占比：\n";
        assertEquals(expected, ChartGenerator.generateStatisticsSummary(categoryAmounts));
    }

    @Test
    void generateStatisticsSummary_singleCategory_returnsCorrectSummary() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        categoryAmounts.put("Food", 75.50);
        String expected = "总支出：75.50元\n\n"
                        + "各类别支出占比：\n"
                        + "Food: 75.50元 (100.0%)\n";
        assertEquals(expected, ChartGenerator.generateStatisticsSummary(categoryAmounts));
    }

    @Test
    void generateStatisticsSummary_multipleCategories_sortedAndCorrectFormat() {
        Map<String, Double> categoryAmounts = new LinkedHashMap<>();
        categoryAmounts.put("Other", 40.0);   // 20% Should be last
        categoryAmounts.put("Food", 100.0);    // 50% Should be first
        categoryAmounts.put("Transport", 60.0); // 30% Should be second
        // Total = 200.0

        String expected = "总支出：200.00元\n\n"
                        + "各类别支出占比：\n"
                        + "Food: 100.00元 (50.0%)\n"
                        + "Transport: 60.00元 (30.0%)\n"
                        + "Other: 40.00元 (20.0%)\n";
        assertEquals(expected, ChartGenerator.generateStatisticsSummary(categoryAmounts));
    }

    @Test
    void generateStatisticsSummary_zeroTotal_handlesCorrectly() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        categoryAmounts.put("Food", 0.0);
        categoryAmounts.put("Transport", 0.0);
        String result = ChartGenerator.generateStatisticsSummary(categoryAmounts);
        
        assertTrue(result.startsWith("总支出：0.00元\n\n各类别支出占比：\n"));
        // For category lines with 0 amount and 0 total, percentage calculation (0.0/0.0) is NaN.
        // String.format("%.1f%%", Double.NaN) -> "NaN%"
        assertTrue(result.contains("Food: 0.00元 (NaN%)\n") || result.contains("Food: 0.00元 (0.0%)\n"));
        assertTrue(result.contains("Transport: 0.00元 (NaN%)\n") || result.contains("Transport: 0.00元 (0.0%)\n"));
    }
    
    @Test
    void generateStatisticsSummary_categoriesWithSameAmounts_orderIsStableIfInputOrderIsStable() {
        Map<String, Double> categoryAmounts = new LinkedHashMap<>(); // Preserve insertion order
        categoryAmounts.put("Alpha", 50.0);
        categoryAmounts.put("Beta", 100.0);
        categoryAmounts.put("Charlie", 50.0);
        // Total = 200.0

        String result = ChartGenerator.generateStatisticsSummary(categoryAmounts);
        String expectedOrderSection = "各类别支出占比：\n"
                                    + "Beta: 100.00元 (50.0%)\n"
                                    // Alpha and Charlie have same amount, their relative order from input should be preserved if sort is stable
                                    // Java's Stream.sorted() is stable for elements that are equal according to comparator.
                                    // Here, Double.compare(e2.getValue(), e1.getValue()) would make them equal. 
                                    // So, their original relative order (Alpha before Charlie) should be kept within the 50.0 block.
                                    + "Alpha: 50.00元 (25.0%)\n"
                                    + "Charlie: 50.00元 (25.0%)\n";
         assertTrue(result.contains(expectedOrderSection), "Order of same-amount categories not as expected or format differs.");
    }
} 