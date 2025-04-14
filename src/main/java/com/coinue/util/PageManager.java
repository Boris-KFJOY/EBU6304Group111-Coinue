package com.coinue.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 页面管理类，用于处理页面切换时保持窗口大小一致并设置最小窗口限制
 */
public class PageManager {
    private static PageManager instance;
    private Stage primaryStage;
    private double width = 800;
    private double height = 600;
    private final double minWidth = 800;
    private final double minHeight = 600;

    /**
     * 私有构造函数，确保单例模式
     */
    private PageManager() {
    }

    /**
     * 获取PageManager的单例实例
     *
     * @return PageManager实例
     */
    public static PageManager getInstance() {
        if (instance == null) {
            instance = new PageManager();
        }
        return instance;
    }

    /**
     * 初始化Stage
     *
     * @param stage 主舞台
     */
    public void initStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setMinWidth(minWidth);
        this.primaryStage.setMinHeight(minHeight);
    }

    /**
     * 切换到指定页面
     *
     * @param fxmlPath FXML文件路径
     * @throws IOException 如果加载FXML文件失败
     */
    public void switchToPage(String fxmlPath) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not initialized. Call initStage() first.");
        }

        // 保存当前窗口大小
        if (primaryStage.getScene() != null) {
            width = primaryStage.getWidth();
            height = primaryStage.getHeight();
        }

        // 加载新页面
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        // 设置场景并保持窗口大小
        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }

    /**
     * 切换到指定页面并传递控制器
     *
     * @param fxmlPath FXML文件路径
     * @param controllerInitializer 控制器初始化接口
     * @throws IOException 如果加载FXML文件失败
     */
    public void switchToPage(String fxmlPath, ControllerInitializer controllerInitializer) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not initialized. Call initStage() first.");
        }

        // 保存当前窗口大小
        if (primaryStage.getScene() != null) {
            width = primaryStage.getWidth();
            height = primaryStage.getHeight();
        }

        // 加载新页面
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // 初始化控制器
        Object controller = loader.getController();
        controllerInitializer.initializeController(controller);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        // 设置场景并保持窗口大小
        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }

    /**
     * 获取当前Stage
     *
     * @return 当前Stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * 控制器初始化接口
     */
    public interface ControllerInitializer {
        /**
         * 初始化控制器
         *
         * @param controller 控制器对象
         */
        void initializeController(Object controller);
    }
}