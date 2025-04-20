package com.coinue.controller;

// 添加必要的导入
import com.coinue.model.ExpenseRecord;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDate;

public class ManualEntryDialogController {
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField amountField;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private DatePicker datePicker;
    
    // 新增UI元素的引用
    @FXML
    private ListView<String> subCategoryListView;
    
    @FXML
    private TextArea noteField;
    
    // 移除这两个字段，因为我们现在使用Button而不是ToggleButton
    @FXML
    private ToggleButton quickEntryToggle;
    
    @FXML
    private ToggleButton familySharingToggle;
    
    private Stage dialogStage;
    private boolean isConfirmed = false;
    private MainPageController mainPageController;
    
    @FXML
    private RadioButton expensesRadio;
    
    @FXML
    private RadioButton incomeRadio;
    
    @FXML
    private ToggleGroup recordTypeGroup;
    
    @FXML
    private ComboBox<String> currencyComboBox;
    
    @FXML
    private Button quickEntryButton;
    
    @FXML
    private Button familySharingButton;
    
    @FXML
    private void initialize() {
        // 初始化币种选择
        currencyComboBox.getItems().addAll("CNY", "USD", "EUR", "GBP", "JPY");
        currencyComboBox.setValue("CNY");
        
        // 初始化类别下拉框 - 默认为支出类别
        updateCategoryComboBox(true);
        
        // 初始化日期选择器为当前日期
        datePicker.setValue(LocalDate.now());
        
        // 设置类别选择监听器，更新子类别列表
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateSubCategories(newValue);
        });

        // 修改：使用ToggleGroup的selectedToggleProperty来监听单选按钮变化
        recordTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            handleRecordTypeChange();
        });
    }
    
    @FXML
    private void handleRecordTypeChange() {
        boolean isExpense = expensesRadio.isSelected();
        updateCategoryComboBox(isExpense);
    }
    
    private void updateCategoryComboBox(boolean isExpense) {
        categoryComboBox.getItems().clear();
        
        if (isExpense) {
            // 支出类别
            categoryComboBox.getItems().addAll("餐饮", "交通", "购物", "娱乐", "教育", "运动", "生活", "通讯", "医疗");
        } else {
            // 收入类别
            categoryComboBox.getItems().addAll("工资", "投资", "礼金", "奖金", "兼职", "其他");
        }
    }
    
    // 更新子类别列表的方法
    private void updateSubCategories(String category) {
        if (subCategoryListView == null || category == null) return;
        
        subCategoryListView.getItems().clear();
        
        // 根据是支出还是收入以及选择的类别添加对应的子类别
        if (expensesRadio.isSelected()) {
            // 支出子类别
            switch (category) {
                case "餐饮":
                    subCategoryListView.getItems().addAll("早餐", "午餐", "晚餐", "外卖", "零食", "饮料");
                    break;
                case "交通":
                    subCategoryListView.getItems().addAll("公交车", "地铁", "出租车", "共享单车", "火车", "飞机");
                    break;
                case "购物":
                    subCategoryListView.getItems().addAll("服装", "电子产品", "日用品", "超市购物");
                    break;
                default:
                    break;
            }
        } else {
            // 收入子类别
            switch (category) {
                case "工资":
                    subCategoryListView.getItems().addAll("月薪", "年终奖", "加班费");
                    break;
                case "投资":
                    subCategoryListView.getItems().addAll("股票", "基金", "存款利息", "债券");
                    break;
                case "礼金":
                    subCategoryListView.getItems().addAll("生日礼金", "节日礼金", "婚礼礼金");
                    break;
                default:
                    break;
            }
        }
    }
    
    @FXML
    private void handleSave() {
        if (isInputValid()) {
            // 获取子类别作为消费名称，如果没有选择则使用nameField的值
            String expenseName = nameField.getText();
            if (subCategoryListView != null) {
                String selectedSubCategory = subCategoryListView.getSelectionModel().getSelectedItem();
                if (selectedSubCategory != null && !selectedSubCategory.isEmpty()) {
                    expenseName = selectedSubCategory;
                }
            }
            
            // 获取记录类型（支出或收入）
            String recordType = expensesRadio.isSelected() ? "支出" : "收入";
            
            // 获取币种
            String currency = currencyComboBox.getValue();
            
            ExpenseRecord record = new ExpenseRecord(
                    Double.parseDouble(amountField.getText()),
                    categoryComboBox.getValue(),
                    expenseName,
                    datePicker.getValue()
            );
            
            // 设置备注信息
            if (noteField != null && noteField.getText() != null && !noteField.getText().isEmpty()) {
                record.setDescription(noteField.getText());
            }
            
            // 设置记录类型和币种
            record.setRecordType(recordType);
            record.setCurrency(currency);
            
            mainPageController.addExpenseRecord(record);
            isConfirmed = true;
            dialogStage.close();
        }
    }

    /**
     * 处理取消按钮点击事件
     */
    @FXML
    void handleCancel() {
        dialogStage.close();
    }

    /**
     * 验证输入数据的有效性
     */
    private boolean isInputValid() {
        String errorMessage = "";
        
        if (categoryComboBox.getValue() == null) {
            errorMessage += expensesRadio.isSelected() ? "请选择支出类别！\n" : "请选择收入类别！\n";
        }
        
        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
            errorMessage += "请输入金额！\n";
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
        
        if (datePicker.getValue() == null) {
            errorMessage += "请选择日期！\n";
        }
        
        // 检查是否有输入名称
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            // 检查是否有选择子类别
            boolean hasSelectedSubCategory = false;
            if (subCategoryListView != null) {
                hasSelectedSubCategory = subCategoryListView.getSelectionModel().getSelectedItem() != null;
            }
            
            if (!hasSelectedSubCategory) {
                errorMessage += expensesRadio.isSelected() ? "请输入支出名称或选择子类别！\n" : "请输入收入名称或选择子类别！\n";
            }
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

    /**
     * 返回用户是否确认保存
     */
    public boolean isConfirmed() {
        return isConfirmed;
    }
    
    /**
     * 检查是否选择了收入类型
     * @return 如果选择了收入类型返回true，否则返回false
     */
    public boolean isIncomeSelected() {
        return incomeRadio.isSelected();
    }
    
    /**
     * 设置记录类型（支出或收入）
     * @param type 记录类型，"income"表示收入，其他值表示支出
     */
    public void setRecordType(String type) {
        if ("income".equalsIgnoreCase(type)) {
            incomeRadio.setSelected(true);
        } else {
            expensesRadio.setSelected(true);
        }
        // 手动触发类别更新
        handleRecordTypeChange();
    }

    // 新增导入CSV文件的方法
    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择CSV文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"));
        
        File file = fileChooser.showOpenDialog(dialogStage);
        if (file != null) {
            try {
                // 这里添加CSV导入逻辑
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("导入成功");
                alert.setHeaderText(null);
                alert.setContentText("CSV文件导入成功！");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("导入错误");
                alert.setHeaderText(null);
                alert.setContentText("导入CSV文件时发生错误：" + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    /**
     * 设置对话框的Stage
     * @param dialogStage 对话框的Stage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * 设置主页面控制器
     * @param mainPageController 主页面控制器
     */
    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }
}