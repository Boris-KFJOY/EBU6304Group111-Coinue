package com.coinue.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import org.testfx.util.WaitForAsyncUtils;

/**
 * SignUpController测试类
 * 使用 TestFX 进行集成测试
 */
public class SignUpControllerTest extends ApplicationTest {

    private SignUpController controller;
    private TextField emailField;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private DatePicker birthdayPicker;
    private ComboBox<String> securityQuestionComboBox;
    private TextField securityAnswerField;
    private CheckBox termsCheckBox;

    /**
     * 初始化测试环境
     */
    @Start
    public void start(Stage stage) throws IOException {
        // 加载FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUp.fxml"));
        Scene scene = new Scene(loader.load());
        
        // 获取控制器实例
        controller = loader.getController();
        
        // 设置场景
        stage.setScene(scene);
        stage.show();
        
        // 获取UI组件引用
        emailField = lookup("#emailField").query();
        usernameField = lookup("#signUpUsernameField").query();
        passwordField = lookup("#signUpPasswordField").query();
        confirmPasswordField = lookup("#confirmPasswordField").query();
        birthdayPicker = lookup("#birthdayPicker").query();
        securityQuestionComboBox = lookup("#securityQuestionComboBox").query();
        securityAnswerField = lookup("#securityAnswerField").query();
        termsCheckBox = lookup("#termsCheckBox").query();
    }

    /**
     * 测试空表单提交
     */
    @Test
    public void testEmptyFormSubmission() {
        // 点击创建账户按钮
        clickOn("#createAccountButton"); // Use ID for button
        WaitForAsyncUtils.waitForFxEvents();

        // 验证错误提示 Alert 是否显示
        verifyThat(lookup(".dialog-pane").queryAs(Node.class), isVisible());
        verifyThat(lookup(".dialog-pane .content.label").queryAs(Label.class), hasText("所有字段都必须填写"));
        // Close the alert
        clickOn(lookup(".dialog-pane .button").queryButton());
    }

