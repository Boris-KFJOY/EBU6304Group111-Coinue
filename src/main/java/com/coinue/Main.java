package com.coinue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX应用程序的主类
 * 继承自Application类以实现JavaFX应用
 */
public class Main extends Application {

    /**
     * JavaFX应用程序的启动方法
     * @param primaryStage 主舞台，应用程序的主窗口
     * @throws IOException 当加载FXML文件失败时抛出异常
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // 加载财务分析界面的FXML文件
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
        // 加载FXML文件内容作为根节点
        Parent root = loader.load();
        // 设置窗口标题
        primaryStage.setTitle("Coinue - Financial Analysis");
        // 创建场景并设置根节点和大小
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        // 允许调整窗口大小
        primaryStage.setResizable(true);
        // 显示窗口
        primaryStage.show();
    }

    /**
     * 程序入口点
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动JavaFX应用程序
        launch(args);
    }
}