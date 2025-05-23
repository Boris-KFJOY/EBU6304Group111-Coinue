package com.coinue;

import com.coinue.util.PageManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 将 initialize 改为 initStage
            PageManager.getInstance().initStage(primaryStage);
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
            
            primaryStage.setTitle("Coinue");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}