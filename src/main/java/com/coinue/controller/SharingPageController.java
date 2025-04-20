package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert;
import java.io.IOException;

public class SharingPageController {
    @FXML
    private ListView<String> memberListView;
    @FXML
    private ToggleButton privacySwitch;

    @FXML
    public void initialize() {
        // 初始化共享页面组件
        memberListView.getItems().addAll("Member A", "Member B", "Member C", "Member D");
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
    private void handleAddMember() {
        // 处理添加成员
    }

    @FXML
    private void handleRemoveMember() {
        // 处理移除成员
    }

    @FXML
    private void handleRefresh() {
        // 处理刷新成员列表
    }

    @FXML
    private void handlePrivacySwitch() {
        // 处理隐私开关
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}