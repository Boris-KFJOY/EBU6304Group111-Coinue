package com.coinue;

import com.coinue.util.PageManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            Parent root = FXMLLoader.load(Main.class.getResource("/view/MainPage.fxml"));
            // 初始化页面管理器
            PageManager pageManager = PageManager.getInstance();
            pageManager.initStage(primaryStage);

            primaryStage.setTitle("Coinue");
            
            // 使用页面管理器加载初始页面
            pageManager.switchToPage("/view/MainPage.fxml");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}