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
        // 初始化类别选项，与手动输入保持一致
        categoryComboBox.getItems().addAll(
            "食品",
            "购物",
            "交通",
            "娱乐",
            "教育",
            "医疗",
            "住房",
            "其他"
        );

        // 初始化货币选项
        currencyComboBox.getItems().addAll(
            "CNY",
            "USD",
            "EUR"
        );

        // 设置默认值
        categoryComboBox.setValue("食品");
        currencyComboBox.setValue("CNY");
    }

    // 获取类别对应的图标路径
    public static String getCategoryIconPath(String category) {
        switch (category.toLowerCase()) {
            case "食品":
                return "/images/icons/food.png";
            case "购物":
                return "/images/icons/shopping.png";
            case "交通":
                return "/images/icons/transport.png";
            case "娱乐":
                return "/images/icons/entertainment.png";
            case "教育":
                return "/images/icons/education.png";
            case "医疗":
                return "/images/icons/medical.png";
            case "住房":
                return "/images/icons/house.png";
            default:
                return "/images/icons/other.png";
        }
    }

    // 获取类别对应的背景颜色
    public static String getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "食品":
                return "linear-gradient(to right, #FFE0B2, #FFB74D)";
            case "购物":
                return "linear-gradient(to right, #B3E5FC, #4FC3F7)";
            case "交通":
                return "linear-gradient(to right, #C8E6C9, #81C784)";
            case "娱乐":
                return "linear-gradient(to right, #F8BBD0, #F06292)";
            case "教育":
                return "linear-gradient(to right, #D1C4E9, #9575CD)";
            case "医疗":
                return "linear-gradient(to right, #B2EBF2, #4DD0E1)";
            case "住房":
                return "linear-gradient(to right, #FFCCBC, #FF8A65)";
            default:
                return "linear-gradient(to right, #CFD8DC, #90A4AE)";
        }
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