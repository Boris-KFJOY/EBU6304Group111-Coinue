package com.coinue.controller;

// import com.coinue.model.User;
// import com.coinue.util.PageManager;
// import com.coinue.util.UserDataManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
// import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

/**
 * RegisterController测试类
 * 使用TestFX框架进行集成测试，验证注册控制器的各项功能
 * 
 * 测试内容包括：
 * 1. 空用户名和密码的登录尝试
 * 2. 有效登录信息的处理
 * 3. 忘记密码功能
 * 4. 注册功能
 * 5. 页面导航功能
 * 
 * 测试方法使用TestFX提供的API模拟用户操作，验证UI响应和页面跳转
 */
public class RegisterControllerTest extends ApplicationTest {

    private RegisterController controller;
    private TextField usernameField;
    private PasswordField passwordField;

    /**
     * 初始化测试环境
     * 
     * 在每个测试方法执行前调用，用于：
     * 1. 加载Register.fxml界面文件
     * 2. 获取RegisterController实例
     * 3. 设置JavaFX舞台和场景
     * 4. 获取用户名和密码输入框的引用
     * 
     * @param stage JavaFX主舞台
     * @throws IOException 如果FXML文件加载失败
     */
    @Start
    public void start(Stage stage) throws IOException {
        // 加载FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
        Scene scene = new Scene(loader.load());
        
        // 获取控制器实例
        controller = loader.getController();
        
        // 设置场景
        stage.setScene(scene);
        stage.show();
        
        // 获取UI组件引用
        usernameField = lookup("#usernameField").query();
        passwordField = lookup("#passwordField").query();
    }

    /**
     * 测试空用户名和密码的登录尝试
     * 
     * 测试场景：
     * 1. 清空用户名和密码输入框
     * 2. 点击登录按钮
     * 
     * 验证点：
     * 1. 错误提示对话框是否显示
     * 2. 对话框内容是否可见
     */
    @Test
    public void testEmptyCredentials() {
        // 清空输入框
        clickOn("#usernameField").write("");
        clickOn("#passwordField").write("");
        
        // 点击登录按钮
        clickOn(hasText("Sign in"));
        
        // 验证错误提示是否显示
        verifyThat(".dialog-pane", isVisible());
        verifyThat(".dialog-pane .content", isVisible());
    }

    /**
     * 测试正确的登录信息
     * 
     * 测试场景：
     * 1. 输入有效的用户名("Test")和密码("Test123")
     * 2. 点击登录按钮
     * 
     * 验证点：
     * 1. 页面是否成功跳转到主页
     * 2. 主页标题是否正确显示("Homepage")
     */
    @Test
    public void testValidCredentials() {
        // 输入有效的用户名和密码
        clickOn("#usernameField").write("Test");
        clickOn("#passwordField").write("Test123");
        
        // 点击登录按钮
        clickOn(hasText("Sign in"));
        
        // 等待页面切换完成
        sleep(1000);
        
        // 验证是否成功跳转到主页面
        verifyThat("Homepage", hasText("Homepage"));
    }

    /**
     * 测试忘记密码功能
     * 
     * 测试场景：
     * 1. 点击"Forgot?"链接
     * 
     * 验证点：
     * 1. 页面是否成功跳转到找回密码页面
     * 2. 找回密码页面标题是否正确显示("Reset your password")
     */
    @Test
    public void testForgotPassword() {
        // 点击忘记密码链接
        clickOn(hasText("Forgot?"));
        
        // 等待页面切换完成
        sleep(1000);
        
        // 验证是否跳转到找回密码页面
        verifyThat("Reset your password", hasText("Reset your password"));
    }

    /**
     * 测试注册功能
     * 
     * 测试场景：
     * 1. 点击"Sign up here"链接
     * 
     * 验证点：
     * 1. 页面是否成功跳转到注册页面
     * 2. 注册页面标题是否正确显示("Create your account")
     */
    @Test
    public void testSignUp() {
        // 点击注册链接
        clickOn(hasText("Sign up here"));
        
        // 等待页面切换完成
        sleep(1000);
        
        // 验证是否跳转到注册页面
        verifyThat("Create your account", hasText("Create your account"));
    }

    /**
     * 测试下一页功能
     * 
     * 测试场景：
     * 1. 点击"Next page"链接
     * 
     * 验证点：
     * 1. 页面是否成功跳转到主页面
     * 2. 主页标题是否正确显示("Homepage")
     */
    @Test
    public void testNextPage() {
        // 点击下一页链接
        clickOn(hasText("Next page"));
        
        // 等待页面切换完成
        sleep(1000);
        
        // 验证是否跳转到主页面
        verifyThat("Homepage", hasText("Homepage"));
    }
}