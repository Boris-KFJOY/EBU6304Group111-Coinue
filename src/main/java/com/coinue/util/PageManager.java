package com.coinue.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class PageManager {
    private static PageManager instance;
    private Stage primaryStage;
    private Map<String, Parent> pages;
    private double width = 800;
    private double height = 600;
    private final double minWidth = 800;
    private final double minHeight = 600;

    private PageManager() {
        pages = new HashMap<>();
    }

    public static PageManager getInstance() {
        if (instance == null) {
            instance = new PageManager();
        }
        return instance;
    }

    public void initStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setMinWidth(minWidth);
        this.primaryStage.setMinHeight(minHeight);
        try {
            initializePages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToPage(String fxmlPath) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not initialized. Call initStage() first.");
        }

        // 保存当前窗口大小
        if (primaryStage.getScene() != null) {
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();
        }

        // 加载新页面
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        // 设置场景并保持窗口大小
        primaryStage.setScene(scene);
        double width = primaryStage.getWidth();
        primaryStage.setWidth(width);
        double height = primaryStage.getHeight();
        primaryStage.setHeight(height);
    }

    public void switchToPage(String fxmlPath, ControllerInitializer controllerInitializer) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not initialized. Call initStage() first.");
        }

        // 保存当前窗口大小
        if (primaryStage.getScene() != null) {
            double width = primaryStage.getWidth();
            double height = primaryStage.getHeight();
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
        // 使用之前保存的窗口宽度
        if (primaryStage.getScene() != null) {
            primaryStage.setWidth(primaryStage.getWidth());
        }
        primaryStage.setHeight(primaryStage.getHeight());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public interface ControllerInitializer {
        void initializeController(Object controller);
    }

    public void initializePages() throws IOException {
        try {
            pages.put("/view/SyncPage.fxml", 
                FXMLLoader.load(getClass().getResource("/view/SyncPage.fxml")));
            pages.put("/view/SharingPage.fxml", 
                FXMLLoader.load(getClass().getResource("/view/SharingPage.fxml")));
            pages.put("/view/EncryptionPage.fxml", 
                FXMLLoader.load(getClass().getResource("/view/EncryptionPage.fxml")));
        } catch (IOException e) {
            throw new IOException("Failed to initialize pages: " + e.getMessage());
        }
    }
}