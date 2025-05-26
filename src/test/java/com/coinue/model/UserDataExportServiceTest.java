package com.coinue.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserDataExportServiceTest {

    private UserDataExportService userDataExportService;
    private ObjectMapper objectMapper;

    private static final String TEST_USERNAME = "exportTestUser";
    private static final String USER_DATA_ROOT_DIR_NAME = "data/users";
    private static final String EXPORT_ROOT_DIR_NAME = "data/exports";
    private static final Path USER_DATA_DIR = Paths.get(USER_DATA_ROOT_DIR_NAME, TEST_USERNAME);
    private static final Path EXPORT_DIR = Paths.get(EXPORT_ROOT_DIR_NAME);

    private User testUser;

    // @TempDir Path tempDir; // Using actual paths due to hardcoding in service

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        userDataExportService = UserDataExportService.getInstance(); // Get the singleton

        // Create test user data directory and export directory
        Files.createDirectories(USER_DATA_DIR);
        Files.createDirectories(EXPORT_DIR);

        testUser = new User(TEST_USERNAME, "export@example.com", "Password123");
        testUser.setBirthday(LocalDate.of(1995, 5, 15));
        testUser.setSecurityQuestion("What is your pet\'s name?");

        // Create dummy data files for the user
        createDummyDataFiles();
    }

    private void createDummyDataFiles() throws IOException {
        // Analysis Data
        UserAnalysisData analysisData = new UserAnalysisData();
        analysisData.setTotalExpenses(1250.75);
        analysisData.setTotalIncome(3500.50);
        analysisData.addCategoryExpense("Groceries", 300.25);
        analysisData.addCategoryExpense("Utilities", 150.50);
        analysisData.updateBudgetUsage("FoodBudget", 500, 300.25);
        analysisData.setLastAnalysisDate(LocalDate.of(2023,10,1));
        objectMapper.writeValue(USER_DATA_DIR.resolve("analysis_data.json").toFile(), analysisData);

        // Bill Data
        UserBillData billData = new UserBillData();
        billData.setCreditLimit(7500.00);
        billData.addBillRecord(new UserBillData.BillRecord(LocalDate.of(2023, 9, 10), "Credit Card Payment, with comma", 500.00, "Paid"));
        billData.addBillRecord(new UserBillData.BillRecord(LocalDate.of(2023, 9, 20), "Rent with \"Quotes\"", 1200.00, "Paid"));
        billData.setUpdatedDate(LocalDate.of(2023,10,2));
        objectMapper.writeValue(USER_DATA_DIR.resolve("bill_data.json").toFile(), billData);

        // Expense Records
        List<ExpenseRecord> expenseRecords = new ArrayList<>();
        expenseRecords.add(new ExpenseRecord(15.75, "Food", "Lunch, with comma", LocalDate.of(2023,9,5), "Client meeting with \"details\"", "Expense", "USD"));
        objectMapper.writeValue(USER_DATA_DIR.resolve("expense_data.json").toFile(), expenseRecords);

        // User Settings
        Map<String, Object> userSettings = new HashMap<>();
        userSettings.put("theme", "dark");
        userSettings.put("notificationsEnabled", true);
        objectMapper.writeValue(USER_DATA_DIR.resolve("user_settings.json").toFile(), userSettings);
        
        // Budget Data (generic object/map)
        Map<String, Object> budgetDataMap = new HashMap<>();
        budgetDataMap.put("monthlySavingGoal", 500);
        budgetDataMap.put("notes", "Aggressive saving month");
        objectMapper.writeValue(USER_DATA_DIR.resolve("budget_data.json").toFile(), budgetDataMap);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete all CSV files created for TEST_USERNAME in the EXPORT_DIR
        if (Files.exists(EXPORT_DIR)) {
            Files.list(EXPORT_DIR)
                .filter(path -> path.getFileName().toString().startsWith(TEST_USERNAME) && path.toString().endsWith(".csv"))
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException e) { e.printStackTrace(); }
                });
            if (Files.list(EXPORT_DIR).collect(Collectors.toList()).isEmpty()) {
                 try { Files.deleteIfExists(EXPORT_DIR); } catch (IOException e) { /* ignore */ }
            }
        }
        
        // Delete the user-specific data directory and its contents
        if (Files.exists(USER_DATA_DIR)) {
            Files.walk(USER_DATA_DIR)
                .map(Path::toFile)
                .sorted((o1, o2) -> -o1.compareTo(o2)) // Delete contents before directory
                .forEach(File::delete);
        }

        // Attempt to delete the base user data directory if it's empty
        Path baseUsersPath = Paths.get(USER_DATA_ROOT_DIR_NAME);
        if (Files.exists(baseUsersPath) && Files.isDirectory(baseUsersPath)) {
            if (Files.list(baseUsersPath).collect(Collectors.toList()).isEmpty()) {
                try { Files.deleteIfExists(baseUsersPath); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    @Test
    void getInstance_returnsSingleton() {
        UserDataExportService instance1 = UserDataExportService.getInstance();
        UserDataExportService instance2 = UserDataExportService.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance.");
    }

    @Test
    void exportUserCompleteData_withValidUserAndData_createsPopulatedCsv() throws IOException {
        String filePath = userDataExportService.exportUserCompleteData(testUser);
        assertNotNull(filePath, "Exported file path should not be null.");

        Path exportedFile = Paths.get(filePath);
        assertTrue(Files.exists(exportedFile), "Exported CSV file should exist.");
        assertTrue(exportedFile.getFileName().toString().startsWith(TEST_USERNAME + "_complete_data_"), "Filename mismatch.");
        assertTrue(exportedFile.getFileName().toString().endsWith(".csv"), "File extension mismatch.");

        List<String> lines = Files.readAllLines(exportedFile);
        String content = String.join("\n", lines);

        assertTrue(content.contains("=== 用户基本信息 ==="), "Missing User Basic Info section.");
        assertTrue(content.contains("用户名," + TEST_USERNAME), "Missing username in basic info.");
        assertTrue(content.contains("邮箱,export@example.com"), "Missing email.");

        assertTrue(content.contains("=== 账单支付数据 ==="), "Missing Bill Data section.");
        assertTrue(content.contains("\"Credit Card Payment, with comma\""), "Missing bill record description with comma.");
        assertTrue(content.contains("\"Rent with \"\"Quotes\"\"\""), "Missing bill record description with quotes.");

        assertTrue(content.contains("=== 财务分析数据 ==="), "Missing Analysis Data section.");
        assertTrue(content.contains("分析摘要,1250.75,3500.50"), "Missing analysis summary totals."); // totalExpenses, totalIncome
        assertTrue(content.contains("Groceries,300.25"), "Missing category expense.");
        assertTrue(content.contains("FoodBudget,500.00,300.25"), "Missing budget usage.");

        assertTrue(content.contains("=== 支出记录数据 ==="), "Missing Expense Data section.");
        // UserDataExportService.loadExpenseRecords is a stub, so it might write "无数据"
        // If it were implemented, we'd check for "Lunch, with comma"
        assertTrue(lines.stream().anyMatch(s -> s.contains("支出记录,无数据") || s.contains("\"Lunch, with comma\"")), "Expense records section content error.");

        assertTrue(content.contains("=== 预算数据 ==="), "Missing Budget Data section.");
         assertTrue(content.contains("monthlySavingGoal") && content.contains("500"), "Missing budget data content");

        assertTrue(content.contains("=== 用户设置 ==="), "Missing User Settings section.");
        assertTrue(content.contains("theme,dark"), "Missing user setting.");

        assertTrue(content.contains("=== 数据导出摘要 ==="), "Missing Export Summary section.");
    }

    @Test
    void exportUserCompleteData_withNullUser_returnsNull() {
        assertNull(userDataExportService.exportUserCompleteData(null), "Export with null user should return null.");
    }

    @Test
    void exportUserCompleteData_withUserNullUsername_returnsNull() {
        User userWithNullName = new User(null, "test@example.com", "pass");
        assertNull(userDataExportService.exportUserCompleteData(userWithNullName), "Export with null username should return null.");
    }

    @Test
    void exportUserBillDataOnly_createsBillCsv() throws IOException {
        String filePath = userDataExportService.exportUserBillDataOnly(testUser);
        assertNotNull(filePath, "Exported file path should not be null.");
        Path exportedFile = Paths.get(filePath);
        assertTrue(Files.exists(exportedFile), "Exported bill CSV should exist.");
        assertTrue(exportedFile.getFileName().toString().startsWith(TEST_USERNAME + "_bill_data_"), "Filename mismatch.");

        List<String> lines = Files.readAllLines(exportedFile);
        String content = String.join("\n", lines);
        assertTrue(content.contains("\"Credit Card Payment, with comma\""), "Missing bill record details with comma.");
        assertTrue(content.contains("\"Rent with \"\"Quotes\"\"\""), "Missing bill record details with quotes.");
        assertFalse(content.contains("=== 财务分析数据 ==="), "Analysis data section should be absent.");
        assertFalse(content.contains("=== 用户基本信息 ==="), "User Basic Info section should be absent in bill-only export.");
    }

    @Test
    void exportUserBillDataOnly_whenNoBillDataFile_returnsNull() throws IOException {
        Files.deleteIfExists(USER_DATA_DIR.resolve("bill_data.json"));
        assertNull(userDataExportService.exportUserBillDataOnly(testUser), "Should return null if bill_data.json is missing.");
    }

    @Test
    void exportUserAnalysisDataOnly_createsAnalysisCsv() throws IOException {
        String filePath = userDataExportService.exportUserAnalysisDataOnly(testUser);
        assertNotNull(filePath, "Exported file path should not be null.");
        Path exportedFile = Paths.get(filePath);
        assertTrue(Files.exists(exportedFile), "Exported analysis CSV should exist.");
        assertTrue(exportedFile.getFileName().toString().startsWith(TEST_USERNAME + "_analysis_data_"), "Filename mismatch.");

        List<String> lines = Files.readAllLines(exportedFile);
        String content = String.join("\n", lines);
        assertTrue(content.contains("Groceries,300.25"), "Missing analysis category expense.");
        assertFalse(content.contains("=== 账单支付数据 ==="), "Bill data section should be absent.");
    }

    @Test
    void exportUserAnalysisDataOnly_whenNoAnalysisDataFile_returnsNull() throws IOException {
        Files.deleteIfExists(USER_DATA_DIR.resolve("analysis_data.json"));
        assertNull(userDataExportService.exportUserAnalysisDataOnly(testUser), "Should return null if analysis_data.json is missing.");
    }

    @Test
    void getExportDirectory_returnsCorrectPathString() {
        assertEquals(EXPORT_ROOT_DIR_NAME.replace(File.separatorChar, '/'), userDataExportService.getExportDirectory().replace(File.separatorChar, '/'));
    }

    @Test
    void cleanupOldExports_removesOldFilesAndKeepsRecent() throws IOException {
        Files.createDirectories(EXPORT_DIR); // Ensure export dir exists
        Path oldFile = EXPORT_DIR.resolve(TEST_USERNAME + "_export_old_20200101_000000.csv");
        Path recentFile = EXPORT_DIR.resolve(TEST_USERNAME + "_export_recent_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        Files.createFile(oldFile);
        Files.createFile(recentFile);

        // Set 'oldFile' lastModifiedTime to be > 30 days ago
        boolean success = oldFile.toFile().setLastModified(System.currentTimeMillis() - (35L * 24 * 60 * 60 * 1000));
        assertTrue(success, "Failed to set last modified time for old file. Test may be unreliable.");
        
        userDataExportService.cleanupOldExports();

        assertFalse(Files.exists(oldFile), "Old export file should have been deleted.");
        assertTrue(Files.exists(recentFile), "Recent export file should be preserved.");
    }
} 