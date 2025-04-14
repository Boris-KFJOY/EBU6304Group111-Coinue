package com.coinue.controller;

import com.coinue.model.Budget;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BudgetDialogController {
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> currencyComboBox;

    private Stage dialogStage;
    private MainPageController mainPageController;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        // 初始化类别选项
        categoryComboBox.getItems().addAll(
            "购物",
            "餐饮",
            "交通",
            "娱乐",
            "其他"
        );

        // 初始化货币选项
        currencyComboBox.getItems().addAll(
            "RMB",
            "USD",
            "EUR"
        );
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            String category = categoryComboBox.getValue();
            double amount = Double.parseDouble(amountField.getText());
            String currency = currencyComboBox.getValue();

            // 创建新的预算对象
            Budget budget = new Budget(category, amount, currency);
            
            // 添加到主页面
            mainPageController.addBudget(budget);
            
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().isEmpty()) {
            errorMessage += "请选择类别！\n";
        }

        if (amountField.getText() == null || amountField.getText().isEmpty()) {
            errorMessage += "请输入金额！\n";
        } else {
            try {
                Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "金额格式无效！\n";
            }
        }

        if (currencyComboBox.getValue() == null || currencyComboBox.getValue().isEmpty()) {
            errorMessage += "请选择货币！\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // 显示错误消息
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("输入错误");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }
}