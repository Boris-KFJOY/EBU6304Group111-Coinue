package com.coinue.controller;

// 添加必要的导入
// Add necessary imports
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import com.coinue.model.ExpenseRecord;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 手动记账对话框控制器
 * Manual Entry Dialog Controller
 * 负责处理手动记账界面的所有交互逻辑，包括收支记录的输入、验证和保存
 * Responsible for handling all interaction logic of the manual entry interface, including input, validation, and saving of income and expense records
 */
public class ManualEntryDialogController {
    /**
     * 收支名称输入框
     * Income/Expense name input field
     */
    @FXML
    private TextField nameField;
    
    /**
     * 金额输入框
     * Amount input field
     */
    @FXML
    private TextField amountField;
    
    /**
     * 类别选择下拉框
     * Category selection combo box
     */
    @FXML
    private ComboBox<String> categoryComboBox;
    
    /**
     * 日期选择器
     * Date picker
     */
    @FXML
    private DatePicker datePicker;
    
    /**
     * 子类别列表视图
     * Subcategory list view
     * 显示所选类别下的具体子类别选项
     * Displays specific subcategory options under the selected category
     */
    @FXML
    private ListView<String> subCategoryListView;
    
    /**
     * 备注输入区域
     * Note input area
     */
    @FXML
    private TextArea noteField;
    
    /**
     * 支出单选按钮
     * Expense radio button
     */
    @FXML
    private RadioButton expensesRadio;
    
    /**
     * 收入单选按钮
     * Income radio button
     */
    @FXML
    private RadioButton incomeRadio;
    
    /**
     * 收支类型单选按钮组
     * Income/Expense type radio button group
     */
    @FXML
    private ToggleGroup recordTypeGroup;
    
    /**
     * 币种选择下拉框
     * Currency selection combo box
     */
    @FXML
    private ComboBox<String> currencyComboBox;
    
    /**
     * 快速记账按钮
     * Quick entry button
     */
    @FXML
    private Button quickEntryButton;
    
    /**
     * 家庭共享按钮
     * Family sharing button
     */
    @FXML
    private Button familySharingButton;
    
    /**
     * 对话框窗口引用
     * Reference to the dialog window
     */
    private Stage dialogStage;
    
    /**
     * 用户是否确认保存的标志
     * Flag indicating whether the user has confirmed saving
     */
    private boolean isConfirmed = false;
    
    /**
     * 主页面控制器引用
     * Reference to the main page controller
     */
    private MainPageController mainPageController;
    
    /**
     * 记录页面控制器引用
     * Reference to the expense record page controller
     */
    private ExpenseRecordPageController expenseRecordPageController;

