package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 基础控制器类
 */
public abstract class BaseController implements Initializable {
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 子类可以重写此方法进行初始化
    }
    
    /**
     * 显示错误消息
     * @param message 错误消息
     */
    protected void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示信息消息
     * @param message 信息消息
     */
    protected void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("信息");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示警告消息
     * @param message 警告消息
     */
    protected void showWarning(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 导航到指定页面
     * @param fxmlPath FXML路径
     */
    protected void navigateTo(String fxmlPath) {
        try {
            PageManager.getInstance().switchToPage(fxmlPath);
        } catch (IOException e) {
            showError("导航失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理异常
     * @param e 异常
     */
    protected void handleException(Exception e) {
        e.printStackTrace();
        showError("发生错误: " + e.getMessage());
    }
} 