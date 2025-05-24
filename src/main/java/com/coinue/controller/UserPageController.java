package com.coinue.controller;

import java.io.IOException;

import com.coinue.model.User;
import com.coinue.util.PageManager;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;

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
    private ImageView avatarImage;
    @FXML
    private VBox contentArea;

    @FXML
    public void initialize() {
        // 从当前登录用户加载用户信息
        User currentUser = User.getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
        } else {
            usernameLabel.setText("未登录用户");
            emailLabel.setText("请先登录");
        }
        
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

    /**
     * 处理修改密码按钮点击事件
     */
    @FXML
    private void handleChangePassword() {
        User currentUser = User.getCurrentUser();
        if (currentUser == null) {
            showError("错误", "请先登录后再修改密码");
            return;
        }

        // 创建密码修改对话框
        Dialog<Pair<String, String>> dialog = createChangePasswordDialog();
        
        // 处理对话框结果
        dialog.showAndWait().ifPresent(result -> {
            String currentPassword = result.getKey();
            String newPassword = result.getValue();
            
            // 验证当前密码
            if (!currentUser.getPassword().equals(currentPassword)) {
                showError("验证失败", "当前密码不正确，请重新输入");
                return;
            }
            
            // 验证新密码强度
            String passwordValidation = User.validatePasswordStrength(newPassword);
            if (passwordValidation != null) {
                showError("密码不符合要求", passwordValidation);
                return;
            }
            
            // 检查新密码是否与当前密码相同
            if (currentPassword.equals(newPassword)) {
                showError("密码重复", "新密码不能与当前密码相同");
                return;
            }
            
            // 更新密码
            currentUser.setPassword(newPassword);
            boolean success = currentUser.save();
            
            if (success) {
                showInfo("修改成功", "密码已成功修改！");
            } else {
                showError("修改失败", "密码修改失败，请稍后重试");
            }
        });
    }

    /**
     * 创建密码修改对话框
     * @return 密码修改对话框
     */
    private Dialog<Pair<String, String>> createChangePasswordDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("修改密码");
        dialog.setHeaderText("请输入当前密码和新密码");

        // 设置按钮类型
        ButtonType confirmButtonType = new ButtonType("确认修改", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // 创建密码输入控件
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("当前密码");
        currentPassword.setPrefWidth(200);
        
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("新密码");
        newPassword.setPrefWidth(200);
        
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("确认新密码");
        confirmPassword.setPrefWidth(200);

        // 添加密码强度提示标签
        Label passwordHint = new Label("密码要求：至少6位，包含字母和数字");
        passwordHint.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        grid.add(new Label("当前密码:"), 0, 0);
        grid.add(currentPassword, 1, 0);
        grid.add(new Label("新密码:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("确认新密码:"), 0, 2);
        grid.add(confirmPassword, 1, 2);
        grid.add(passwordHint, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // 获取确认按钮
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        // 添加输入验证
        Runnable checkInput = () -> {
            boolean allFieldsFilled = !currentPassword.getText().trim().isEmpty() &&
                                    !newPassword.getText().trim().isEmpty() &&
                                    !confirmPassword.getText().trim().isEmpty();
            
            boolean passwordsMatch = newPassword.getText().equals(confirmPassword.getText());
            
            confirmButton.setDisable(!allFieldsFilled || !passwordsMatch);
            
            // 实时显示密码匹配状态
            if (!newPassword.getText().isEmpty() && !confirmPassword.getText().isEmpty()) {
                if (passwordsMatch) {
                    passwordHint.setText("密码要求：至少6位，包含字母和数字 ✓");
                    passwordHint.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px;");
                } else {
                    passwordHint.setText("两次输入的密码不一致");
                    passwordHint.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12px;");
                }
            } else {
                passwordHint.setText("密码要求：至少6位，包含字母和数字");
                passwordHint.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
            }
        };

        // 为所有密码字段添加监听器
        currentPassword.textProperty().addListener((observable, oldValue, newValue) -> checkInput.run());
        newPassword.textProperty().addListener((observable, oldValue, newValue) -> checkInput.run());
        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> checkInput.run());

        // 设置初始焦点
        javafx.application.Platform.runLater(() -> currentPassword.requestFocus());

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return new Pair<>(currentPassword.getText(), newPassword.getText());
            }
            return null;
        });

        return dialog;
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
            showError("Navigation Failed", "Failed to load bill payment page: " + e.getMessage());
        }
    }

    /**
     * 处理导出数据按钮点击事件
     */
    @FXML
    private void handleExportData() {
        User currentUser = User.getCurrentUser();
        if (currentUser == null) {
            showError("用户未登录", "请先登录后再导出数据");
            return;
        }

        // 创建数据导出选择对话框
        Dialog<String> dialog = createExportDataDialog();
        
        dialog.showAndWait().ifPresent(exportType -> {
            try {
                com.coinue.model.UserDataExportService exportService = 
                    com.coinue.model.UserDataExportService.getInstance();
                String exportPath = null;
                
                switch (exportType) {
                    case "complete":
                        exportPath = exportService.exportUserCompleteData(currentUser);
                        break;
                    case "bill":
                        exportPath = exportService.exportUserBillDataOnly(currentUser);
                        break;
                    case "analysis":
                        exportPath = exportService.exportUserAnalysisDataOnly(currentUser);
                        break;
                }
                
                if (exportPath != null) {
                    showExportSuccessDialog(exportPath);
                } else {
                    showError("导出失败", "数据导出过程中发生错误，请检查控制台日志");
                }
                
            } catch (Exception e) {
                showError("导出失败", "数据导出失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理退出登录按钮点击事件
     */
    @FXML
    private void handleLogout() {
        try {
            // 显示确认对话框
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("确认退出");
            confirmAlert.setHeaderText("退出登录");
            confirmAlert.setContentText("您确定要退出当前用户登录吗？");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    performLogout();
                }
            });
        } catch (Exception e) {
            showError("退出失败", "退出登录时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 执行退出登录操作
     */
    private void performLogout() {
        try {
            // 获取当前用户信息用于日志记录
            User currentUser = User.getCurrentUser();
            String username = currentUser != null ? currentUser.getUsername() : "未知用户";
            
            // 清除当前用户会话
            User.logout();
            
            // 清理可能的临时数据或缓存
            clearUserCache();
            
            // 显示退出成功提示
            showInfo("退出成功", "用户 " + username + " 已成功退出登录");
            
            // 跳转到登录页面
            PageManager.getInstance().switchToPage("/view/Register.fxml");
            
            // 更新窗口标题
            if (PageManager.getInstance().getPrimaryStage() != null) {
                PageManager.getInstance().getPrimaryStage().setTitle("Coinue - 登录");
            }
            
            System.out.println("用户 " + username + " 已退出登录");
            
        } catch (IOException e) {
            showError("页面跳转失败", "无法返回登录页面: " + e.getMessage());
        } catch (Exception e) {
            showError("退出失败", "退出登录时发生未知错误: " + e.getMessage());
        }
    }
    
    /**
     * 清理用户相关的缓存数据
     */
    private void clearUserCache() {
        try {
            // 这里可以添加清理用户相关缓存的逻辑
            // 例如：清理临时文件、重置UI状态等
            
            // 重置用户信息显示
            usernameLabel.setText("未登录用户");
            emailLabel.setText("请先登录");
            
            // 如果有头像图片，可以重置为默认状态
            if (avatarImage != null) {
                avatarImage.setImage(null);
            }
            
            System.out.println("用户缓存数据已清理");
            
        } catch (Exception e) {
            System.err.println("清理用户缓存时出错: " + e.getMessage());
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
            showError("Navigation Failed", "Failed to load main page: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        try {
            // 使用页面管理器切换到分析页面
            PageManager.getInstance().switchToPage("/view/AnalysisPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load analysis page: " + e.getMessage());
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
        alert.setTitle("Notice");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 创建数据导出选择对话框
     * @return 导出类型选择对话框
     */
    private Dialog<String> createExportDataDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("数据导出");
        dialog.setHeaderText("选择要导出的数据类型");

        // 设置按钮类型
        ButtonType exportButtonType = new ButtonType("导出", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CANCEL);

        // 创建选择控件
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 20, 10, 20));

        Label instructionLabel = new Label("请选择要导出的数据类型:");
        instructionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ToggleGroup exportGroup = new ToggleGroup();

        RadioButton completeDataRadio = new RadioButton("完整数据导出");
        completeDataRadio.setToggleGroup(exportGroup);
        completeDataRadio.setSelected(true); // 默认选择完整导出
        completeDataRadio.setUserData("complete");
        Label completeDesc = new Label("包含用户基本信息、账单数据、分析数据、支出记录等所有数据");
        completeDesc.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-padding: 0 0 0 25px;");

        RadioButton billDataRadio = new RadioButton("仅账单数据");
        billDataRadio.setToggleGroup(exportGroup);
        billDataRadio.setUserData("bill");
        Label billDesc = new Label("仅导出账单支付相关数据和信用额度信息");
        billDesc.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-padding: 0 0 0 25px;");

        RadioButton analysisDataRadio = new RadioButton("仅分析数据");
        analysisDataRadio.setToggleGroup(exportGroup);
        analysisDataRadio.setUserData("analysis");
        Label analysisDesc = new Label("仅导出财务分析数据和类别统计信息");
        analysisDesc.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px; -fx-padding: 0 0 0 25px;");

        content.getChildren().addAll(
            instructionLabel,
            completeDataRadio, completeDesc,
            billDataRadio, billDesc,
            analysisDataRadio, analysisDesc
        );

        dialog.getDialogPane().setContent(content);

        // 设置初始焦点
        javafx.application.Platform.runLater(() -> completeDataRadio.requestFocus());

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == exportButtonType) {
                RadioButton selectedRadio = (RadioButton) exportGroup.getSelectedToggle();
                return selectedRadio != null ? (String) selectedRadio.getUserData() : "complete";
            }
            return null;
        });

        return dialog;
    }

    /**
     * 显示导出成功对话框
     * @param exportPath 导出文件路径
     */
    private void showExportSuccessDialog(String exportPath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("导出成功");
        alert.setHeaderText("数据导出完成");
        
        // 创建内容
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label messageLabel = new Label("您的数据已成功导出到以下位置:");
        messageLabel.setStyle("-fx-font-weight: bold;");
        
        TextField pathField = new TextField(exportPath);
        pathField.setEditable(false);
        pathField.setPrefWidth(400);
        pathField.setStyle("-fx-background-color: #f5f5f5;");
        
        // 添加复制路径和打开文件夹按钮
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button copyPathButton = new Button("📋 复制路径");
        copyPathButton.setOnAction(e -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
            clipboardContent.putString(exportPath);
            clipboard.setContent(clipboardContent);
            showInfo("已复制", "文件路径已复制到剪贴板");
        });
        
        Button openFolderButton = new Button("📂 打开文件夹");
        openFolderButton.setOnAction(e -> {
            try {
                java.io.File file = new java.io.File(exportPath);
                java.io.File parentDir = file.getParentFile();
                if (parentDir != null && parentDir.exists()) {
                    java.awt.Desktop.getDesktop().open(parentDir);
                }
            } catch (Exception ex) {
                showError("打开失败", "无法打开文件夹: " + ex.getMessage());
            }
        });
        
        buttonBox.getChildren().addAll(copyPathButton, openFolderButton);
        
        content.getChildren().addAll(messageLabel, pathField, buttonBox);
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    /**
     * 处理同步功能区导航
     */
    @FXML
    private void handleSyncNav() {
        try {
            PageManager.getInstance().switchToPage("/view/SyncPage.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Failed", 
                "Failed to load synchronisation page. Please check if the page exists and try again.");
        }
    }

    @FXML
    private void handleSharingNav() {
        try {
            PageManager.getInstance().switchToPage("/view/SharingPage.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Failed", 
                "Failed to load sharing page. Please check if the page exists and try again.");
        }
    }

    /**
     * 处理加密功能区导航
     */
    @FXML
    private void handleEncryptionNav() {
        try {
            PageManager.getInstance().switchToPage("/view/EncryptionPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load encryption page: " + e.getMessage());
        }
    }
    
    /**
     * 处理导入账单按钮点击事件
     */
    @FXML
    private void handleImportBill() {
        showInfo("Feature Notice", "Bill import feature is under development.");
    }

    /**
     * Handle username edit button click event
     */
    @FXML
    private void handleUsernameEdit() {
        showInfo("Feature Notice", "Username edit feature is under development.");
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText("无法加载图片");
                alert.setContentText("请选择有效的图片文件。");
                alert.showAndWait();
            }
        }
    }
}