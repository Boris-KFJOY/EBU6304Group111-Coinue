package com.coinue.controller;

import com.coinue.model.ExpenseRecord;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * 手动输入消费记录对话框控制器
 * 处理手动输入消费记录的表单逻辑
 */
public class ManualEntryDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private DatePicker datePicker;

    private Stage dialogStage;
    private MainPageController mainPageController;
    private boolean isConfirmed = false;

    @FXML
    public void initialize() {
        // 初始化消费类别下拉框
        categoryComboBox.setItems(FXCollections.observableArrayList(
                Arrays.asList("餐饮", "交通", "购物", "娱乐", "医疗", "教育", "其他")
        ));

        // 设置当前日期为默认值
        datePicker.setValue(LocalDate.now());

        // 添加金额输入验证
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                amountField.setText(oldValue);
            }
        });
    }

    /**
     * 设置对话框窗口
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * 设置主页面控制器引用
     */
    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    /**
     * 处理保存按钮点击事件
     */
    @FXML
    private void handleSave() {
        if (isInputValid()) {
            ExpenseRecord record = new ExpenseRecord(
                    Double.parseDouble(amountField.getText()),
                    categoryComboBox.getValue(),
                    nameField.getText(),
                    datePicker.getValue()
            );

            mainPageController.addExpenseRecord(record);
            isConfirmed = true;
            dialogStage.close();
        }
    }

    /**
     * 处理取消按钮点击事件
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * 验证输入数据的有效性
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "请输入消费名称！\n";
        }

        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
            errorMessage += "请输入消费金额！\n";
        } else {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    errorMessage += "金额必须大于0！\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "金额格式无效！\n";
            }
        }

        if (categoryComboBox.getValue() == null) {
            errorMessage += "请选择消费类别！\n";
        }

        if (datePicker.getValue() == null) {
            errorMessage += "请选择消费日期！\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("输入错误");
            alert.setHeaderText("请修正以下输入错误");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    /**
     * 返回用户是否确认保存
     */
    public boolean isConfirmed() {
        return isConfirmed;
    }
}