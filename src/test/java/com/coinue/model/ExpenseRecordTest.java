package com.coinue.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ExpenseRecordTest {

    private static final double DELTA = 0.001; // For double comparisons

    @Test
    void defaultConstructor_shouldInitializeWithDefaultValues() {
        ExpenseRecord record = new ExpenseRecord();
        assertEquals(0.0, record.getAmount(), DELTA, "Default amount should be 0.0");
        assertNull(record.getCategory(), "Default category should be null");
        assertNull(record.getName(), "Default name should be null");
        assertNull(record.getDate(), "Default date should be null");
        assertNull(record.getDescription(), "Default description should be null");
        assertEquals("支出", record.getRecordType(), "Default recordType should be '支出'");
        assertEquals("CNY", record.getCurrency(), "Default currency should be 'CNY'");
    }

    @Test
    void constructorWithAmountCategoryNameDate_shouldInitializeCorrectly() {
        LocalDate testDate = LocalDate.of(2023, 1, 15);
        ExpenseRecord record = new ExpenseRecord(100.0, "Food", "Lunch", testDate);

        assertEquals(100.0, record.getAmount(), DELTA);
        assertEquals("Food", record.getCategory());
        assertEquals("Lunch", record.getName());
        assertEquals(testDate, record.getDate());
        assertNull(record.getDescription(), "Description should be null when not provided.");
        assertEquals("支出", record.getRecordType(), "recordType should default to '支出'");
        assertEquals("CNY", record.getCurrency(), "currency should default to 'CNY'");
    }

    @Test
    void constructorWithAmountCategoryNameDateDescription_shouldInitializeCorrectly() {
        LocalDate testDate = LocalDate.of(2023, 5, 20);
        ExpenseRecord record = new ExpenseRecord(50.75, "Transport", "Bus ticket", testDate, "Monthly pass");

        assertEquals(50.75, record.getAmount(), DELTA);
        assertEquals("Transport", record.getCategory());
        assertEquals("Bus ticket", record.getName());
        assertEquals(testDate, record.getDate());
        assertEquals("Monthly pass", record.getDescription());
        assertEquals("支出", record.getRecordType(), "recordType should default to '支出'");
        assertEquals("CNY", record.getCurrency(), "currency should default to 'CNY'");
    }

    @Test
    void fullConstructor_shouldInitializeAllFieldsCorrectly() {
        LocalDate testDate = LocalDate.of(2024, 2, 10);
        ExpenseRecord record = new ExpenseRecord(200.0, "Utilities", "Electricity Bill", testDate, "January bill", "收入", "USD");

        assertEquals(200.0, record.getAmount(), DELTA);
        assertEquals("Utilities", record.getCategory());
        assertEquals("Electricity Bill", record.getName());
        assertEquals(testDate, record.getDate());
        assertEquals("January bill", record.getDescription());
        assertEquals("收入", record.getRecordType());
        assertEquals("USD", record.getCurrency());
    }

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        ExpenseRecord record = new ExpenseRecord();
        LocalDate testDate = LocalDate.now();

        record.setAmount(123.45);
        assertEquals(123.45, record.getAmount(), DELTA);

        record.setCategory("Entertainment");
        assertEquals("Entertainment", record.getCategory());

        record.setName("Movie Ticket");
        assertEquals("Movie Ticket", record.getName());

        record.setDate(testDate);
        assertEquals(testDate, record.getDate());

        record.setDescription("Evening show");
        assertEquals("Evening show", record.getDescription());

        record.setRecordType("收入");
        assertEquals("收入", record.getRecordType());

        record.setCurrency("EUR");
        assertEquals("EUR", record.getCurrency());
    }

    @Test
    void toString_shouldReturnCorrectFormat() {
        LocalDate testDate = LocalDate.of(2023, 8, 25);
        ExpenseRecord record = new ExpenseRecord(99.99, "Shopping", "New Shoes", testDate, "Running shoes", "支出", "USD");
        String expected = String.format("ExpenseRecord{amount=%.2f, currency='%s', category='%s', name='%s', date=%s, description='%s', recordType='%s'}",
                                        99.99, "USD", "Shopping", "New Shoes", testDate.toString(), "Running shoes", "支出");
        assertEquals(expected, record.toString());
    }

    @Test
    void toString_withNullFields_shouldHandleGracefully() {
        ExpenseRecord record = new ExpenseRecord();
        // Set some values to non-null to make the output more predictable for default constructor scenario
        record.setAmount(0.0); // Explicitly set to test formatting
        // Other fields like category, name, date, description are null by default constructor
        // recordType and currency have defaults
        String expected = "ExpenseRecord{amount=0.00, currency='CNY', category='null', name='null', date=null, description='null', recordType='支出'}";
        assertEquals(expected, record.toString());
    }
} 