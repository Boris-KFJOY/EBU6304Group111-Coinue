package com.coinue.util;

import com.coinue.model.Budget;
import com.coinue.model.Category;
import com.coinue.model.ExpenseRecord;
import com.coinue.model.PaymentReminder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataManagerTest {

    private static final String DATA_DIR_PATH = "data";
    private static final Path EXPENSE_FILE_PATH = Paths.get(DATA_DIR_PATH, "expense.json");
    private static final Path BUDGET_FILE_PATH = Paths.get(DATA_DIR_PATH, "budget.json");
    private static final Path REMINDER_FILE_PATH = Paths.get(DATA_DIR_PATH, "reminder.json");
    private static final Path DATA_DIR = Paths.get(DATA_DIR_PATH);


    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(DATA_DIR);
        Files.deleteIfExists(EXPENSE_FILE_PATH);
        Files.deleteIfExists(BUDGET_FILE_PATH);
        Files.deleteIfExists(REMINDER_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(EXPENSE_FILE_PATH);
        Files.deleteIfExists(BUDGET_FILE_PATH);
        Files.deleteIfExists(REMINDER_FILE_PATH);
        // Attempt to delete data directory if empty, but don't fail test if it can't
        try {
            if (Files.isDirectory(DATA_DIR) && Files.list(DATA_DIR).findAny().isEmpty()) {
                Files.deleteIfExists(DATA_DIR);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not clean up data directory: " + DATA_DIR_PATH + " - " + e.getMessage());
        }
    }

    // --- ExpenseRecord Tests ---

    @Test
    void saveAndLoadExpenseRecords_emptyList() {
        List<ExpenseRecord> emptyList = new ArrayList<>();
        DataManager.saveExpenseRecords(emptyList);
        List<ExpenseRecord> loadedList = DataManager.loadExpenseRecords();
        assertNotNull(loadedList, "Loaded list should not be null.");
        assertTrue(loadedList.isEmpty(), "Loaded list should be empty.");
    }

    @Test
    void saveAndLoadExpenseRecords_multipleRecords() {
        ExpenseRecord record1 = new ExpenseRecord(50.0, "Groceries", "Weekly shopping", LocalDate.now(), "Food for the week", "Groceries", "USD");
        ExpenseRecord record2 = new ExpenseRecord(25.0, "Transport", "Bus fare", LocalDate.now().minusDays(1), "Commute to work", "Transport", "USD");
        List<ExpenseRecord> records = Arrays.asList(record1, record2);

        DataManager.saveExpenseRecords(records);
        List<ExpenseRecord> loadedRecords = DataManager.loadExpenseRecords();

        assertNotNull(loadedRecords);
        assertEquals(2, loadedRecords.size());

        ExpenseRecord loadedRecord1 = loadedRecords.stream().filter(r -> r.getName().equals("Weekly shopping")).findFirst().orElse(null);
        ExpenseRecord loadedRecord2 = loadedRecords.stream().filter(r -> r.getName().equals("Bus fare")).findFirst().orElse(null);
        
        assertNotNull(loadedRecord1, "Record1 not found after loading");
        assertEquals(record1.getAmount(), loadedRecord1.getAmount());
        assertEquals(record1.getDate(), loadedRecord1.getDate());
        assertEquals(record1.getCategory(), loadedRecord1.getCategory());
        assertEquals(record1.getDescription(), loadedRecord1.getDescription());


        assertNotNull(loadedRecord2, "Record2 not found after loading");
        assertEquals(record2.getAmount(), loadedRecord2.getAmount());
        assertEquals(record2.getDate(), loadedRecord2.getDate());
        assertEquals(record2.getCategory(), loadedRecord2.getCategory());
        assertEquals(record2.getDescription(), loadedRecord2.getDescription());
    }

    @Test
    void loadExpenseRecords_fileNotExists() {
        List<ExpenseRecord> loadedRecords = DataManager.loadExpenseRecords();
        assertNotNull(loadedRecords);
        assertTrue(loadedRecords.isEmpty());
    }

    // --- Budget Tests ---

    @Test
    void saveAndLoadBudgets_emptyList() {
        List<Budget> emptyList = new ArrayList<>();
        DataManager.saveBudgets(emptyList);
        List<Budget> loadedList = DataManager.loadBudgets();
        assertNotNull(loadedList);
        assertTrue(loadedList.isEmpty());
    }

    @Test
    void saveAndLoadBudgets_multipleBudgets() {
        Category catFood = new Category("Food", 0.0);
        Category catEntertainment = new Category("Entertainment", 0.0);
        Budget budget1 = new Budget(catFood.getName(), 500.0, "USD");
        Budget budget2 = new Budget(catEntertainment.getName(), 150.0, "USD");
        List<Budget> budgets = Arrays.asList(budget1, budget2);

        DataManager.saveBudgets(budgets);
        List<Budget> loadedBudgets = DataManager.loadBudgets();

        assertNotNull(loadedBudgets);
        assertEquals(2, loadedBudgets.size());
        
        Budget loadedBudget1 = loadedBudgets.stream().filter(b -> b.getCategory().equals("Food")).findFirst().orElse(null);
        Budget loadedBudget2 = loadedBudgets.stream().filter(b -> b.getCategory().equals("Entertainment")).findFirst().orElse(null);

        assertNotNull(loadedBudget1, "Budget1 not found");
        assertEquals(budget1.getAmount(), loadedBudget1.getAmount());
        assertEquals(budget1.getCategory(), loadedBudget1.getCategory());
        assertEquals(budget1.getCurrency(), loadedBudget1.getCurrency());


        assertNotNull(loadedBudget2, "Budget2 not found");
        assertEquals(budget2.getAmount(), loadedBudget2.getAmount());
        assertEquals(budget2.getCategory(), loadedBudget2.getCategory());
        assertEquals(budget2.getCurrency(), loadedBudget2.getCurrency());
    }

    @Test
    void loadBudgets_fileNotExists() {
        List<Budget> loadedBudgets = DataManager.loadBudgets();
        assertNotNull(loadedBudgets);
        assertTrue(loadedBudgets.isEmpty());
    }

    // --- PaymentReminder Tests ---

    @Test
    void saveAndLoadReminders_emptyList() {
        List<PaymentReminder> emptyList = new ArrayList<>();
        DataManager.saveReminders(emptyList);
        List<PaymentReminder> loadedList = DataManager.loadReminders();
        assertNotNull(loadedList);
        assertTrue(loadedList.isEmpty());
    }

    @Test
    void saveAndLoadReminders_multipleReminders() {
        PaymentReminder reminder1 = new PaymentReminder("Credit Card", 100.0, LocalDate.now().plusDays(10), "/icons/cc.png");
        PaymentReminder reminder2 = new PaymentReminder("Rent", 1200.0, LocalDate.now().plusDays(5), "/icons/rent.png");
        List<PaymentReminder> reminders = Arrays.asList(reminder1, reminder2);

        DataManager.saveReminders(reminders);
        List<PaymentReminder> loadedReminders = DataManager.loadReminders();

        assertNotNull(loadedReminders);
        assertEquals(2, loadedReminders.size());

        PaymentReminder loadedReminder1 = loadedReminders.stream().filter(r -> r.getPlatform().equals("Credit Card")).findFirst().orElse(null);
        PaymentReminder loadedReminder2 = loadedReminders.stream().filter(r -> r.getPlatform().equals("Rent")).findFirst().orElse(null);

        assertNotNull(loadedReminder1, "Reminder1 not found");
        assertEquals(reminder1.getAmount(), loadedReminder1.getAmount());
        assertEquals(reminder1.getDueDate(), loadedReminder1.getDueDate());
        assertEquals(reminder1.getIconPath(), loadedReminder1.getIconPath());

        assertNotNull(loadedReminder2, "Reminder2 not found");
        assertEquals(reminder2.getAmount(), loadedReminder2.getAmount());
        assertEquals(reminder2.getDueDate(), loadedReminder2.getDueDate());
        assertEquals(reminder2.getIconPath(), loadedReminder2.getIconPath());
    }

    @Test
    void loadReminders_fileNotExists() {
        List<PaymentReminder> loadedReminders = DataManager.loadReminders();
        assertNotNull(loadedReminders);
        assertTrue(loadedReminders.isEmpty());
    }

    @Test
    void loadReminders_emptyFile() throws IOException {
        Files.createFile(REMINDER_FILE_PATH);
        assertTrue(Files.exists(REMINDER_FILE_PATH), "Empty reminder file should be created.");
        assertEquals(0, Files.size(REMINDER_FILE_PATH), "Reminder file should be empty.");

        List<PaymentReminder> loadedReminders = DataManager.loadReminders();
        assertNotNull(loadedReminders);
        assertTrue(loadedReminders.isEmpty(), "Loading from an explicitly empty file should result in an empty list.");
    }
    
    @Test
    void loadReminders_fileWithEmptyJsonArray() throws IOException {
        Files.writeString(REMINDER_FILE_PATH, "[]");
        List<PaymentReminder> loadedReminders = DataManager.loadReminders();
        assertNotNull(loadedReminders);
        assertTrue(loadedReminders.isEmpty());
    }

    @Test
    void loadReminders_fileWithOnlyWhitespace() throws IOException {
        Files.writeString(REMINDER_FILE_PATH, "   "); 
        List<PaymentReminder> loadedReminders = DataManager.loadReminders();
        assertNotNull(loadedReminders);
        assertTrue(loadedReminders.isEmpty(), "Loading from a file with only whitespace should result in an empty list.");
    }
} 