    /**
     * 初始化方法
     * Initialization method
     * 在FXML加载后自动调用，设置界面初始状态和事件监听器
     * Automatically called after FXML is loaded, sets the initial state of the interface and event listeners
     */
    @FXML
    private void initialize() {
        // 初始化币种选择
        // Initialize currency selection
        currencyComboBox.getItems().addAll("CNY", "USD", "EUR", "GBP", "JPY");
        currencyComboBox.setValue("CNY");
        
        // 初始化类别下拉框 - 默认为支出类别
        // Initialize category combo box - default to expense category
        updateCategoryComboBox(true);
        
        // 初始化日期选择器为当前日期
        // Initialize date picker to current date
        datePicker.setValue(LocalDate.now());
        
        // 设置类别选择监听器，更新子类别列表
        // Set category selection listener to update subcategory list
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateSubCategories(newValue);
        });

        // 修改：使用ToggleGroup的selectedToggleProperty来监听单选按钮变化
        // Modification: Use selectedToggleProperty of ToggleGroup to listen for radio button changes
        recordTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            handleRecordTypeChange();
        });
    }
    
    /**
     * 处理记录类型变化事件
     * Handle record type change event
     * 当切换收支类型时更新类别选项
     * Update category options when switching income/expense type
     */
    @FXML
    private void handleRecordTypeChange() {
        boolean isExpense = expensesRadio.isSelected();
        updateCategoryComboBox(isExpense);
    }
    
    /**
     * 更新类别下拉框选项
     * Update category combo box options
     * @param isExpense 是否为支出类型
     * @param isExpense Whether it is an expense type
     */
    private void updateCategoryComboBox(boolean isExpense) {
        categoryComboBox.getItems().clear();
        
        if (isExpense) {
            // 支出类别
            // Expense categories
            categoryComboBox.getItems().addAll("餐饮", "交通", "购物", "娱乐", "教育", "运动", "生活", "通讯", "医疗");
        } else {
            // 收入类别
            // Income categories
            categoryComboBox.getItems().addAll("工资", "投资", "礼金", "奖金", "兼职", "其他");
        }
    }
    
    /**
     * 更新子类别列表
     * Update subcategory list
     * @param category 选中的主类别
     * @param category Selected main category
     */
    private void updateSubCategories(String category) {
        if (subCategoryListView == null || category == null) return;
        
        subCategoryListView.getItems().clear();
        
        // 根据是支出还是收入以及选择的类别添加对应的子类别
        // Add corresponding subcategories based on whether it is an expense or income and the selected category
        if (expensesRadio.isSelected()) {
            // 支出子类别
            // Expense subcategories
            switch (category) {
                case "餐饮":
                    subCategoryListView.getItems().addAll("早餐", "午餐", "晚餐", "外卖", "零食", "饮料", "下午茶", "宵夜");
                    break;
                case "交通":
                    subCategoryListView.getItems().addAll("公交车", "地铁", "出租车", "共享单车", "火车", "飞机", "加油", "停车费", "高速费");
                    break;
                case "购物":
                    subCategoryListView.getItems().addAll("服装", "电子产品", "日用品", "超市购物", "化妆品", "饰品", "鞋包", "家居用品");
                    break;
                case "娱乐":
                    subCategoryListView.getItems().addAll("电影", "游戏", "KTV", "演唱会", "展览", "运动场馆", "旅游", "网络服务");
                    break;
                case "教育":
                    subCategoryListView.getItems().addAll("学费", "书籍", "文具", "培训", "考试费", "网课", "辅导班", "留学");
                    break;
                case "运动":
                    subCategoryListView.getItems().addAll("健身房", "运动装备", "球类运动", "游泳", "瑜伽", "户外运动", "体育赛事");
                    break;
                case "生活":
                    subCategoryListView.getItems().addAll("房租", "水费", "电费", "燃气费", "物业费", "维修", "理发", "洗衣");
                    break;
                case "通讯":
                    subCategoryListView.getItems().addAll("话费", "网费", "有线电视", "手机", "电脑", "配件", "软件服务");
                    break;
                case "医疗":
                    subCategoryListView.getItems().addAll("挂号费", "药品", "检查费", "手术费", "保健品", "医疗保险", "牙科", "眼科");
                    break;
                default:
                    break;
            }
        } else {
            // 收入子类别
            // Income subcategories
            switch (category) {
                case "工资":
                    subCategoryListView.getItems().addAll("月薪", "年终奖", "加班费", "绩效奖金", "项目奖励", "补贴", "提成");
                    break;
                case "投资":
                    subCategoryListView.getItems().addAll("股票", "基金", "存款利息", "债券", "房产投资", "数字货币", "期货", "理财产品");
                    break;
                case "礼金":
                    subCategoryListView.getItems().addAll("生日礼金", "节日礼金", "婚礼礼金", "红包", "祝寿礼金", "满月礼金");
                    break;
                case "奖金":
                    subCategoryListView.getItems().addAll("竞赛奖金", "抽奖奖金", "创新奖励", "专利奖励", "优秀员工奖", "销售奖金");
                    break;
                case "兼职":
                    subCategoryListView.getItems().addAll("家教", "翻译", "写作", "外包项目", "网络兼职", "临时工", "代驾", "外卖配送");
                    break;
                case "其他":
                    subCategoryListView.getItems().addAll("二手交易", "租金收入", "版权收入", "广告收入", "退款", "赔偿金", "中奖", "继承");
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * 设置主页面控制器
     * Set main page controller
     * @param controller 主页面控制器实例
     * @param controller Main page controller instance
     */
    public void setMainPageController(MainPageController controller) {
        this.mainPageController = controller;
    }
    
    /**
     * 设置记录页面控制器
     * Set expense record page controller
     * @param controller 记录页面控制器实例
     * @param controller Expense record page controller instance
     */
    public void setExpenseRecordPageController(ExpenseRecordPageController controller) {
        this.expenseRecordPageController = controller;
    }
    
    /**
     * 处理保存按钮点击事件
     * Handle save button click event
     * 验证输入数据并保存记录
     * Validate input data and save record
     */
    @FXML
    private void handleSave() {
        if (isInputValid()) {
            // 获取子类别作为消费名称，如果没有选择则使用nameField的值
            // Get subcategory as expense name, use nameField value if none selected
            String expenseName = nameField.getText();
            if (subCategoryListView != null) {
                String selectedSubCategory = subCategoryListView.getSelectionModel().getSelectedItem();
                if (selectedSubCategory != null && !selectedSubCategory.isEmpty()) {
                    expenseName = selectedSubCategory;
                }
            }
            
            // 获取记录类型（支出或收入）
            // Get record type (expense or income)
            String recordType = expensesRadio.isSelected() ? "支出" : "收入";
            
            // 获取币种
            // Get currency
            String currency = currencyComboBox.getValue();
            
            ExpenseRecord record = new ExpenseRecord(
                    Double.parseDouble(amountField.getText()),
                    categoryComboBox.getValue(),
                    expenseName,
                    datePicker.getValue()
            );
            
            // 设置备注信息
            // Set note information
            if (noteField != null && noteField.getText() != null && !noteField.getText().isEmpty()) {
                record.setDescription(noteField.getText());
            }
            
            // 设置记录类型和币种
            // Set record type and currency
            record.setRecordType(recordType);
            record.setCurrency(currency);
            
            // 根据控制器类型添加消费记录
            // Add expense record based on controller type
            if (mainPageController != null) {
                mainPageController.addExpenseRecord(record);
            } else if (expenseRecordPageController != null) {
                expenseRecordPageController.addExpenseRecord(record);
            }
            
            isConfirmed = true;
            dialogStage.close();
        }
    }

    /**
     * 处理取消按钮点击事件
     * Handle cancel button click event
     * 关闭对话框
     * Close dialog
     */
    @FXML
    void handleCancel() {
        dialogStage.close();
    }

    /**
     * 验证输入数据的有效性
     * Validate input data validity
     * 检查必填字段和数据格式
     * Check required fields and data format
     * @return 如果所有输入有效返回true，否则返回false
     * @return true if all inputs are valid, otherwise false
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
        // Check if a name is entered
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            // 检查是否有选择子类别
            // Check if a subcategory is selected
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
     * 获取用户是否确认保存
     * Get whether the user has confirmed saving
     * @return 如果用户确认保存返回true，否则返回false
     * @return true if the user has confirmed saving, otherwise false
     */
    public boolean isConfirmed() {
        return isConfirmed;
    }
    
    /**
     * 检查是否选择了收入类型
     * Check if income type is selected
     * @return 如果选择了收入类型返回true，否则返回false
     * @return true if income type is selected, otherwise false
     */
    public boolean isIncomeSelected() {
        return incomeRadio.isSelected();
    }
    
    /**
     * 设置记录类型（支出或收入）
     * Set record type (expense or income)
     * @param type 记录类型，"income"表示收入，其他值表示支出
     * @param type Record type, "income" for income, other values for expense
     */
    public void setRecordType(String type) {
        if ("income".equalsIgnoreCase(type)) {
            incomeRadio.setSelected(true);
        } else {
            expensesRadio.setSelected(true);
        }
        // 手动触发类别更新
        // Manually trigger category update
        handleRecordTypeChange();
    }

    /**
     * 处理导入按钮点击事件
     * Handle import button click event
     * 打开文件选择器并导入CSV文件数据
     * Open file chooser and import CSV file data
     * CSV文件格式要求：
     * CSV file format requirements:
     * - 标题行：Type,Amount,Date,Additional
     * - Header row: Type,Amount,Date,Additional
     * - Type: 支出/收入类别（如：餐饮、交通、工资等）
     * - Type: Expense/Income category (e.g., dining, transportation, salary, etc.)
     * - Amount: 金额（数字格式）
     * - Amount: Amount (numeric format)
     * - Date: 日期（格式：yyyy-MM-dd）
     * - Date: Date (format: yyyy-MM-dd)
     * - Additional: 子类别或备注信息
     * - Additional: Subcategory or note information
     */
    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择CSV文件");
        
        // 设置默认目录为项目的data文件夹
        // Set default directory to the project's data folder
        // 但是目前只能直接调用这个测试csv，还没有变成“默认调用任意一个本地csv文件”后续会改，此行仅测试用 ————BY JADE
        // But currently can only directly call this test csv, not yet changed to "default call any local csv file", will be modified later, this line is for testing only ————BY JADE
        fileChooser.setInitialDirectory(
            new File("src/main/resources/data/homepagecontroller_test_sorted.csv").getParentFile());
        
        // 添加CSV文件过滤器
        // Add CSV file filter
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"),
                new FileChooser.ExtensionFilter("所有文件", "*.*"));
        
        File file = fileChooser.showOpenDialog(dialogStage);
        if (file != null) {
            try {
                // 使用CSVHandler导入数据
                List<ExpenseRecord> importedRecords = com.coinue.util.CSVHandler.readExpenseRecords(file.getPath());
                
                // 记录导入成功和失败的数量
                int successCount = 0;
                int failureCount = 0;
                
                // 将导入的记录添加到主页面
                for (ExpenseRecord record : importedRecords) {
                    try {
                        // 验证记录的有效性
                        if (validateRecord(record)) {
                            mainPageController.addExpenseRecord(record);
                            successCount++;
                        } else {
                            failureCount++;
                        }
                    } catch (Exception e) {
                        failureCount++;
                    }
                }
                
                // 刷新主页面的消费记录表格
                mainPageController.refreshExpenseRecords();
                
                // 显示导入结果
                showImportResult(successCount, failureCount);
                
            } catch (Exception e) {
                showErrorDialog("导入错误", "导入CSV文件时发生错误：" + e.getMessage());
            }
        }
    }

    /**
     * 验证导入的记录是否有效
     * Validate if the imported record is valid
     * @param record 要验证的记录
     * @param record The record to validate
     * @return 如果记录有效返回true，否则返回false
     * @return true if the record is valid, otherwise false
     */
    private boolean validateRecord(ExpenseRecord record) {
        // 检查必要字段
        // Check necessary fields
        if (record.getAmount() <= 0 || 
            record.getCategory() == null || 
            record.getCategory().trim().isEmpty() ||
            record.getDate() == null) {
            return false;
        }

        // 验证类别是否在预定义列表中
        // Validate if the category is in the predefined list
        String category = record.getCategory();
        if (record.getRecordType().equals("支出")) {
            return java.util.Arrays.asList("餐饮", "交通", "购物", "娱乐", "教育",
                               "运动", "生活", "通讯", "医疗").contains(category);
        } else {
            return java.util.Arrays.asList("工资", "投资", "礼金", "奖金", 
                               "兼职", "其他").contains(category);
        }
    }

    /**
     * 显示导入结果对话框
     * Show import result dialog
     * @param successCount 成功导入的记录数
     * @param successCount Number of successfully imported records
     * @param failureCount 导入失败的记录数
     * @param failureCount Number of failed imports
     */
    private void showImportResult(int successCount, int failureCount) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("导入结果");
        alert.setHeaderText(null);
        
        StringBuilder message = new StringBuilder();
        message.append("导入完成：\n");
        message.append("成功导入：").append(successCount).append(" 条记录\n");
        if (failureCount > 0) {
            message.append("导入失败：").append(failureCount).append(" 条记录\n");
            message.append("请检查失败记录的格式是否正确。");
        }
        
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    /**
     * 显示错误对话框
     * Show error dialog
     * @param title 对话框标题
     * @param title Dialog title
     * @param message 错误信息
     * @param message Error message
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 设置对话框的Stage
     * Set the dialog's Stage
     * @param dialogStage 对话框的Stage实例
     * @param dialogStage The Stage instance of the dialog
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}