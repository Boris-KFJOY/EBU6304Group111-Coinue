package com.coinue;

import com.coinue.util.PageManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 应用程序主入口类
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // 初始化Stage
            PageManager.getInstance().initStage(primaryStage);
            
            // 切换到登录页面
            PageManager.getInstance().switchToPage("/view/LoginPage.fxml");
            
            // 设置标题并显示
            primaryStage.setTitle("Coinue - 个人财务管理系统");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}