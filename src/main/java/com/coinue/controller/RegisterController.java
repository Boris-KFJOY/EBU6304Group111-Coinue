package com.coinue.controller;

// 导入必要的类
import com.coinue.model.User;
import com.coinue.util.PageManager;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;

import javafx.stage.Stage;

import javafx.util.Duration;

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
        
        // 验证输入
        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "登录错误", "用户名/邮箱和密码不能为空");
            return;
        }
        
        // 验证用户登录
        User user = com.coinue.util.UserDataManager.getInstance().validateLogin(usernameOrEmail, password);
        
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "登录失败", "用户名/邮箱或密码不正确");
            return;
        }
        
        // 登录成功
        System.out.println("用户登录成功: " + usernameOrEmail);
        
        // 登录成功后跳转到主页
        try {
            // 获取当前窗口
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Coinue - 主页");
            
            // 使用页面管理器切换到主页
            PageManager.getInstance().initStage(stage);
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
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
        try {
            // 获取当前窗口和场景
            Stage stage = (Stage) forgotPasswordLink.getScene().getWindow();
 
            
            // 获取当前右侧面板（登录表单）
            VBox loginForm = (VBox)forgotPasswordLink.getParent().getParent().getParent().getParent().getParent();
            
            // 获取父级HBox容器（包含左侧和右侧部分）
            HBox MainContainer = (HBox) loginForm.getParent();
            
            // 加载忘记密码页面FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Forget.fxml"));
            Parent forgetPage = loader.load();
            
            // 从新加载的FXML中提取右侧表单部分
            HBox forgetHBox = (HBox) ((AnchorPane) forgetPage).getChildren().get(0);
            VBox forgetForm = (VBox) forgetHBox.getChildren().get(1);
            
            // 设置翻转动画
            // 1. 创建Y轴旋转
            RotateTransition rotateOut = new RotateTransition(Duration.millis(500), loginForm);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);
            rotateOut.setInterpolator(Interpolator.EASE_IN);
            
            // 2. 创建淡出效果
            Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(500),
                    new KeyValue(loginForm.opacityProperty(), 0.3, Interpolator.EASE_IN)
                )
            );
            
            // 3. 组合动画
            ParallelTransition parallelOut = new ParallelTransition(rotateOut, fadeOut);
            
            // 4. 设置动画完成后的操作
            parallelOut.setOnFinished(e -> {
                // 更新窗口标题
                stage.setTitle("Coinue - 找回密码");
                
                // 从父容器中移除当前登录表单
                MainContainer.getChildren().remove(loginForm);
                
                // 将新的忘记密码表单添加到父容器中
                MainContainer.getChildren().add(forgetForm);
                
                // 设置初始状态
                forgetForm.setRotate(90);
                forgetForm.setOpacity(0.3);
                
                // 创建进入动画
                RotateTransition rotateIn = new RotateTransition(Duration.millis(500), forgetForm);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(90);
                rotateIn.setToAngle(0);
                rotateIn.setInterpolator(Interpolator.EASE_OUT);
                
                Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.millis(500),
                        new KeyValue(forgetForm.opacityProperty(), 1, Interpolator.EASE_OUT)
                    )
                );
                
                ParallelTransition parallelIn = new ParallelTransition(rotateIn, fadeIn);
                parallelIn.play();
            });
            
            // 开始动画
            parallelOut.play();
            
        } catch (IOException e) {
            System.err.println("无法加载忘记密码页面: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理注册事件
     * @param event 事件对象
     */
    @FXML
    private void handleSignUp(ActionEvent event) {
        System.out.println("用户点击了注册链接");
        try {
            // 获取当前窗口和场景
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            
            // 获取当前右侧面板（登录表单）
            VBox loginForm = (VBox) signUpLink.getParent().getParent().getParent();
            
            // 获取父级HBox容器（包含左侧和右侧部分）
            HBox mainContainer = (HBox) loginForm.getParent();
            
            // 加载注册页面FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUp.fxml"));
            Parent signUpPage = loader.load();
            
            // 从新加载的FXML中提取右侧表单部分
            HBox signUpHBox = (HBox) ((AnchorPane) signUpPage).getChildren().get(0);
            VBox signUpForm = (VBox) signUpHBox.getChildren().get(1);
            
            // 设置翻转动画
            // 1. 创建Y轴旋转
            RotateTransition rotateOut = new RotateTransition(Duration.millis(500), loginForm);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);
            rotateOut.setInterpolator(Interpolator.EASE_IN);
            
            // 2. 创建淡出效果
            Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(500),
                    new KeyValue(loginForm.opacityProperty(), 0.3, Interpolator.EASE_IN)
                )
            );
            
            // 3. 组合动画
            ParallelTransition parallelOut = new ParallelTransition(rotateOut, fadeOut);
            
            // 4. 设置动画完成后的操作
            parallelOut.setOnFinished(e -> {
                // 更新窗口标题
                stage.setTitle("Coinue - 注册");
                
                // 从父容器中移除当前登录表单
                mainContainer.getChildren().remove(loginForm);
                
                // 将新的注册表单添加到父容器中
                mainContainer.getChildren().add(signUpForm);
                
                // 设置初始状态
                signUpForm.setRotate(90);
                signUpForm.setOpacity(0.3);
                
                // 创建进入动画
                RotateTransition rotateIn = new RotateTransition(Duration.millis(500), signUpForm);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(90);
                rotateIn.setToAngle(0);
                rotateIn.setInterpolator(Interpolator.EASE_OUT);
                
                Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.millis(500),
                        new KeyValue(signUpForm.opacityProperty(), 1, Interpolator.EASE_OUT)
                    )
                );
                
                ParallelTransition parallelIn = new ParallelTransition(rotateIn, fadeIn);
                parallelIn.play();
            });
            
            // 开始动画
            parallelOut.play();
            
        } catch (IOException e) {
            System.err.println("无法加载注册页面: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理下一页事件
     * @param event 事件对象
     */
    @FXML
    private void nextPage(ActionEvent event) {
        try {
            // 获取当前窗口
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            
            // 使用页面管理器切换到主页
            PageManager.getInstance().initStage(stage);
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            System.err.println("无法加载主页面: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 显示提示对话框
     * @param alertType 对话框类型
     * @param title 标题
     * @param content 内容
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}