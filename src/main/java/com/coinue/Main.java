package com.coinue;

import com.coinue.model.User;
import com.coinue.util.PageManager;
import javafx.application.Application;
import javafx.stage.Stage;
import java.time.LocalDate;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 初始化页面管理器
            PageManager.getInstance().initStage(primaryStage);
            
            // 自动登录指定用户
            autoLoginTestUser();
            
            // 直接跳转到主页面
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
            
            primaryStage.setTitle("Coinue - 主页");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("应用启动失败: " + e.getMessage());
        }
    }
    
    /**
     * 自动登录测试用户
     */
    private void autoLoginTestUser() {
        try {
            // 创建指定的测试用户对象
            User testUser = new User();
            testUser.setUsername("Test");
            testUser.setEmail("1@q.com");
            testUser.setPassword("Test1234");
            testUser.setSecurityQuestion("Where was your mother born?");
            testUser.setSecurityAnswer("1");
            testUser.setBirthday(LocalDate.of(2004, 1, 1));
            
            // 设置为当前登录用户
            User.setCurrentUser(testUser);
            
            System.out.println("自动登录成功: " + testUser.getUsername() + " (" + testUser.getEmail() + ")");
            
        } catch (Exception e) {
            System.err.println("自动登录失败: " + e.getMessage());
            // 如果自动登录失败，可以考虑跳转到登录页面
            try {
                PageManager.getInstance().switchToPage("/view/Register.fxml");
            } catch (Exception ex) {
                System.err.println("跳转到登录页面失败: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}