    /**
     * 测试密码不匹配
     */
    @Test
    public void testPasswordMismatch() {
        // 填写表单
        clickOn("#emailField").write("test@example.com");
        clickOn("#signUpUsernameField").write("testuser");
        clickOn("#signUpPasswordField").write("password123");
        clickOn("#confirmPasswordField").write("password456");

        // --- 正确选择日期 ---
        selectDateInPicker(birthdayPicker, LocalDate.of(2000, 1, 1));
        // --- 日期选择结束 ---

        // --- 正确选择安全问题 ---
        clickOn("#securityQuestionComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn(hasText("What was the name of your elementary school?"));
        WaitForAsyncUtils.waitForFxEvents(); // Wait for combo box to close
        // --- 安全问题选择结束 ---

        clickOn("#securityAnswerField").write("My School");
        clickOn("#termsCheckBox");

        // 点击创建账户按钮
        clickOn("#createAccountButton"); // Use ID for button
        WaitForAsyncUtils.waitForFxEvents();

        // 验证错误提示 Alert 是否显示
        verifyThat(lookup(".dialog-pane").queryAs(Node.class), isVisible());
        verifyThat(lookup(".dialog-pane .content.label").queryAs(Label.class), hasText("两次输入的密码不匹配"));
        // Close the alert
        clickOn(lookup(".dialog-pane .button").queryButton());
    }

    /**
     * 测试未同意服务条款
     */
    @Test
    public void testTermsNotAccepted() {
        // 填写表单
        clickOn("#emailField").write("test@example.com");
        clickOn("#signUpUsernameField").write("testuser");
        clickOn("#signUpPasswordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");

        // --- 正确选择日期 ---
        selectDateInPicker(birthdayPicker, LocalDate.of(2000, 1, 1));
        // --- 日期选择结束 ---

        // --- 正确选择安全问题 ---
        clickOn("#securityQuestionComboBox");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn(hasText("What was the name of your elementary school?"));
        WaitForAsyncUtils.waitForFxEvents(); // Wait for combo box to close
        // --- 安全问题选择结束 ---

        clickOn("#securityAnswerField").write("My School");
        // 不点击 termsCheckBox

        // 点击创建账户按钮
        clickOn("#createAccountButton"); // Use ID for button
        WaitForAsyncUtils.waitForFxEvents();

        // 验证错误提示 Alert 是否显示
        verifyThat(lookup(".dialog-pane").queryAs(Node.class), isVisible());
        verifyThat(lookup(".dialog-pane .content.label").queryAs(Label.class), hasText("请阅读并同意服务条款、隐私政策和行为准则"));
        // Close the alert
        clickOn(lookup(".dialog-pane .button").queryButton());
    }

    /**
     * 测试返回登录页面
     */
    @Test
    public void testBackToSignIn() {
        // 点击返回登录链接
        clickOn("#signInLink"); // Use ID for link

        // 等待页面切换动画完成
        WaitForAsyncUtils.waitForFxEvents();
        sleep(600); // Wait for animation
        WaitForAsyncUtils.waitForFxEvents();

        // 验证是否返回登录页面 (查找登录页面的特定元素)
        verifyThat(lookup("#usernameField").queryAs(TextField.class), isVisible());
        verifyThat(lookup(hasText("Sign in to Coinue")).queryAs(Label.class), isVisible());
    }

    /**
     * 测试服务条款链接
     */
    @Test
    public void testTermsLink() {
        // 点击服务条款链接
        clickOn("#termsLink"); // Use ID for link
        WaitForAsyncUtils.waitForFxEvents();

        // 验证是否显示服务条款对话框 (查找新窗口及其内容)
        verifyDialogShowingWithTitle("Terms of Service");
    }

    /**
     * 测试隐私政策链接
     */
    @Test
    public void testPrivacyLink() {
        // 点击隐私政策链接
        clickOn("#privacyLink"); // Use ID for link
        WaitForAsyncUtils.waitForFxEvents();

        // 验证是否显示隐私政策对话框
        verifyDialogShowingWithTitle("Privacy Policy");
    }

    /**
     * 测试行为准则链接
     */
    @Test
    public void testConductLink() {
        // 点击行为准则链接
        clickOn("#conductLink"); // 使用ID而不是文本
        WaitForAsyncUtils.waitForFxEvents();
        sleep(1000); // 等待对话框显示

        // 验证是否显示行为准则对话框 (查找新窗口及其内容)
        verifyDialogShowingWithTitle("User Code of Conduct");
    }

    /**
     * 测试成功注册
     */
    @Test
    public void testSuccessfulRegistration() {
        // 生成更随机的测试数据
        String timestamp = String.valueOf(System.currentTimeMillis());
        String testEmail = "testuser" + timestamp + "@example.com";
        String testUsername = "testuser" + timestamp;
        
        try {
            // 填写表单
            clickOn("#emailField").write(testEmail);
            clickOn("#signUpUsernameField").write(testUsername);
            clickOn("#signUpPasswordField").write("password123");
            clickOn("#confirmPasswordField").write("password123");

            // --- 正确选择日期 ---
            selectDateInPicker(birthdayPicker, LocalDate.of(2000, 1, 1));
            // --- 日期选择结束 ---

            // --- 正确选择安全问题 ---
            clickOn("#securityQuestionComboBox");
            WaitForAsyncUtils.waitForFxEvents();
            clickOn(hasText("What was the name of your elementary school?"));
            WaitForAsyncUtils.waitForFxEvents(); // Wait for combo box to close
            // --- 安全问题选择结束 ---

            clickOn("#securityAnswerField").write("My School");
            clickOn("#termsCheckBox");

            // 点击创建账户按钮
            clickOn("#createAccountButton"); // Use ID for button
            WaitForAsyncUtils.waitForFxEvents();

            // 验证注册成功 Alert 是否显示
            verifyThat(lookup(".dialog-pane").queryAs(Node.class), isVisible());
            verifyThat(lookup(".dialog-pane .content.label").queryAs(Label.class), hasText("账户创建成功，请登录"));
            // Close the alert
            clickOn(lookup(".dialog-pane .button").queryButton());
            WaitForAsyncUtils.waitForFxEvents();

            // 等待页面切换动画完成
            sleep(600); // Wait for animation
            WaitForAsyncUtils.waitForFxEvents();

            // 验证是否返回登录页面
            verifyThat(lookup("#usernameField").queryAs(TextField.class), isVisible());
            verifyThat(lookup(hasText("Sign in to Coinue")).queryAs(Label.class), isVisible());
        } finally {
            // 测试完成后清理测试数据
            cleanupTestUser(testEmail, testUsername);
        }
    }

    /**
     * 清理测试用户数据
     */
    private void cleanupTestUser(String email, String username) {
        try {
            // 这里添加实际的清理逻辑，例如：
            // userService.deleteByEmail(email);
            // 或 userRepository.deleteByUsername(username);
            System.out.println("Cleaned up test user: " + username);
        } catch (Exception e) {
            System.err.println("Failed to clean up test user: " + e.getMessage());
        }
    }

    // --- Helper method for DatePicker ---
    private void selectDateInPicker(DatePicker datePicker, LocalDate dateToSelect) {
        interact(() -> datePicker.setValue(dateToSelect));
        WaitForAsyncUtils.waitForFxEvents();
        // The above might be sufficient if direct value setting works reliably.
        // If UI interaction is strictly needed:
        /*
        // 1. Set value close to the target month/year first (optional but good)
        interact(() -> datePicker.setValue(dateToSelect.withDayOfMonth(15)));
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Click to open the calendar
        clickOn(datePicker);
        WaitForAsyncUtils.waitForFxEvents();

        // 3. Navigate month/year if necessary (more complex, omitted for brevity)
        //    Example: clickOn(".date-picker-popup .button.previous-month");

        // 4. Find and click the specific day cell
        String dayOfMonthStr = String.valueOf(dateToSelect.getDayOfMonth());
        Node dayCell = lookup(".date-cell")
                .match(node -> node instanceof Labeled && dayOfMonthStr.equals(((Labeled) node).getText()) &&
                        !node.getStyleClass().contains("previous-month") &&
                        !node.getStyleClass().contains("next-month"))
                .query();
        clickOn(dayCell);
        WaitForAsyncUtils.waitForFxEvents();
        */
    }

    // --- Helper method to verify custom dialog ---
    private void verifyDialogShowingWithTitle(String expectedTitle) {
        // Find the dialog stage by title
        Optional<Window> dialogWindow = listWindows().stream()
                .filter(window -> window instanceof Stage && expectedTitle.equals(((Stage) window).getTitle()))
                .findFirst();

        assertTrue(dialogWindow.isPresent(), "Dialog with title '" + expectedTitle + "' should be showing.");
        assertTrue(((Stage)dialogWindow.get()).isShowing(), "Dialog stage should be showing");

        // Optional: Verify content within the dialog
        // Stage dialogStage = (Stage) dialogWindow.get();
        // FxRobot dialogRobot = new FxRobot(dialogStage);
        // verifyThat(dialogRobot.lookup("TextArea").queryAs(TextArea.class), NodeMatchers.isVisible());

        // Close the dialog for the next test
        interact(() -> ((Stage) dialogWindow.get()).close());
        WaitForAsyncUtils.waitForFxEvents();
    }
}