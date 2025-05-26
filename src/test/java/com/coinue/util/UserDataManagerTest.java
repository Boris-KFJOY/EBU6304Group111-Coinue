package com.coinue.util;

import com.coinue.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserDataManagerTest {

    private UserDataManager userDataManager;
    private static final String TEST_USERS_FILE_NAME = "users.json";
    private static final String DATA_DIR_NAME = "data";
    private Path actualUsersFilePath = Paths.get(DATA_DIR_NAME, TEST_USERS_FILE_NAME);
    private Path backupUsersFilePath = Paths.get(DATA_DIR_NAME, "users.json.backup");

    // Helper method to reset the singleton instance for testing
    private static void resetSingleton(Class<?> clazz, String fieldName) {
        try {
            Field instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to reset singleton instance", e);
        }
    }
    
    // Helper to clear private caches using reflection
    private void clearCaches(UserDataManager manager) throws NoSuchFieldException, IllegalAccessException {
        Field userCacheField = UserDataManager.class.getDeclaredField("userCache");
        userCacheField.setAccessible(true);
        Map<?, ?> userCache = (Map<?, ?>) userCacheField.get(manager);
        userCache.clear();

        Field emailCacheField = UserDataManager.class.getDeclaredField("emailCache");
        emailCacheField.setAccessible(true);
        Map<?, ?> emailCache = (Map<?, ?>) emailCacheField.get(manager);
        emailCache.clear();
    }

    @BeforeEach
    void setUp() throws IOException {
        // Backup existing users.json if it exists
        if (Files.exists(actualUsersFilePath)) {
            Files.copy(actualUsersFilePath, backupUsersFilePath, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(actualUsersFilePath);
        } else {
             Files.deleteIfExists(backupUsersFilePath); // no original to backup
        }
        
        // Reset the singleton instance so it reloads/reinitializes with a clean slate
        resetSingleton(UserDataManager.class, "instance");
        userDataManager = UserDataManager.getInstance(); // This will create a new instance and attempt to load users.json (which is now deleted)
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up: delete the test users.json
        Files.deleteIfExists(actualUsersFilePath);

        // Restore backup if it exists
        if (Files.exists(backupUsersFilePath)) {
            Files.copy(backupUsersFilePath, actualUsersFilePath, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(backupUsersFilePath);
        } 
        // Reset singleton again to ensure no state leaks to other test classes if run in same JVM
        resetSingleton(UserDataManager.class, "instance");
    }

    private User createValidUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setSecurityQuestion("What is your pet's name?");
        user.setSecurityAnswer("Buddy");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void getInstance_shouldReturnSingletonInstance() {
        UserDataManager instance1 = UserDataManager.getInstance();
        UserDataManager instance2 = UserDataManager.getInstance();
        assertSame(instance1, instance2, "getInstance should return the same instance.");
    }

    @Test
    void createUser_successfulCreation() {
        User user = createValidUser("testUser1", "test1@example.com", "password123");
        assertTrue(userDataManager.createUser(user), "User creation should succeed.");
        assertNotNull(userDataManager.getUserByUsername("testUser1"), "User should exist after creation.");
        assertNotNull(userDataManager.getUserByEmail("test1@example.com"), "User should be findable by email.");
        assertTrue(Files.exists(actualUsersFilePath), "users.json should be created after saving a user.");
    }
    
    @Test
    void createUser_reloadAndVerifyPersistence() throws Exception {
        User user = createValidUser("persistUser", "persist@example.com", "passwordPersist");
        assertTrue(userDataManager.createUser(user), "Initial user creation should succeed.");

        // Reset singleton and caches to force reload from file
        resetSingleton(UserDataManager.class, "instance");
        UserDataManager newManager = UserDataManager.getInstance();
        
        assertNotNull(newManager.getUserByUsername("persistUser"), "User should be loaded from file by new instance.");
        assertNotNull(newManager.getUserByEmail("persist@example.com"));
    }

    @Test
    void createUser_usernameAlreadyExists() {
        User user1 = createValidUser("duplicateUser", "user1@example.com", "password123");
        userDataManager.createUser(user1);
        User user2 = createValidUser("duplicateUser", "user2@example.com", "password456");
        assertFalse(userDataManager.createUser(user2), "User creation should fail if username exists.");
    }

    @Test
    void createUser_emailAlreadyExists() {
        User user1 = createValidUser("userA", "duplicate@example.com", "password123");
        userDataManager.createUser(user1);
        User user2 = createValidUser("userB", "duplicate@example.com", "password456");
        assertFalse(userDataManager.createUser(user2), "User creation should fail if email exists.");
    }

    @Test
    void createUser_invalidData_emptyUsername() {
        User user = createValidUser("", "invalid@example.com", "password123");
        assertFalse(userDataManager.createUser(user));
    }

    @Test
    void createUser_invalidData_invalidEmail() {
        User user = createValidUser("invalidEmailUser", "invalid-email", "password123");
        assertFalse(userDataManager.createUser(user));
    }

    @Test
    void createUser_invalidData_shortPassword() {
        User user = createValidUser("shortPassUser", "short@example.com", "123");
        assertFalse(userDataManager.createUser(user));
    }
    
    @Test
    void createUser_invalidData_nullSecurityQuestion() {
        User user = createValidUser("noSecQ", "nosecq@example.com", "password123");
        user.setSecurityQuestion(null);
        assertFalse(userDataManager.createUser(user));
    }

    @Test
    void createUser_invalidData_emptySecurityAnswer() {
        User user = createValidUser("noSecA", "noseca@example.com", "password123");
        user.setSecurityAnswer("");
        assertFalse(userDataManager.createUser(user));
    }

    @Test
    void createUser_invalidData_nullBirthday() {
        User user = createValidUser("noBday", "nobday@example.com", "password123");
        user.setBirthday(null);
        assertFalse(userDataManager.createUser(user));
    }

    @Test
    void getUserByUsername_existingUser() {
        User user = createValidUser("findUser", "find@example.com", "password123");
        userDataManager.createUser(user);
        assertNotNull(userDataManager.getUserByUsername("findUser"));
    }

    @Test
    void getUserByUsername_nonExistingUser() {
        assertNull(userDataManager.getUserByUsername("nonExistent"));
    }

    @Test
    void getUserByEmail_existingUser() {
        User user = createValidUser("emailUser", "findbyemail@example.com", "password123");
        userDataManager.createUser(user);
        assertNotNull(userDataManager.getUserByEmail("findbyemail@example.com"));
    }

    @Test
    void getUserByEmail_nonExistingUser() {
        assertNull(userDataManager.getUserByEmail("nonexistent@example.com"));
    }

    @Test
    void validateLogin_correctUsernamePassword() {
        User user = createValidUser("loginUser", "login@example.com", "securePass");
        userDataManager.createUser(user);
        assertNotNull(userDataManager.validateLogin("loginUser", "securePass"));
    }

    @Test
    void validateLogin_correctEmailPassword() {
        User user = createValidUser("loginEmailUser", "loginbyemail@example.com", "securePass");
        userDataManager.createUser(user);
        assertNotNull(userDataManager.validateLogin("loginbyemail@example.com", "securePass"));
    }

    @Test
    void validateLogin_incorrectUsername() {
        User user = createValidUser("correctUser", "correct@example.com", "securePass");
        userDataManager.createUser(user);
        assertNull(userDataManager.validateLogin("wrongUser", "securePass"));
    }
    
    @Test
    void validateLogin_incorrectEmail() {
        User user = createValidUser("correctEmailUser", "correctE@example.com", "securePass");
        userDataManager.createUser(user);
        assertNull(userDataManager.validateLogin("wrongE@example.com", "securePass"));
    }

    @Test
    void validateLogin_correctUsernameIncorrectPassword() {
        User user = createValidUser("passUser", "pass@example.com", "correctPassword");
        userDataManager.createUser(user);
        assertNull(userDataManager.validateLogin("passUser", "wrongPassword"));
    }

    @Test
    void updateUser_successfulUpdate() {
        User originalUser = createValidUser("updateMe", "update@example.com", "oldPassword");
        userDataManager.createUser(originalUser);
        
        // Create a new User object or clone originalUser for the update
        // This ensures that modifications for the update don't change the originalUser instance prematurely
        // if it were fetched from a cache and then modified directly by the test before calling updateUser.
        User userWithUpdates = new User();
        userWithUpdates.setUsername(originalUser.getUsername()); // Keep the same username
        userWithUpdates.setSecurityQuestion(originalUser.getSecurityQuestion());
        userWithUpdates.setSecurityAnswer(originalUser.getSecurityAnswer());
        userWithUpdates.setBirthday(originalUser.getBirthday());

        userWithUpdates.setPassword("newPassword");
        userWithUpdates.setEmail("newupdate@example.com");
        
        assertTrue(userDataManager.updateUser(userWithUpdates), "User update should succeed.");
        
        User fetchedUpdatedUser = userDataManager.getUserByUsername("updateMe");
        assertNotNull(fetchedUpdatedUser, "Updated user should be retrievable by username.");
        assertEquals("newPassword", fetchedUpdatedUser.getPassword(), "Password should be updated.");
        assertEquals("newupdate@example.com", fetchedUpdatedUser.getEmail(), "Email should be updated.");
        
        // Check old email is no longer in emailCache and does not resolve to this user
        User userByOldEmail = userDataManager.getUserByEmail("update@example.com");
        assertNull(userByOldEmail, "Old email should not resolve to any user, or at least not this one.");
    }

    @Test
    void updateUser_nonExistingUser() {
        User user = createValidUser("noSuchUser", "nosuch@example.com", "password123");
        assertFalse(userDataManager.updateUser(user), "Updating non-existing user should fail.");
    }

    @Test
    void updateUser_changeEmailToExistingEmail() {
        User user1 = createValidUser("userOne", "one@example.com", "passOne");
        assertTrue(userDataManager.createUser(user1), "user1 creation failed");
        User userFromMgrForUser1 = userDataManager.getUserByEmail("one@example.com");
        assertNotNull(userFromMgrForUser1, "user1 not found by email after creation");
        assertEquals("userOne", userFromMgrForUser1.getUsername(), "user1 email mapping incorrect after creation");

        User user2_original = createValidUser("userTwo", "two@example.com", "passTwo");
        assertTrue(userDataManager.createUser(user2_original), "user2 creation failed");
        User userFromMgrForUser2 = userDataManager.getUserByEmail("two@example.com");
        assertNotNull(userFromMgrForUser2, "user2 not found by email after creation");
        assertEquals("userTwo", userFromMgrForUser2.getUsername(), "user2 email mapping incorrect after creation");

        // Verify initial state of emailCache 
        User initialOccupantOfOneExampleCom = userDataManager.getUserByEmail("one@example.com");
        assertNotNull(initialOccupantOfOneExampleCom);
        assertEquals("userOne", initialOccupantOfOneExampleCom.getUsername());

        User initialOccupantOfTwoExampleCom = userDataManager.getUserByEmail("two@example.com");
        assertNotNull(initialOccupantOfTwoExampleCom);
        assertEquals("userTwo", initialOccupantOfTwoExampleCom.getUsername());

        // Create a new User object for the update attempt, based on user2_original's data
        User user2_for_update_attempt = new User();
        user2_for_update_attempt.setUsername(user2_original.getUsername()); // "userTwo"
        user2_for_update_attempt.setPassword(user2_original.getPassword());
        user2_for_update_attempt.setSecurityQuestion(user2_original.getSecurityQuestion());
        user2_for_update_attempt.setSecurityAnswer(user2_original.getSecurityAnswer());
        user2_for_update_attempt.setBirthday(user2_original.getBirthday());
        user2_for_update_attempt.setEmail("one@example.com"); // Attempt to take user1's email
        
        System.out.println("DEBUG_TEST: Attempting to update userTwo to take userOne's email.");
        assertFalse(userDataManager.updateUser(user2_for_update_attempt), "Updating email to an existing one (userOne's) should fail for userTwo.");
        
        // Verify state after failed update attempt
        User userTwoAfterAttempt = userDataManager.getUserByUsername("userTwo");
        assertNotNull(userTwoAfterAttempt, "userTwo should still exist by username.");
        assertEquals("two@example.com", userTwoAfterAttempt.getEmail(), "UserTwo email should not have changed from its original.");

        User userOneAfterAttempt = userDataManager.getUserByUsername("userOne");
        assertNotNull(userOneAfterAttempt, "userOne should still exist by username.");
        assertEquals("one@example.com", userOneAfterAttempt.getEmail(), "UserOne email should be unchanged.");
        
        // Final check on email cache integrity
        User finalOccupantOfOneExampleCom = userDataManager.getUserByEmail("one@example.com");
        assertNotNull(finalOccupantOfOneExampleCom, "one@example.com should still be occupied after failed update.");
        assertEquals("userOne", finalOccupantOfOneExampleCom.getUsername(), "one@example.com should still be mapped to userOne.");

        User finalOccupantOfTwoExampleCom = userDataManager.getUserByEmail("two@example.com");
        assertNotNull(finalOccupantOfTwoExampleCom, "two@example.com should still be occupied by userTwo as update failed.");
        assertEquals("userTwo", finalOccupantOfTwoExampleCom.getUsername(), "two@example.com should still be mapped to userTwo.");
    }
    
    @Test
    void updateUser_updateWithInvalidData(){
        User userToSetup = createValidUser("validUpdateUser", "validUpdate@example.com", "passwordValid");
        userDataManager.createUser(userToSetup);
        
        // Create a separate user object for update attempt, or clone if User implements Cloneable
        User userForUpdateAttempt = createValidUser("validUpdateUser", "validUpdate@example.com", "passwordValid");
        userForUpdateAttempt.setPassword("short"); // Invalid new password
        
        assertFalse(userDataManager.updateUser(userForUpdateAttempt), "Update with invalid data should fail.");
        
        // Fetch the user from the manager again to ensure its state hasn't changed in the cache/storage
        User userAfterFailedUpdate = userDataManager.getUserByUsername("validUpdateUser");
        assertNotNull(userAfterFailedUpdate, "User should still exist.");
        assertEquals("passwordValid", userAfterFailedUpdate.getPassword(), "Password should not be updated with invalid data");
        assertEquals("validUpdate@example.com", userAfterFailedUpdate.getEmail(), "Email should not be updated with invalid data");
    }

    @Test
    void findUserByUsernameOrEmail_byUsername() {
        User user = createValidUser("findMeName", "findMeName@example.com", "passFind");
        userDataManager.createUser(user);
        assertNotNull(userDataManager.findUserByUsernameOrEmail("findMeName"));
    }

    @Test
    void findUserByUsernameOrEmail_byEmail() {
        User user = createValidUser("findMeEmail", "findMeEmail@example.com", "passFind");
        userDataManager.createUser(user);
        assertNotNull(userDataManager.findUserByUsernameOrEmail("findMeEmail@example.com"));
    }

    @Test
    void findUserByUsernameOrEmail_nonExistent() {
        assertNull(userDataManager.findUserByUsernameOrEmail("nobodyHome"));
    }

    @Test
    void resetPassword_successfulReset() {
        User user = createValidUser("resetPassUser", "reset@example.com", "oldPass");
        user.setSecurityAnswer("secretAnswer");
        userDataManager.createUser(user);

        assertTrue(userDataManager.resetPassword("resetPassUser", "secretAnswer", "newSecurePass"));
        User updatedUser = userDataManager.getUserByUsername("resetPassUser");
        assertEquals("newSecurePass", updatedUser.getPassword());
    }

    @Test
    void resetPassword_nonExistingUser() {
        assertFalse(userDataManager.resetPassword("ghostUser", "anyAnswer", "newPass"));
    }

    @Test
    void resetPassword_incorrectSecurityAnswer() {
        User user = createValidUser("secAnsUser", "secans@example.com", "oldPass");
        user.setSecurityAnswer("correctAnswer");
        userDataManager.createUser(user);

        assertFalse(userDataManager.resetPassword("secAnsUser", "wrongAnswer", "newPass"));
        assertEquals("oldPass", userDataManager.getUserByUsername("secAnsUser").getPassword());
    }

    @Test
    void resetPassword_newPasswordTooShort() {
        User user = createValidUser("shortNewPassUser", "shortnew@example.com", "oldPass");
        user.setSecurityAnswer("answer");
        userDataManager.createUser(user);

        assertFalse(userDataManager.resetPassword("shortNewPassUser", "answer", "new"));
        assertEquals("oldPass", userDataManager.getUserByUsername("shortNewPassUser").getPassword());
    }
} 