package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import java.io.IOException;

public class SyncPageController {
    @FXML
    private Button syncButton;
    @FXML
    private Button bindButton;
    @FXML
    private TextField mailboxInput;
    @FXML
    private Label syncProgressLabel;
    @FXML
    private Label lastUpdateLabel;

    @FXML
    public void initialize() {
        // 初始化同步页面组件
        syncProgressLabel.setText("100%");
        lastUpdateLabel.setText("20XX.03.15 Updated");
    }

    @FXML
    private void handleHomeNav() {
        try {
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load main page: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        try {
            PageManager.getInstance().switchToPage("/view/AnalysisPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load analysis page: " + e.getMessage());
        }
    }

    @FXML
    private void handleUserNav() {
        try {
            PageManager.getInstance().switchToPage("/view/UserPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load user page: " + e.getMessage());
        }
    }

    @FXML
    private void handleSync() {
        // 处理同步操作
    }

    @FXML
    private void handleMailboxBind() {
        // 处理邮箱绑定
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}