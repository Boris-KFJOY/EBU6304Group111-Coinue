package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import java.io.IOException;

public class EncryptionPageController {
    @FXML
    private ListView<String> encryptionListView;

    @FXML
    public void initialize() {
        // 初始化加密页面组件
        encryptionListView.getItems().addAll(
            "Local data encryption",
            "Local data encryption",
            "Local data encryption",
            "Local data encryption"
        );
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

    private void showError(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleEncryption(String item) {
        // 处理加密/解密操作
    }
}