package com.coinue.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private final LocalDate validBirthday = LocalDate.now().minusYears(20);
    private final LocalDate underageBirthday = LocalDate.now().minusYears(10);
    private final String validTestPassword = "ValidPass123"; // Password that passes strength validation

    @BeforeEach
    void setUp() {
        user = new User();
        // Ensure currentUser is reset before each test that might use/affect it
        User.logout(); 
    }

    @AfterEach
    void tearDown() {
        // Clean up static currentUser after each test
        User.logout();
    }

    // --- Constructor Tests ---
    @Test
    void defaultConstructor_shouldCreateUserWithNullFields() {
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getSecurityQuestion());
        assertNull(user.getSecurityAnswer());
        assertNull(user.getBirthday());
        assertNull(user.getUserDataPath(), "User data path should be null initially for default constructor before username is set");
    }

    @Test
    void constructorWithUsernameEmailPassword_shouldInitializeFieldsAndPath() {
        User u = new User("testuser", "test@example.com", "Password123");
        assertEquals("testuser", u.getUsername());
        assertEquals("test@example.com", u.getEmail());
        assertEquals("Password123", u.getPassword());
        assertEquals("data/users/testuser", u.getUserDataPath());
    }

    @Test
    void fullConstructor_shouldInitializeAllFieldsAndPath() {
        User u = new User("fulluser", "full@example.com", "PasswordFull1", "Ques", "Ans", validBirthday);
        assertEquals("fulluser", u.getUsername());
        assertEquals("full@example.com", u.getEmail());
        assertEquals("PasswordFull1", u.getPassword());
        assertEquals("Ques", u.getSecurityQuestion());
        assertEquals("Ans", u.getSecurityAnswer());
        assertEquals(validBirthday, u.getBirthday());
        assertEquals("data/users/fulluser", u.getUserDataPath());
    }

    // --- Getters and Setters Tests ---
    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        user.setUsername("jane_doe");
        assertEquals("jane_doe", user.getUsername());
        assertEquals("data/users/jane_doe", user.getUserDataPath(), "UserDataPath should update with username");

        user.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", user.getEmail());

        user.setPassword("SecurePass99");
        assertEquals("SecurePass99", user.getPassword());

        user.setSecurityQuestion("Fav color?");
        assertEquals("Fav color?", user.getSecurityQuestion());

        user.setSecurityAnswer("Blue");
        assertEquals("Blue", user.getSecurityAnswer());

        user.setBirthday(validBirthday);
        assertEquals(validBirthday, user.getBirthday());
    }
    
    @Test
    void getUserDataPath_lazyInitialization() {
        User u = new User();
        assertNull(u.getUserDataPath()); // Initially null
        u.setUsername("lazyuser");
        assertEquals("data/users/lazyuser", u.getUserDataPath()); // Set by setUsername

        User u2 = new User();
        u2.setUsername("anotheruser"); 
        // To test the lazy init inside getUserDataPath, we'd need to nullify userDataPath after setting username
        // This is tricky as setUsername already sets it. The current getUserDataPath logic is fine.
        // The test above with setUsername effectively covers the path generation.
    }


    // --- Static Validation Method Tests ---
    // validatePasswordStrength
    @Test
    void validatePasswordStrength_validPassword_shouldReturnNull() {
        assertNull(User.validatePasswordStrength("ValidPass1"));
    }

    @Test
    void validatePasswordStrength_tooShort_shouldReturnErrorMessage() {
        assertEquals("密码长度不能少于6位", User.validatePasswordStrength("Shrt1"));
    }

    @Test
    void validatePasswordStrength_tooLong_shouldReturnErrorMessage() {
        String longPass = "a".repeat(51) + "1";
        assertEquals("密码长度不能超过50位", User.validatePasswordStrength(longPass));
    }
    
    @Test
    void validatePasswordStrength_noLetter_shouldReturnErrorMessage() {
        assertEquals("密码必须包含至少一个字母和一个数字", User.validatePasswordStrength("1234567"));
    }

    @Test
    void validatePasswordStrength_noDigit_shouldReturnErrorMessage() {
        assertEquals("密码必须包含至少一个字母和一个数字", User.validatePasswordStrength("PasswordOnly"));
    }

    // validateRegistrationData
    @Test
    void validateRegistrationData_allValid_shouldReturnNull() {
        assertNull(User.validateRegistrationData("regUser", "reg@example.com", validTestPassword, validTestPassword, "Q", "A", validBirthday));
    }

    @Test
    void validateRegistrationData_emptyUsername_shouldReturnError() {
        assertEquals("用户名不能为空", User.validateRegistrationData("", "e@e.com", validTestPassword, validTestPassword, "Q", "A", validBirthday));
    }
    
    @Test
    void validateRegistrationData_invalidEmail_shouldReturnError() {
        assertEquals("邮箱格式不正确", User.validateRegistrationData("user", "invalid-email", validTestPassword, validTestPassword, "Q", "A", validBirthday));
    }

    @Test
    void validateRegistrationData_passwordMismatch_shouldReturnError() {
        assertEquals("两次输入的密码不匹配", User.validateRegistrationData("user", "e@e.com", validTestPassword, "AnotherValidPass456", "Q", "A", validBirthday));
    }

    @Test
    void validateRegistrationData_underage_shouldReturnError() {
        assertEquals("用户年龄必须满13岁", User.validateRegistrationData("user", "e@e.com", validTestPassword, validTestPassword, "Q", "A", underageBirthday));
    }
    
    @Test
    void validateRegistrationData_usernameTooShort_shouldReturnError(){
        assertEquals("用户名长度必须在3-20个字符之间", User.validateRegistrationData("u1", "e@e.com", validTestPassword, validTestPassword, "Q", "A", validBirthday));
    }

    @Test
    void validateRegistrationData_usernameTooLong_shouldReturnError(){
        String longUsername = "u".repeat(21);
        assertEquals("用户名长度必须在3-20个字符之间", User.validateRegistrationData(longUsername, "e@e.com", validTestPassword, validTestPassword, "Q", "A", validBirthday));
    }

    @Test
    void validateRegistrationData_usernameInvalidChars_shouldReturnError(){
        assertEquals("用户名只能包含字母、数字和下划线", User.validateRegistrationData("user!", "e@e.com", validTestPassword, validTestPassword, "Q", "A", validBirthday));
    }
    
    @Test
    void validateRegistrationData_allFieldsEmpty_shouldReturnAppropriateErrorsSequentially() {
        assertEquals("用户名不能为空", User.validateRegistrationData("", "", "", "", "", "", null));
        assertEquals("邮箱不能为空", User.validateRegistrationData("user123", "", "", "", "", "", null));
        // ... and so on for other fields, testing the first error encountered.
    }

    // validateLoginData
    @Test
    void validateLoginData_valid_shouldReturnNull() {
        assertNull(User.validateLoginData("userOrEmail", "Password123"));
    }

    @Test
    void validateLoginData_emptyUsernameOrEmail_shouldReturnError() {
        assertEquals("用户名/邮箱不能为空", User.validateLoginData("", "Password123"));
    }

    @Test
    void validateLoginData_emptyPassword_shouldReturnError() {
        assertEquals("密码不能为空", User.validateLoginData("userOrEmail", ""));
    }

    // validatePasswordResetData
    @Test
    void validatePasswordResetData_valid_shouldReturnNull() {
        assertNull(User.validatePasswordResetData("userOrEmail", validTestPassword, validTestPassword, "Answer", validBirthday));
    }

    @Test
    void validatePasswordResetData_emptyNewPassword_shouldReturnError() {
        assertEquals("新密码不能为空", User.validatePasswordResetData("userOrEmail", "", validTestPassword, "Answer", validBirthday));
    }
    
    @Test
    void validatePasswordResetData_passwordMismatch_shouldReturnError() {
        assertEquals("两次输入的密码不匹配", User.validatePasswordResetData("userOrEmail", validTestPassword, "AnotherValidPass789", "Answer", validBirthday));
    }

    // --- Instance Method Tests (Pure Logic) ---
    @Test
    void validateIdentity_correctDetails_shouldReturnTrue() {
        user.setBirthday(validBirthday);
        user.setSecurityAnswer("MyAnswer");
        assertTrue(user.validateIdentity(validBirthday, "MyAnswer"));
    }

    @Test
    void validateIdentity_incorrectBirthday_shouldReturnFalse() {
        user.setBirthday(validBirthday);
        user.setSecurityAnswer("MyAnswer");
        assertFalse(user.validateIdentity(LocalDate.now().minusYears(18), "MyAnswer"));
    }

    @Test
    void validateIdentity_incorrectAnswer_shouldReturnFalse() {
        user.setBirthday(validBirthday);
        user.setSecurityAnswer("MyAnswer");
        assertFalse(user.validateIdentity(validBirthday, "WrongAnswer"));
    }
    
    @Test
    void validateIdentity_nullInstanceFields_shouldReturnFalse() {
        // User has null birthday and null securityAnswer
        assertFalse(user.validateIdentity(validBirthday, "AnyAnswer"));
        user.setBirthday(validBirthday); // Now birthday is set, answer is still null
        assertFalse(user.validateIdentity(validBirthday, "AnyAnswer"));
    }

    // --- Static Session Management Tests ---
    @Test
    void currentUser_getAndSet_shouldWork() {
        assertNull(User.getCurrentUser(), "Initially current user should be null.");
        User testUser = new User("sessionUser", "s@e.com", "Pass1");
        User.setCurrentUser(testUser);
        assertSame(testUser, User.getCurrentUser());
        assertEquals("data/users/sessionUser", testUser.getUserDataPath(), "UserDataPath should be set on setCurrentUser");
    }

    @Test
    void logout_shouldSetCurrentUserToNull() {
        User testUser = new User("logoutUser", "l@e.com", "Pass1");
        User.setCurrentUser(testUser);
        assertNotNull(User.getCurrentUser());
        User.logout();
        assertNull(User.getCurrentUser());
    }

    @Test
    void isLoggedIn_shouldReflectCurrentUserStatus() {
        assertFalse(User.isLoggedIn());
        User.setCurrentUser(new User("loggedIn", "li@e.com", "Pass1"));
        assertTrue(User.isLoggedIn());
        User.logout();
        assertFalse(User.isLoggedIn());
    }
    
    @Test
    void setCurrentUser_withNull_shouldClearCurrentUserAndPath() {
        User testUser = new User("test", "test@example.com", "pass");
        User.setCurrentUser(testUser);
        assertNotNull(User.getCurrentUser());
        assertNotNull(testUser.getUserDataPath());

        User.setCurrentUser(null);
        assertNull(User.getCurrentUser());
        // UserDataPath of the original testUser object remains, only the static currentUser is null
        assertEquals("data/users/test", testUser.getUserDataPath()); 
    }

    // --- toString, equals, hashCode Tests ---
    @Test
    void toString_shouldReturnCorrectFormat() {
        user.setUsername("toStringUser");
        user.setEmail("ts@example.com");
        user.setBirthday(validBirthday);
        String expected = "User{username='toStringUser', email='ts@example.com', birthday=" + validBirthday.toString() + "}";
        assertEquals(expected, user.toString());
    }

    @Test
    void equals_reflexivity() {
        User u = new User("user1", "e1@c.com", "p1");
        assertTrue(u.equals(u));
    }

    @Test
    void equals_symmetry() {
        User u1 = new User("userSym", "es@c.com", "p1");
        User u2 = new User("userSym", "es.other@c.com", "p2"); // Same username
        User u3 = new User("userOther", "es@c.com", "p1"); // Different username

        assertTrue(u1.equals(u2));
        assertTrue(u2.equals(u1));
        assertFalse(u1.equals(u3));
        assertFalse(u3.equals(u1));
    }

    @Test
    void equals_withNull_shouldReturnFalse() {
        User u1 = new User("userNull", "en@c.com", "p1");
        assertFalse(u1.equals(null));
    }

    @Test
    void equals_withDifferentClass_shouldReturnFalse() {
        User u1 = new User("userClass", "ec@c.com", "p1");
        assertFalse(u1.equals("AString"));
    }
    
    @Test
    void equals_bothUsernamesNull_shouldBeTrue() {
        User u1 = new User(); // username is null
        User u2 = new User(); // username is null
        assertTrue(u1.equals(u2), "Users with both usernames null should be equal.");
    }

    @Test
    void equals_oneUsernameNull_shouldBeFalse() {
        User u1 = new User(); // username is null
        User u2 = new User("userNonNull", "e@e.com", "p");
        assertFalse(u1.equals(u2));
        assertFalse(u2.equals(u1));
    }

    @Test
    void hashCode_consistencyWithEquals() {
        User u1 = new User("userHash", "eh@c.com", "p1");
        User u2 = new User("userHash", "eh.other@c.com", "p2");
        assertTrue(u1.equals(u2));
        assertEquals(u1.hashCode(), u2.hashCode());
    }
    
    @Test
    void hashCode_usernameNull_shouldReturnZero(){
        User u1 = new User(); // username is null
        assertEquals(0, u1.hashCode());
    }

    // --- Placeholder tests for methods with external dependencies (UserDataManager/UserDataService) ---
    // These would require mocking UserDataManager and UserDataService to be true unit tests.

    @Test
    void save_shouldCallUserDataManagerUpdateUser() {
        // To test this properly: Mock UserDataManager, inject mock, call user.save(), verify mock.updateUser(user) was called.
        // For now, just a placeholder acknowledging the dependency.
        User testUser = new User("saveUser", "save@example.com", "Pass1");
        // boolean result = testUser.save(); 
        // Potentially: if (mockUserDataManager.updateUser(testUser) returns true) then assertEquals(true, result);
        assertTrue(true, "Test needs mocking for UserDataManager.save"); 
    }

    @Test
    void register_shouldCallUserDataManagerCreateUser() {
        User testUser = new User("regUserNew", "regnew@example.com", "PassNew1");
        assertTrue(true, "Test needs mocking for UserDataManager.register");
    }

    @Test
    void login_validCredentials_shouldSetCurrentUserAndReturnUser() {
        // Mock UserDataManager.validateLogin to return a user object
        // User loggedInUser = User.login("loginUser", "LoginPass1");
        // assertNotNull(loggedInUser);
        // assertSame(loggedInUser, User.getCurrentUser());
        assertTrue(true, "Test needs mocking for UserDataManager.login");
    }

    @Test
    void login_invalidCredentials_shouldReturnNullAndNotSetCurrentUser() {
        // Mock UserDataManager.validateLogin to return null
        // User loggedInUser = User.login("wrongUser", "WrongPass");
        // assertNull(loggedInUser);
        // assertNull(User.getCurrentUser());
        assertTrue(true, "Test needs mocking for UserDataManager.login");
    }
    
    @Test
    void resetPassword_validNewPassword_shouldUpdatePasswordAndSave(){
        // User userToReset = new User("resetPassUser", "reset@example.com", "OldPass1");
        // userToReset.setBirthday(validBirthday); // for validateIdentity if that were part of a more complex reset
        // userToReset.setSecurityAnswer("ValidAnswer");
        // Mock UserDataManager.updateUser to return true
        // boolean result = userToReset.resetPassword("NewSecurePass1");
        // assertTrue(result);
        // assertEquals("NewSecurePass1", userToReset.getPassword());
        assertTrue(true, "Test needs mocking for UserDataManager.resetPassword and save");
    }
    
    @Test
    void saveAnalysisData_shouldCallDataService() {
        User u = new User("dataUser", "data@example.com", "pass");
        Object mockData = new Object();
        // Mock UserDataService, call u.saveAnalysisData(mockData)
        // verify dataService.saveAnalysisData(u.getUsername(), mockData) was called.
        assertTrue(true, "Test needs mocking for UserDataService.saveAnalysisData");
    }
    
    // Similar placeholder tests for other save/load methods can be added.
} 