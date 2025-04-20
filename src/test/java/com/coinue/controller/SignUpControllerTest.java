package com.coinue.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDate;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

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
        clickOn("#birthdayPicker").write("2000-01-01");
        clickOn("#securityQuestionComboBox");
        clickOn(hasText("What was the name of your elementary school?"));
        clickOn("#securityAnswerField").write("My School");
        clickOn("#termsCheckBox");
        
        // 点击创建账户按钮
        clickOn(hasText("Create Account"));
        
        // 等待页面切换完成
        sleep(1000);
        
        // 验证是否返回登录页面
        verifyThat("Sign in to Coinue", hasText("Sign in to Coinue"));
    }
}