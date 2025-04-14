package com.coinue.controller;

import com.coinue.model.PaymentReminder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ReminderDialogController {
    @FXML
    private TextField platformField;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker dueDatePicker;

    private Stage dialogStage;
    private MainPageController mainPageController;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            String platform = platformField.getText();
            double amount = Double.parseDouble(amountField.getText());
            
            // 创建新的还款提醒对象
            PaymentReminder reminder = new PaymentReminder(platform, amount, dueDatePicker.getValue());
            
            // 添加到主页面
            mainPageController.addReminder(reminder);
            
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

        if (platformField.getText() == null || platformField.getText().trim().isEmpty()) {
            errorMessage += "请输入平台名称！\n";
        }

        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
            errorMessage += "请输入金额！\n";
        } else {
            try {
                Double.parseDouble(amountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "金额格式无效！\n";
            }
        }

        if (dueDatePicker.getValue() == null) {
            errorMessage += "请选择到期日期！\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
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