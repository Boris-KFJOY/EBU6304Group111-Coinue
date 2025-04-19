package com.coinue.controller;

import com.coinue.model.PaymentReminder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ReminderDialogController {
    @FXML
    private TextField platformField;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker dueDatePicker;
    
    @FXML
    private ImageView rentIcon;
    @FXML
    private ImageView creditCardIcon;
    @FXML
    private ImageView expressIcon;
    @FXML
    private ImageView shoppingIcon;

    private Stage dialogStage;
    private MainPageController mainPageController;
    private boolean okClicked = false;
    private String selectedIconPath = "/images/icons/credit_card.png"; // 默认图标

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
            
            // 创建新的还款提醒对象，包含选择的图标
            PaymentReminder reminder = new PaymentReminder(platform, amount, dueDatePicker.getValue(), selectedIconPath);
            
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
    
    @FXML
    private void selectRentIcon() {
        selectedIconPath = "/images/icons/rent.png";
        highlightSelectedIcon(rentIcon);
    }
    
    @FXML
    private void selectCreditCardIcon() {
        selectedIconPath = "/images/icons/credit_card.png";
        highlightSelectedIcon(creditCardIcon);
    }
    
    @FXML
    private void selectExpressIcon() {
        selectedIconPath = "/images/icons/express.png";
        highlightSelectedIcon(expressIcon);
    }
    
    @FXML
    private void selectShoppingIcon() {
        selectedIconPath = "/images/icons/shopping.png";
        highlightSelectedIcon(shoppingIcon);
    }
    
    private void highlightSelectedIcon(ImageView selectedIcon) {
        // 重置所有图标的样式
        rentIcon.setOpacity(0.5);
        creditCardIcon.setOpacity(0.5);
        expressIcon.setOpacity(0.5);
        shoppingIcon.setOpacity(0.5);
        
        // 高亮选中的图标
        selectedIcon.setOpacity(1.0);
    }
    
    @FXML
    public void initialize() {
        // 默认选中信用卡图标
        highlightSelectedIcon(creditCardIcon);
    }
}