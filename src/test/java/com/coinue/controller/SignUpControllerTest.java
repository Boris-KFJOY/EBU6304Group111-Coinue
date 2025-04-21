package com.coinue.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDate;

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
        clickOn(hasText("Create Account"));
        
        // 验证错误提示是否显示
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", isVisible());
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
        clickOn("#birthdayPicker").write("2000-01-01");
        clickOn("#securityQuestionComboBox");
        clickOn(hasText("What was the name of your elementary school?"));
        clickOn("#securityAnswerField").write("My School");
        clickOn("#termsCheckBox");
        
        // 点击创建账户按钮
        clickOn(hasText("Create Account"));
        
        // 验证错误提示是否显示
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", isVisible());
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
        clickOn("#birthdayPicker").write("2000-01-01");
        clickOn("#securityQuestionComboBox");
        clickOn(hasText("What was the name of your elementary school?"));
        clickOn("#securityAnswerField").write("My School");
        
        // 点击创建账户按钮
        clickOn(hasText("Create Account"));
        
        // 验证错误提示是否显示
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", isVisible());
    }

    /**
     * 测试返回登录页面
     */
    @Test
    public void testBackToSignIn() {
        // 点击返回登录链接
        clickOn(hasText("Sign in"));
        
        // 等待页面切换完成
        sleep(1000);
        
        // 验证是否返回登录页面
        verifyThat("Sign in to Coinue", hasText("Sign in to Coinue"));
    }

    /**
     * 测试服务条款链接
     */
    @Test
    public void testTermsLink() {
        // 点击服务条款链接
        clickOn(hasText("Terms of Service"));
        
        // 验证是否显示服务条款对话框
        verifyThat(".dialog-pane", isVisible());
    }

    /**
     * 测试隐私政策链接
     */
    @Test
    public void testPrivacyLink() {
        // 点击隐私政策链接
        clickOn(hasText("Privacy Policy"));
        
        // 验证是否显示隐私政策对话框
        verifyThat(".dialog-pane", isVisible());
    }

    /**
     * 测试行为准则链接
     */
    @Test
    public void testConductLink() {
        // 点击行为准则链接
        clickOn(hasText("Code of Conduct"));
        
        // 验证是否显示行为准则对话框
        verifyThat(".dialog-pane", isVisible());
    }

    /**
     * 测试成功注册
     */
    @Test
    public void testSuccessfulRegistration() {
        // 填写表单
        clickOn("#emailField").write("test@example.com");
        clickOn("#signUpUsernameField").write("testuser");
        clickOn("#signUpPasswordField").write("password123");
        clickOn("#confirmPasswordField").write("password123");

        // --- 修改生日选择部分 ---
        // 1. (可选但推荐) 先用代码设置日期，让日历打开时接近目标
        interact(() -> birthdayPicker.setValue(LocalDate.of(2000, 1, 15))); // 设置到目标月份
        WaitForAsyncUtils.waitForFxEvents(); // 等待值设置生效

        // 2. 点击打开日历
        clickOn("#birthdayPicker");
        WaitForAsyncUtils.waitForFxEvents(); // 等待日历弹出

        // 3. (如果第1步没做或不够精确，需要导航年月 - 此处省略导航逻辑，假设已是2000年1月)
        //    示例：点击上一年按钮 (需要找到正确的选择器)
        //    clickOn(".date-picker-popup .button.previous-year");

        // 4. 定位并点击日期 "1"
        //    注意：这个查找器可能需要根据实际 FXML 结构调整
        Node dayCell = lookup(".date-cell")
            .match(node -> node instanceof Labeled && "1".equals(((Labeled) node).getText()) && !node.getStyleClass().contains("previous-month") && !node.getStyleClass().contains("next-month")) // 确保是当前月份的 "1"
            .query();
        clickOn(dayCell);
        WaitForAsyncUtils.waitForFxEvents(); // 等待选择生效，日历关闭
        // --- 生日选择部分结束 ---

        clickOn("#securityQuestionComboBox");
        // 需要确保下拉框选项可见后再点击
        WaitForAsyncUtils.waitForFxEvents();
        clickOn(hasText("What was the name of your elementary school?"));
        clickOn("#securityAnswerField").write("My School");
        clickOn("#termsCheckBox");

        // 点击创建账户按钮
        clickOn(hasText("Create Account"));

        // 等待页面切换完成 (使用更可靠的等待方式可能更好)
        // 例如，等待登录页面的某个特定元素出现
        WaitForAsyncUtils.waitForFxEvents(); // 基础等待
        sleep(1000); // 保留 sleep 作为后备，但尽量避免

        // 验证是否返回登录页面
        verifyThat("Sign in to Coinue", hasText("Sign in to Coinue"));
    }
}