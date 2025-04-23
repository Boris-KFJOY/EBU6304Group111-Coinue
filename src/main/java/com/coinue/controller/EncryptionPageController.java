package com.coinue.controller;

import java.io.IOException;

import com.coinue.util.PageManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class EncryptionPageController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private ImageView avatarImage;
    @FXML
    private VBox contentArea;

    @FXML
    public void initialize() {
        // TODO: 从用户会话或数据库加载用户信息
        usernameLabel.setText("测试用户");
        emailLabel.setText("test@example.com");
        
        // Ensure content area is visible
        if (contentArea != null) {
            contentArea.setVisible(true);
        }
        
        // Initialize avatar image if needed
        if (avatarImage != null) {
            // Set a default avatar or load from user profile
            avatarImage.setFitWidth(40);
            avatarImage.setFitHeight(40);
        }
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
    private void handleSyncNav() {
        try {
            PageManager.getInstance().switchToPage("/view/SyncPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load sync page: " + e.getMessage());
        }
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
        // 已在加密页面，无需操作
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleChangeAvatar() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("选择头像图片");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg")
        );
        
        java.io.File selectedFile = fileChooser.showOpenDialog(avatarImage.getScene().getWindow());
        if (selectedFile != null) {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(selectedFile.toURI().toString());
                avatarImage.setImage(image);
                // TODO: 保存头像到用户配置或数据库
            } catch (Exception e) {
                // 显示错误提示
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText("无法加载图片");
                alert.setContentText("请选择有效的图片文件。");
                alert.showAndWait();
            }
        }
    }
}