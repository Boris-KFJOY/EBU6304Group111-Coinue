package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * 用户页面控制器
 * 处理用户信息展示和相关功能的逻辑
 */
public class UserPageController {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;

    @FXML
    public void initialize() {
        // TODO: 从用户会话或数据库加载用户信息
        usernameLabel.setText("测试用户");
        emailLabel.setText("test@example.com");
    }

    /**
     * 处理修改密码按钮点击事件
     */
    @FXML
    private void handleChangePassword() {
        showInfo("功能提示", "密码修改功能正在开发中");
    }

    /**
     * 处理还款账单按钮点击事件
     */
    @FXML
    private void handleBillPayment() {
        try {
            // 使用页面管理器切换到还款账单页面
            PageManager.getInstance().switchToPage("/view/BillPaymentPage.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法加载还款账单页面：" + e.getMessage());
        }
    }

    /**
     * 处理导出数据按钮点击事件
     */
    @FXML
    private void handleExportData() {
        showInfo("功能提示", "数据导出功能正在开发中");
    }

    /**
     * 处理退出登录按钮点击事件
     */
    @FXML
    private void handleLogout() {
        try {
            // 使用页面管理器切换到登录页面
            PageManager.getInstance().switchToPage("/view/Register.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法返回登录页面：" + e.getMessage());
        }
    }

    /**
     * 处理导航到主页
     */
    @FXML
    private void handleHomeNav() {
        try {
            // 使用页面管理器切换到主页面
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法加载主页面：" + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        try {
            // 使用页面管理器切换到分析页面
            PageManager.getInstance().switchToPage("/view/AnalysisPage.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法加载分析页面：" + e.getMessage());
        }
    }

    @FXML
    private void handleUserNav() {
        // 已在用户页面，无需操作
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 显示信息提示对话框
     */
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}