package com.coinue.controller;

// 导入必要的类
import com.coinue.model.User;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;

/**
 * 忘记密码控制器类
 * 负责处理用户忘记密码相关的界面交互逻辑
 */
public class ForgetController {

    // JavaFX注入的UI组件
    @FXML
    private TextField usernameOrEmailField;      // 用户名/邮箱输入框

    @FXML
    private DatePicker birthdayPicker;          // 生日选择器

    @FXML
    private Label securityQuestionLabel;        // 安全问题显示标签

    @FXML
    private TextField securityAnswerField;       // 安全问题答案输入框

    @FXML
    private PasswordField newPasswordField;      // 新密码输入框

    @FXML
    private PasswordField confirmPasswordField;  // 确认新密码输入框

    @FXML
    private Hyperlink backToLoginLink;          // 返回登录链接

    // 用户对象，用于存储查询到的用户信息
    private User user;

    /**
     * 初始化方法，在FXML加载后自动调用
     */
    @FXML
    private void initialize() {
        // 添加用户名/邮箱输入框的焦点监听器，当失去焦点时尝试加载安全问题
        usernameOrEmailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !usernameOrEmailField.getText().isEmpty()) {
                loadSecurityQuestion();
            }
        });
    }

    /**
     * 加载用户的安全问题
     */
    private void loadSecurityQuestion() {
        String usernameOrEmail = usernameOrEmailField.getText().trim();
        if (!usernameOrEmail.isEmpty()) {
            // 通过用户名或邮箱查询用户
            user = com.coinue.util.UserDataManager.getInstance().findUserByUsernameOrEmail(usernameOrEmail);
            if (user != null) {
                // 显示用户的安全问题
                securityQuestionLabel.setText(user.getSecurityQuestion());
            } else {
                securityQuestionLabel.setText("未找到用户信息");
            }
        }
    }

    /**
     * 处理重置密码事件
     * @param event 事件对象
     */
    @FXML
    private void handleResetPassword(ActionEvent event) {
        // 获取输入信息
        String usernameOrEmail = usernameOrEmailField.getText().trim();
        LocalDate birthday = birthdayPicker.getValue();
        String securityAnswer = securityAnswerField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 验证输入
        if (usernameOrEmail.isEmpty() || birthday == null || securityAnswer.isEmpty() 
                || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "输入错误", "所有字段都必须填写");
            return;
        }

        // 验证两次密码输入是否一致
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "密码错误", "两次输入的密码不一致");
            return;
        }

        // 验证用户信息
        if (user == null) {
            loadSecurityQuestion(); // 尝试再次加载用户信息
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "用户错误", "未找到用户信息");
                return;
            }
        }

        // 验证生日
        if (!user.getBirthday().equals(birthday)) {
            showAlert(Alert.AlertType.ERROR, "验证失败", "生日信息不匹配");
            return;
        }

        // 重置密码
        boolean success = com.coinue.util.UserDataManager.getInstance().resetPassword(
                usernameOrEmail, securityAnswer, newPassword);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "密码重置成功", "您的密码已重置，请使用新密码登录");
            // 重置成功后返回登录页面
            handleBackToLogin(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "密码重置失败", "安全问题答案不正确");
        }
    }

    /**
     * 处理返回登录页面事件
     * @param event 事件对象
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            // 获取当前窗口和场景
            Stage stage = (Stage) backToLoginLink.getScene().getWindow();
            
            // 获取当前右侧面板（忘记密码表单）
            VBox forgetForm = (VBox) backToLoginLink.getParent().getParent().getParent();
            
            // 获取父级HBox容器（包含左侧和右侧部分）
            HBox mainContainer = (HBox) forgetForm.getParent();
            
            // 加载登录页面FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
            Parent loginPage = loader.load();
            
            // 从新加载的FXML中提取右侧表单部分
            HBox loginHBox = (HBox) ((AnchorPane) loginPage).getChildren().get(0);
            VBox loginForm = (VBox) loginHBox.getChildren().get(1);
            
            // 设置翻转动画
            // 1. 创建Y轴旋转
            RotateTransition rotateOut = new RotateTransition(Duration.millis(500), forgetForm);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);
            rotateOut.setInterpolator(Interpolator.EASE_IN);
            
            // 2. 创建淡出效果
            Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(500),
                    new KeyValue(forgetForm.opacityProperty(), 0.3, Interpolator.EASE_IN)
                )
            );
            
            // 3. 组合动画
            ParallelTransition parallelOut = new ParallelTransition(rotateOut, fadeOut);
            
            // 4. 设置动画完成后的操作
            parallelOut.setOnFinished(e -> {
                // 更新窗口标题
                stage.setTitle("Coinue - 登录");
                
                // 从父容器中移除当前忘记密码表单
                mainContainer.getChildren().remove(forgetForm);
                
                // 将新的登录表单添加到父容器中
                mainContainer.getChildren().add(loginForm);
                
                // 设置初始状态
                loginForm.setRotate(90);
                loginForm.setOpacity(0.3);
                
                // 创建进入动画
                RotateTransition rotateIn = new RotateTransition(Duration.millis(500), loginForm);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(90);
                rotateIn.setToAngle(0);
                rotateIn.setInterpolator(Interpolator.EASE_OUT);
                
                Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.millis(500),
                        new KeyValue(loginForm.opacityProperty(), 1, Interpolator.EASE_OUT)
                    )
                );
                
                ParallelTransition parallelIn = new ParallelTransition(rotateIn, fadeIn);
                parallelIn.play();
            });
            
            // 开始动画
            parallelOut.play();
            
        } catch (IOException e) {
            System.err.println("无法加载登录页面: " + e.getMessage());
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