package com.coinue.controller;

import com.coinue.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink signUpLink;

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

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        System.out.println("用户点击了忘记密码");
        // 这里应该添加忘记密码的逻辑
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        System.out.println("用户点击了注册链接");
        // 这里应该添加跳转到注册页面的逻辑
    }
}