package com.coinue.controller;

import com.coinue.model.User;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * SignUp页面的控制器类
 * 处理注册页面的超链接和安全问题选择
 */
public class SignUpController implements Initializable {

    // JavaFX注入的UI组件
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField signUpUsernameField;
    
    @FXML
    private PasswordField signUpPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private DatePicker birthdayPicker;
    
    @FXML
    private ComboBox<String> securityQuestionComboBox;
    
    @FXML
    private TextField securityAnswerField;
    
    @FXML
    private CheckBox termsCheckBox;
    
    @FXML
    private Hyperlink termsLink;
    
    @FXML
    private Hyperlink privacyLink;
    
    @FXML
    private Hyperlink conductLink;
    
    @FXML
    private Hyperlink signInLink;
    
    @FXML
    private Hyperlink backToRegisterLink;

    /**
     * 初始化方法，在FXML加载后自动调用
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化安全问题下拉框
        ObservableList<String> securityQuestions = FXCollections.observableArrayList(
                "What was the name of your elementary school?",
                "Where was your mother born?",
                "What's the name of your favorite book?",
                "What's the name of your best friend in childhood?"
        );
        securityQuestionComboBox.setItems(securityQuestions);
        
        // 设置超链接点击事件
        setupHyperlinks();
    }
    
    /**
     * 设置各个超链接的点击事件
     */
    private void setupHyperlinks() {
        termsLink.setOnAction(this::handleTermsLink);
        privacyLink.setOnAction(this::handlePrivacyLink);
        conductLink.setOnAction(this::handleConductLink);
    }

    /**
     * 处理服务条款链接点击事件
     */
    private void handleTermsLink(ActionEvent event) {
        System.out.println("用户点击了服务条款链接");
        openWebpage("https://www.coinue.com/terms"); // 示例URL，实际应替换为真实URL
    }

    /**
     * 处理隐私政策链接点击事件
     */
    private void handlePrivacyLink(ActionEvent event) {
        System.out.println("用户点击了隐私政策链接");
        openWebpage("https://www.coinue.com/privacy"); // 示例URL，实际应替换为真实URL
    }

    /**
     * 处理行为准则链接点击事件
     */
    private void handleConductLink(ActionEvent event) {
        System.out.println("用户点击了行为准则链接");
        openWebpage("https://www.coinue.com/conduct"); // 示例URL，实际应替换为真实URL
    }

    /**
     * 处理返回登录页面链接点击事件
     */
    @FXML
    private void handleBackToSignIn(ActionEvent event) {
        System.out.println("用户点击了返回登录链接");
        try {
            // 获取当前窗口和场景
            Stage stage = (Stage) signInLink.getScene().getWindow();
            Scene currentScene = stage.getScene();
            
            // 获取当前右侧面板（注册表单）
            VBox signUpForm = (VBox) signInLink.getParent().getParent().getParent();
            
            // 获取父级HBox容器（包含左侧和右侧部分）
            HBox mainContainer = (HBox) signUpForm.getParent();
            
            // 加载登录页面FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
            Parent loginPage = loader.load();
            
            // 从新加载的FXML中提取右侧表单部分
            HBox loginHBox = (HBox) ((AnchorPane) loginPage).getChildren().get(0);
            VBox loginForm = (VBox) loginHBox.getChildren().get(1);
            
            
            // 设置翻转动画
            // 1. 创建Y轴旋转
            RotateTransition rotateOut = new RotateTransition(Duration.millis(500), signUpForm);
            rotateOut.setAxis(Rotate.Y_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(-90);
            rotateOut.setInterpolator(Interpolator.EASE_IN);
            
            // 2. 创建淡出效果
            Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(500),
                    new KeyValue(signUpForm.opacityProperty(), 0.3, Interpolator.EASE_IN)
                )
            );
            
            // 3. 组合动画
            ParallelTransition parallelOut = new ParallelTransition(rotateOut, fadeOut);
            
            // 4. 设置动画完成后的操作
            parallelOut.setOnFinished(e -> {
                // 更新窗口标题
                stage.setTitle("Coinue - 登录");
                
                // 从父容器中移除当前注册表单
                mainContainer.getChildren().remove(signUpForm);
                
                // 将新的登录表单添加到父容器中
                mainContainer.getChildren().add(loginForm);
                
                // 设置初始状态
                loginForm.setRotate(-90);
                loginForm.setOpacity(0.3);
                
                // 创建进入动画
                RotateTransition rotateIn = new RotateTransition(Duration.millis(500), loginForm);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(-90);
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
     * 处理创建账户按钮点击事件
     */
    @FXML
    private void handleCreateAccount(ActionEvent event) {
        System.out.println("用户点击了创建账户按钮");
        
        // 获取用户输入
        String username = signUpUsernameField.getText();
        String email = emailField.getText();
        String password = signUpPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        LocalDate birthday = birthdayPicker.getValue();
        String securityQuestion = securityQuestionComboBox.getValue();
        String securityAnswer = securityAnswerField.getText();
        
        // 验证用户输入
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            birthday == null || securityQuestion == null || securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "输入错误", "所有字段都必须填写");
            return;
        }
        
        // 验证密码匹配
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "密码错误", "两次输入的密码不匹配");
            return;
        }
        
        // 验证服务条款是否同意
        if (!termsCheckBox.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "条款未接受", "请阅读并同意服务条款、隐私政策和行为准则");
            return;
        }
        
        // 创建用户对象
        User user = new User(username, email, password, securityQuestion, securityAnswer, birthday);
        
        // 保存用户数据
        boolean success = com.coinue.util.UserDataManager.getInstance().createUser(user);
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "注册成功", "账户创建成功，请登录");
            // 注册成功后返回登录页面
            handleBackToSignIn(new ActionEvent());
        } else {
            showAlert(Alert.AlertType.ERROR, "注册失败", "用户名或邮箱可能已存在，请更换后重试");
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

   
    /**
     * 打开网页的辅助方法
     */
    private void openWebpage(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            System.err.println("无法打开网页: " + e.getMessage());
        }
    }
}