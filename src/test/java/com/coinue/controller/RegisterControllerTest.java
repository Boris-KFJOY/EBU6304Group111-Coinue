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
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
// import java.util.concurrent.TimeUnit; // No longer needed for sleep
// import java.util.concurrent.TimeoutException; // No longer needed for sleep

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.hamcrest.Matchers.equalTo;
// import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.testfx.api.FxToolkit.showStage; // For waitUntilVisible if needed or other utilities
// Correct import for waitUntilVisible

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

    // private RegisterController controller; // 控制器实例通常不需要直接在测试中引用
    // private TextField usernameField; // 可以通过lookup直接操作
    // private PasswordField passwordField; // 可以通过lookup直接操作
    private Stage primaryStage;

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
        this.primaryStage = stage;
        // 加载FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
        Scene scene = new Scene(loader.load());
        
        // 获取控制器实例 - 如果需要直接调用控制器方法，可以保留
        // controller = loader.getController(); 
        
        // 设置场景
        stage.setScene(scene);
        stage.setTitle("Coinue - 登录"); // 初始标题，与RegisterController中一致
        stage.show();
        
        // 获取UI组件引用 - TestFX推荐在测试方法中按需lookup
        // usernameField = lookup("#usernameField").query();
        // passwordField = lookup("#passwordField").query();
    }

    /**
     * 测试空用户名和密码的登录尝试
     * 
     * 测试场景：
     * 1. 清空用户名和密码输入框 (由于是新启动，默认是空的，但显式清空更保险)
     * 2. 点击登录按钮
     * 
     * 验证点：
     * 1. 错误提示对话框是否显示
     * 2. 对话框内容是否为"用户名/邮箱或密码不正确，请重试"
     */
    @Test
    public void testEmptyCredentials() {
        // 清空输入框
        clickOn("#usernameField").write("");
        clickOn("#passwordField").write("");
        
        // 点击登录按钮 - 使用ID
        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents(); // 等待UI更新
        
        // 验证错误提示是否显示
        verifyThat(".dialog-pane", isVisible());
        // 验证对话框内容 - 从RegisterController的showAlert方法获取准确的错误信息
        verifyThat(lookup(".dialog-pane .content.label").queryAs(javafx.scene.control.Label.class), hasText("用户名/邮箱或密码不正确，请重试"));
        // 关闭对话框以便后续测试
        clickOn(lookup(".dialog-pane .button").queryButton());
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * 测试正确的登录信息
     * 
     * 测试场景：
     * 1. 输入有效的用户名("Test")和密码("Test1234")
     * 2. 点击登录按钮
     * 
     * 验证点：
     * 1. 页面是否成功跳转到主页
     * 2. 主页标题是否正确显示("Coinue - 主页")
     * 3. 主页上是否存在expenseTableView
     */
    @Test
    public void testValidCredentials() {
        // 输入有效的用户名和密码
        clickOn("#usernameField").write("Test"); // 假设Test用户存在
        clickOn("#passwordField").write("Test1234"); // 使用正确的密码
        
        // 点击登录按钮
        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents(); // 等待页面切换等异步操作
        
        // 验证是否成功跳转到主页面
        // 1. 验证舞台标题
        verifyThat(primaryStage.getTitle(), equalTo("Coinue - 主页"));
        // 2. 验证主页面上的特定元素是否存在
        verifyThat("#expenseTableView", isVisible()); // 假设MainPage.fxml中有 fx:id="expenseTableView"
    }

    /**
     * 测试忘记密码功能
     * 
     * 测试场景：
     * 1. 点击"Forgot?"链接
     * 
     * 验证点：
     * 1. 页面是否成功切换到找回密码表单
     * 2. 舞台标题是否正确显示("Coinue - 找回密码")
     * 3. 忘记密码表单中的usernameOrEmailField是否可见
     */
    @Test
    public void testForgotPassword() /*throws TimeoutException*/ {
        // 点击忘记密码链接 (fx:id="forgotPasswordLink")
        clickOn("#forgotPasswordLink");
        // 等待动画完成以及新表单中的元素可见 - 使用sleep进行临时诊断
        sleep(2000); // Wait for 2 seconds
        WaitForAsyncUtils.waitForFxEvents(); // 确保所有FX事件处理完毕
        
        // 验证是否跳转到找回密码页面
        // 1. 验证舞台标题
        verifyThat(primaryStage.getTitle(), equalTo("Coinue - 找回密码"));
        // 2. 验证新表单中的特定元素
        verifyThat("#usernameOrEmailField", isVisible()); // Forget.fxml中的元素
    }

    /**
     * 测试注册功能
     * 
     * 测试场景：
     * 1. 点击"Sign up here"链接
     * 
     * 验证点：
     * 1. 页面是否成功切换到注册表单
     * 2. 注册页面标题是否正确显示("Coinue - 注册")
     * 3. 注册表单中的emailField是否可见
     */
    @Test
    public void testSignUp() /*throws TimeoutException*/ {
        // 点击注册链接 (fx:id="signUpHereLink")
        clickOn("#signUpHereLink");
        // 等待动画完成以及新表单中的元素可见 - 使用sleep进行临时诊断
        sleep(2000); // Wait for 2 seconds
        WaitForAsyncUtils.waitForFxEvents(); // 确保所有FX事件处理完毕
        
        // 验证是否跳转到注册页面
        // 1. 验证舞台标题
        verifyThat(primaryStage.getTitle(), equalTo("Coinue - 注册"));
        // 2. 验证新表单中的特定元素
        verifyThat("#emailField", isVisible()); // SignUp.fxml中的元素
    }

    /**
     * 测试下一页功能
     * 
     * 测试场景：
     * 1. 点击"Next page"链接 (fx:id="signUpLink" 但实际调用nextPage)
     * 
     * 验证点：
     * 1. 页面是否成功跳转到主页面
     * 2. 主页标题是否正确显示("Coinue - 主页")
     * 3. 主页上是否存在expenseTableView
     */
    @Test
    public void testNextPage() {
        // 点击下一页链接 (ID是signUpLink，但关联到nextPage方法)
        clickOn("#signUpLink"); // Register.fxml中此链接的fx:id是signUpLink，文本是"Next page"
        WaitForAsyncUtils.waitForFxEvents(); // 等待页面切换
        
        // 验证是否跳转到主页面
        // 1. 验证舞台标题
        verifyThat(primaryStage.getTitle(), equalTo("Coinue - 主页"));
        // 2. 验证主页面上的特定元素
        verifyThat("#expenseTableView", isVisible());
    }
}