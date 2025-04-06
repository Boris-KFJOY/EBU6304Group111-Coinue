package com.coinue.controller;

// 导入必要的类
import com.coinue.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
        
        // 登录成功后跳转到主页
        try {
            // 加载主页面FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
            Parent mainPage = loader.load();
            
            // 获取当前窗口
            Stage stage = (Stage) usernameField.getScene().getWindow();
            
            // 创建新场景并设置
            Scene scene = new Scene(mainPage);
            stage.setTitle("Coinue - 主页");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("无法加载主页面: " + e.getMessage());
            e.printStackTrace();
        }
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
        try {
            // 加载主页面FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
            Parent mainPage = loader.load();
            
            // 获取当前窗口
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            
            // 创建新场景并设置
            Scene scene = new Scene(mainPage);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("无法加载主页面: " + e.getMessage());
            e.printStackTrace();
        }
    }
}