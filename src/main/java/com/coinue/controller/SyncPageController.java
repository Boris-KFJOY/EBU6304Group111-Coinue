package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.IOException;

public class SyncPageController {
    
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
    private void handleSyncNav() {
        // 已在同步页面，无需操作
    }

    @FXML
    private void handleSharingNav() {
        try {
            PageManager.getInstance().switchToPage("/view/SharingPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load sharing page: " + e.getMessage());
        }
    }

    @FXML
    private void handleEncryptionNav() {
        try {
            PageManager.getInstance().switchToPage("/view/EncryptionPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load encryption page: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}