package com.coinue.controller;

// 导入必要的类
import com.coinue.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * 注册控制器类
 * 负责处理用户注册相关的界面交互逻辑
 */
public class RegisterController {

    // JavaFX注入的UI组件
    @FXML
    private TextField usernameField;      // 用户名输入框

    @FXML
    private PasswordField passwordField;   // 密码输入框

    @FXML
    private Hyperlink forgotPasswordLink; // 忘记密码链接

    @FXML
    private Hyperlink signUpLink;         // 注册链接

    /**
     * 处理用户登录事件
     * @param event 事件对象
     */
    @FXML
    private void handleSignIn(ActionEvent event) {
        String usernameOrEmail = usernameField.getText();
        String password = passwordField.getText();
        
        // 这里应该添加验证逻辑
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            System.out.println("用户名/邮箱和密码不能为空");
            return;
        }
        
        // 创建用户对象
        User user = new User();
        if (usernameOrEmail.contains("@")) {
            user.setEmail(usernameOrEmail);
        } else {
            user.setUsername(usernameOrEmail);
        }
        user.setPassword(password);
        
        // 这里应该添加登录逻辑
        System.out.println("用户尝试登录: " + usernameOrEmail);
    }

    /**
     * 处理忘记密码事件
     * @param event 事件对象
     */
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        System.out.println("用户点击了忘记密码");
        // 这里应该添加忘记密码的逻辑
    }

    /**
     * 处理注册事件
     * @param event 事件对象
     */
    @FXML
    private void handleSignUp(ActionEvent event) {
        System.out.println("用户点击了注册链接");
        // 这里应该添加跳转到注册页面的逻辑
    }
}