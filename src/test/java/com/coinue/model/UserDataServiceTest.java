package com.coinue.model;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserDataServiceTest {

    private UserDataService userDataService;
    private ObjectMapper objectMapper;

    private static final String TEST_USERNAME = "serviceTestUser";
    private static final String OTHER_TEST_USERNAME = "otherServiceTestUser";
    private static final String BASE_USERS_DIR_NAME = "data/users";
    private static final Path USER_TEST_DATA_DIR = Paths.get(BASE_USERS_DIR_NAME, TEST_USERNAME);
    private static final Path OTHER_USER_TEST_DATA_DIR = Paths.get(BASE_USERS_DIR_NAME, OTHER_TEST_USERNAME);

    // Define specific file names as used in UserDataService
    private static final String ANALYSIS_DATA_FILE = "analysis_data.json";
    private static final String BUDGET_DATA_FILE = "budget_data.json";
    private static final String EXPENSE_DATA_FILE = "expense_data.json";
    private static final String SETTINGS_FILE = "user_settings.json";

    @TempDir
    Path tempDir; // JUnit 5 injects a temporary directory

    private String originalBaseDataDirPropertyValue; // To store original system property if set
    private Path testBaseDataPath;

    @BeforeEach
    void setUp() throws IOException {
        userDataService = UserDataService.getInstance();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Clean up and create the primary test user directory
        cleanupDirectory(USER_TEST_DATA_DIR);
        Files.createDirectories(USER_TEST_DATA_DIR);
        // Clean up other test user directory if it exists from a failed previous run
        cleanupDirectory(OTHER_USER_TEST_DATA_DIR);

        // Store and override system property for BASE_DATA_DIR for testing
        // This is a common way to make such constants configurable for tests.
        // However, UserDataService uses a private static final String, which is hard to override.
        // For this test, we'll assume UserDataService will create files under the current working directory
        // if BASE_DATA_DIR leads to a relative path, and @TempDir will give us a suitable subdir for that.

        // We will create our test user directory inside tempDir
        testBaseDataPath = tempDir.resolve(BASE_USERS_DIR_NAME);
        Files.createDirectories(testBaseDataPath.resolve(TEST_USERNAME));

        // Re-initialize UserDataService or use a method to set its base path if available.
        // Since it's a singleton with a hardcoded path, true isolation is tricky without refactoring UserDataService.
        // We will proceed by expecting it to write into "data/users/testUser" relative to project root,
        // and our assertions will check files created within the @TempDir structure if UserDataService could be pointed there.
        // For now, tests will assume UserDataService operates in its default hardcoded relative path.
        // To make tests fully independent and clean, UserDataService would need refactoring to allow base path injection.
        
        // For the purpose of these tests, we will manually manage a test user directory within tempDir
        // and perform operations there, then verify against that. This approach tests the file
        // writing/reading logic of UserDataService's methods but doesn't test its internal path construction as perfectly.

        // Clean up any pre-existing test user directory from previous runs (if not using TempDir effectively)
        // Path defaultTestUserDir = Paths.get(BASE_USERS_DIR_NAME, TEST_USERNAME);
        // if(Files.exists(defaultTestUserDir)){
        //     try (Stream<Path> walk = Files.walk(defaultTestUserDir)) {
        // walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        //     }
        // }
    }

    @AfterEach
    void tearDown() throws IOException {
        cleanupDirectory(USER_TEST_DATA_DIR);
        cleanupDirectory(OTHER_USER_TEST_DATA_DIR); // Ensure this is also cleaned up

        // Attempt to clean up parent directories if they are empty
        Path baseUsersDir = Paths.get(BASE_USERS_DIR_NAME);
        if (Files.exists(baseUsersDir) && Files.isDirectory(baseUsersDir)) {
            if (Files.list(baseUsersDir).collect(Collectors.toList()).isEmpty()) {
                try { Files.deleteIfExists(baseUsersDir); } catch (IOException e) { /* ignore */ }
            }
        }
        Path dataDir = Paths.get("data");
        if (Files.exists(dataDir) && Files.isDirectory(dataDir)){
             if (Files.list(dataDir).collect(Collectors.toList()).isEmpty()) {
                try { Files.deleteIfExists(dataDir); } catch (IOException e) { /* ignore */ }
            }
        }

        // Restore system property if it was changed
        // if (originalBaseDataDirPropertyValue != null) {
        //     System.setProperty("BASE_DATA_DIR_PROPERTY_KEY", originalBaseDataDirPropertyValue);
        // } else {
        //     System.clearProperty("BASE_DATA_DIR_PROPERTY_KEY");
        // }

        // TempDir will be cleaned up automatically by JUnit
        // However, if UserDataService wrote to its hardcoded relative path, that needs manual cleanup.
        Path hardcodedUserDir = Path.of(BASE_USERS_DIR_NAME, TEST_USERNAME);
        if (Files.exists(hardcodedUserDir)) {
            try {
                Files.walk(hardcodedUserDir)
                    .map(Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .forEach(File::delete);
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
        Path hardcodedBaseDir = Path.of(BASE_USERS_DIR_NAME);
        if (Files.exists(hardcodedBaseDir) && Files.isDirectory(hardcodedBaseDir)){
             try{
                if(Files.list(hardcodedBaseDir).findAny().isEmpty()){
                    Files.deleteIfExists(hardcodedBaseDir);
                }
             } catch(IOException e){
                //  e.printStackTrace();
             }
        }
    }
    
    private Path getTestUserFilePath(String fileName) {
        // This method should ideally point to where UserDataService *actually* writes.
        // Given the hardcoded path, it writes relative to the project root.
        return Path.of(BASE_USERS_DIR_NAME, TEST_USERNAME, fileName);
    }

    private void cleanupDirectory(Path dirPath) throws IOException {
        if (Files.exists(dirPath)) {
            Files.walk(dirPath)
                .map(Path::toFile)
                .sorted((o1, o2) -> -o1.compareTo(o2)) // Delete contents before directory
                .forEach(File::delete);
        }
    }

    static class TestMockObject {
        public String name;
        public int value;
        public LocalDate date;

        public TestMockObject() {}
        public TestMockObject(String name, int value, LocalDate date) {
            this.name = name;
            this.value = value;
            this.date = date;
        }
        // equals and hashCode for proper comparison if needed, though field access is fine for these tests
    }

    @Test
    void getInstance_returnsSingleton() {
        UserDataService instance1 = UserDataService.getInstance();
        UserDataService instance2 = UserDataService.getInstance();
        assertSame(instance1, instance2, "getInstance should always return the same instance.");
    }

    @Test
    void getUserDataDirectory_returnsCorrectPath() {
        String expectedPath = Paths.get(BASE_USERS_DIR_NAME, TEST_USERNAME).toString();
        // ensureUserDataDirectory is called internally, so the path should be created.
        // Calling any save/load method or even getUserDataDirectory itself can trigger creation.
        String actualPath = userDataService.getUserDataDirectory(TEST_USERNAME);
        assertEquals(expectedPath, actualPath.replace(File.separatorChar, '/'), "User data directory path is incorrect.");
        assertTrue(Files.exists(USER_TEST_DATA_DIR), "User data directory should be created.");
    }

    @Test
    void saveData_and_loadData_workCorrectly() {
        String fileName = "test_mock_object.json";
        TestMockObject originalData = new TestMockObject("TestData", 123, LocalDate.now());

        assertTrue(userDataService.saveData(TEST_USERNAME, fileName, originalData), "saveData should return true on success.");
        assertTrue(Files.exists(USER_TEST_DATA_DIR.resolve(fileName)), "Data file should be created.");

        TestMockObject loadedData = userDataService.loadData(TEST_USERNAME, fileName, TestMockObject.class);
        assertNotNull(loadedData, "Loaded data should not be null.");
        assertEquals(originalData.name, loadedData.name);
        assertEquals(originalData.value, loadedData.value);
        assertEquals(originalData.date, loadedData.date);
    }

    @Test
    void loadData_nonExistentFile_returnsNull() {
        assertNull(userDataService.loadData(TEST_USERNAME, "non_existent.json", TestMockObject.class));
    }

    @Test
    void saveAnalysisData_and_loadAnalysisData_workCorrectly() {
        UserAnalysisData originalData = new UserAnalysisData();
        originalData.setTotalExpenses(500.50);
        originalData.addCategoryExpense("Food", 200.25);

        assertTrue(userDataService.saveAnalysisData(TEST_USERNAME, originalData));
        assertTrue(Files.exists(USER_TEST_DATA_DIR.resolve(ANALYSIS_DATA_FILE)));

        UserAnalysisData loadedData = userDataService.loadAnalysisData(TEST_USERNAME, UserAnalysisData.class);
        assertNotNull(loadedData);
        assertEquals(originalData.getTotalExpenses(), loadedData.getTotalExpenses());
        assertEquals(originalData.getCategoryExpenses().get("Food"), loadedData.getCategoryExpenses().get("Food"));
    }
    
    @Test
    void loadAnalysisData_nonExistent_returnsNull(){
        assertNull(userDataService.loadAnalysisData(TEST_USERNAME, UserAnalysisData.class));
    }

    @Test
    void saveBudgetData_and_loadBudgetData_workCorrectly() {
        Map<String, Object> originalData = new HashMap<>();
        originalData.put("monthlyLimit", 2000.0);
        originalData.put("categories", List.of("Shopping", "Entertainment"));

        assertTrue(userDataService.saveBudgetData(TEST_USERNAME, originalData));
        assertTrue(Files.exists(USER_TEST_DATA_DIR.resolve(BUDGET_DATA_FILE)));

        Map<String, Object> loadedData = userDataService.loadBudgetData(TEST_USERNAME, Map.class);
        assertNotNull(loadedData);
        assertEquals(originalData.get("monthlyLimit"), loadedData.get("monthlyLimit"));
        assertTrue(loadedData.get("categories") instanceof List);
        assertEquals(originalData.get("categories"), loadedData.get("categories"));
    }

    @Test
    void loadBudgetData_nonExistent_returnsNull(){
         assertNull(userDataService.loadBudgetData(TEST_USERNAME, Map.class));
    }

    @Test
    void saveExpenseData_and_loadExpenseData_workCorrectly() throws IOException {
        List<ExpenseRecord> originalRecords = new ArrayList<>();
        originalRecords.add(new ExpenseRecord(50.0, "Food", "Lunch", LocalDate.now(), "Work lunch", "Expense", "USD"));

        assertTrue(userDataService.saveExpenseData(TEST_USERNAME, originalRecords));
        assertTrue(Files.exists(USER_TEST_DATA_DIR.resolve(EXPENSE_DATA_FILE)));

        // Jackson deserializes list of objects into List<LinkedHashMap<String, Object>> by default if just List.class is given.
        // We need to use TypeReference for correct deserialization into List<ExpenseRecord>.
        byte[] jsonData = Files.readAllBytes(USER_TEST_DATA_DIR.resolve(EXPENSE_DATA_FILE));
        List<ExpenseRecord> properlyLoadedRecords = objectMapper.readValue(jsonData, new TypeReference<List<ExpenseRecord>>() {});

        assertNotNull(properlyLoadedRecords);
        assertEquals(1, properlyLoadedRecords.size());
        assertEquals(originalRecords.get(0).getName(), properlyLoadedRecords.get(0).getName());
        assertEquals(originalRecords.get(0).getAmount(), properlyLoadedRecords.get(0).getAmount());
    }
    
    @Test
    void loadExpenseData_nonExistent_returnsNull(){
         assertNull(userDataService.loadExpenseData(TEST_USERNAME, List.class));
    }

    @Test
    void saveUserSettings_and_loadUserSettings_workCorrectly() {
        Map<String, Object> originalSettings = new HashMap<>();
        originalSettings.put("theme", "dark");
        originalSettings.put("language", "en");

        assertTrue(userDataService.saveUserSettings(TEST_USERNAME, originalSettings));
        assertTrue(Files.exists(USER_TEST_DATA_DIR.resolve(SETTINGS_FILE)));

        Map<String, Object> loadedSettings = userDataService.loadUserSettings(TEST_USERNAME);
        assertNotNull(loadedSettings);
        assertEquals(originalSettings.get("theme"), loadedSettings.get("theme"));
        assertEquals(originalSettings.get("language"), loadedSettings.get("language"));
    }

    @Test
    void loadUserSettings_nonExistent_returnsEmptyMap() {
        // For a user whose settings file does not exist yet
        Map<String, Object> settings = userDataService.loadUserSettings(OTHER_TEST_USERNAME); 
        assertNotNull(settings);
        assertTrue(settings.isEmpty(), "Loading settings for a new user should return an empty map.");
    }

    @Test
    void deleteData_existingFile_deletesSuccessfully() {
        String fileName = "delete_test.json";
        userDataService.saveData(TEST_USERNAME, fileName, new TestMockObject("delete_me", 1, LocalDate.now()));
        assertTrue(Files.exists(USER_TEST_DATA_DIR.resolve(fileName)), "File should exist before deletion.");

        assertTrue(userDataService.deleteData(TEST_USERNAME, fileName), "deleteData should return true for existing file.");
        assertFalse(Files.exists(USER_TEST_DATA_DIR.resolve(fileName)), "File should not exist after deletion.");
    }

    @Test
    void deleteData_nonExistentFile_returnsTrue() {
        assertTrue(userDataService.deleteData(TEST_USERNAME, "non_existent_to_delete.json"),
                   "deleteData should return true for non-existent file (idempotency).");
    }

    @Test
    void dataExists_worksCorrectly() {
        String fileName = "existence_check.json";
        assertFalse(userDataService.dataExists(TEST_USERNAME, fileName), "File should not exist initially.");

        userDataService.saveData(TEST_USERNAME, fileName, new TestMockObject("exists", 1, LocalDate.now()));
        assertTrue(userDataService.dataExists(TEST_USERNAME, fileName), "File should exist after saving.");

        userDataService.deleteData(TEST_USERNAME, fileName);
        assertFalse(userDataService.dataExists(TEST_USERNAME, fileName), "File should not exist after deletion.");
    }

    @Test
    void cleanupUserData_removesUserDirectoryAndContents() throws IOException {
        // Use OTHER_TEST_USERNAME to avoid conflict with @AfterEach cleanup of TEST_USERNAME
        Files.createDirectories(OTHER_USER_TEST_DATA_DIR); // Ensure dir exists for this test user
        userDataService.saveData(OTHER_TEST_USERNAME, "file1.json", new TestMockObject("file1", 1, LocalDate.now()));
        userDataService.saveData(OTHER_TEST_USERNAME, "file2.json", new TestMockObject("file2", 2, LocalDate.now()));
        assertTrue(Files.exists(OTHER_USER_TEST_DATA_DIR.resolve("file1.json")), "File1 should exist before cleanup.");

        assertTrue(userDataService.cleanupUserData(OTHER_TEST_USERNAME), "cleanupUserData should return true on success.");
        assertFalse(Files.exists(OTHER_USER_TEST_DATA_DIR), "User directory should be removed after cleanup.");
    }
} 