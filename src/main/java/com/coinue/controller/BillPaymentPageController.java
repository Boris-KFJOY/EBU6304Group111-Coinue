package com.coinue.controller;

import com.coinue.model.User;
import com.coinue.model.UserBillData;
import com.coinue.model.UserDataService;
import com.coinue.util.PageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Bill Payment Page Controller
 * Handles bill import and chart display functionality
 */
public class BillPaymentPageController {

    @FXML
    private PieChart repaymentChart;
    @FXML
    private TextField creditLimitField; // 替换原来的Label
    @FXML
    private Label repaymentAmountLabel;
    @FXML
    private TableView<BillRecord> billTable;
    @FXML
    private TableColumn<BillRecord, LocalDate> dateColumn;
    @FXML
    private TableColumn<BillRecord, String> descriptionColumn;
    @FXML
    private TableColumn<BillRecord, Double> amountColumn;
    @FXML
    private TableColumn<BillRecord, String> statusColumn;
    @FXML
    private DatePicker dateFilterPicker;
    @FXML
    private Label userDataStatusLabel;
    @FXML
    private Button clearDataButton;

    private double creditLimit = 7500.00; // Default credit limit
    
    // 用户账单数据
    private UserBillData userBillData;
    
    // 用户数据服务
    private UserDataService userDataService;

    @FXML
    private void initialize() {
        // 初始化用户数据服务
        userDataService = UserDataService.getInstance();
        
        // 初始化表格列
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 设置列比较器
        dateColumn.setComparator(LocalDate::compareTo);
        descriptionColumn.setComparator(String::compareToIgnoreCase);
        amountColumn.setComparator(Double::compare);
        statusColumn.setComparator(String::compareToIgnoreCase);

        // 启用表格排序
        billTable.setSortPolicy(tableView -> {
            Comparator<BillRecord> comparator = (r1, r2) -> 0;
            ObservableList<TableColumn<BillRecord, ?>> sortOrder = tableView.getSortOrder();
            
            for (TableColumn<BillRecord, ?> column : sortOrder) {
                Comparator<BillRecord> columnComparator = (Comparator<BillRecord>) column.getComparator();
                if (columnComparator != null) {
                    comparator = comparator.thenComparing(columnComparator);
                }
            }
            
            FXCollections.sort(tableView.getItems(), comparator);
            return true;
        });
        
        // 初始化用户状态标签
        if (userDataStatusLabel != null) {
            userDataStatusLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        }
        
        // 设置清除数据按钮
        if (clearDataButton != null) {
            clearDataButton.setOnAction(e -> handleClearData());
        }
        
        // Initialize pie chart with default data
        initializePieChart();
        
        // 设置信用额度输入框
        creditLimitField.setText(String.format("%.2f", creditLimit));
        creditLimitField.setOnAction(event -> {
            try {
                double newCreditLimit = Double.parseDouble(creditLimitField.getText());
                setCreditLimit(newCreditLimit);
                updatePieChart(calculateTotalRepayment()); // 更新图表
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number for the credit limit.");
                creditLimitField.setText(String.format("%.2f", creditLimit));
            }
        });
        repaymentAmountLabel.setText("Repayment Amount: ¥0.00");
        
        // Set repayment amount label style
        repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
        
        // 添加日期筛选器监听
        dateFilterPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterTableByDate(newVal);
            } else {
                // 如果取消选择日期，显示所有记录
                billTable.setItems(FXCollections.observableArrayList(billTable.getItems()));
            }
        });
        
        // 加载用户数据
        loadUserDataOnInitialize();
    }

    private void filterTableByDate(LocalDate date) {
        ObservableList<BillRecord> filteredList = FXCollections.observableArrayList();
        for (BillRecord record : billTable.getItems()) {
            if (record.getDate().equals(date)) {
                filteredList.add(record);
            }
        }
        billTable.setItems(filteredList);
    }

    private void initializePieChart() {
        // 初始化时显示完整的信用额度
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Available Credit (100%)", creditLimit)
        );
        repaymentChart.setData(pieChartData);
        repaymentChart.setTitle("Credit Usage: 0%");
        repaymentChart.setLegendVisible(true);
        repaymentChart.setLabelsVisible(true);
    }

    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(billTable.getScene().getWindow());
        if (file != null) {
            importCSVFile(file);
        }
    }

    private void importCSVFile(File file) {
        // 检查用户登录状态
        User currentUser = User.getCurrentUser();
        if (currentUser == null) {
            showError("User Not Logged In", "Please log in before importing data to save it to your personal account.");
            return;
        }
        
        List<BillRecord> records = new ArrayList<>();
        double totalAmount = 0.0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 4) {
                    BillRecord record = new BillRecord(
                        LocalDate.parse(data[0].trim()),
                        data[1].trim(),
                        Double.parseDouble(data[2].trim()),
                        data[3].trim()
                    );
                    records.add(record);
                    totalAmount += record.getAmount();
                }
            }
        } catch (IOException e) {
            showError("Import Error", "Failed to import CSV file: " + e.getMessage());
            return;
        } catch (Exception e) {
            showError("Data Error", "Please ensure the CSV file format is correct (e.g., date,description,amount,status).");
            return;
        }

        // 备份CSV文件到用户目录
        String backupPath = backupCsvFileToUserDirectory(file, currentUser.getUsername());
        
        billTable.setItems(FXCollections.observableArrayList(records));
        updatePieChart(totalAmount);
        repaymentAmountLabel.setText(String.format("Repayment Amount: ¥%.2f", totalAmount));
        updateRepaymentAmountColor(totalAmount);
        
        // 保存用户数据
        saveUserBillData(currentUser.getUsername());
        
        // 更新状态
        String statusMessage = "Imported " + records.size() + " records";
        if (backupPath != null) {
            statusMessage += " (CSV file backed up)";
        }
        updateUserDataStatus(statusMessage + " - " + currentUser.getUsername());
    }

    private void updatePieChart(double repaymentAmount) {
        // 确保数据有效性
        if (repaymentAmount < 0) repaymentAmount = 0;
        if (repaymentAmount > creditLimit) repaymentAmount = creditLimit;
        
        double remainingCredit = creditLimit - repaymentAmount;
        double usagePercentage = (repaymentAmount / creditLimit) * 100;
        
        // 创建饼图数据，确保数据大于0才显示
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        if (repaymentAmount > 0) {
            pieChartData.add(new PieChart.Data(String.format("Used Credit (%.1f%%)", usagePercentage), repaymentAmount));
        }
        
        if (remainingCredit > 0) {
            pieChartData.add(new PieChart.Data(String.format("Remaining Credit (%.1f%%)", 100 - usagePercentage), remainingCredit));
        }
        
        // 设置数据到饼图
        repaymentChart.setData(pieChartData);
        repaymentChart.setTitle(String.format("Credit Usage: %.0f%%", usagePercentage));
        
        // 强制刷新图表
        repaymentChart.setAnimated(false);
        repaymentChart.setAnimated(true);
        
        // 添加调试输出
        System.out.println("DEBUG: Updating pie chart with repaymentAmount=" + repaymentAmount + 
                         ", creditLimit=" + creditLimit + ", usage=" + usagePercentage + "%");
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Bill record data model
    public static class BillRecord {
        private final LocalDate date;
        private final String description;
        private final double amount;
        private final String status;

        public BillRecord(LocalDate date, String description, double amount, String status) {
            this.date = date;
            this.description = description;
            this.amount = amount;
            this.status = status;
        }

        public LocalDate getDate() { return date; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
        public String getStatus() { return status; }
    }

    @FXML
    private void handleHomeNav() {
        try {
            // 保存当前数据
            saveCurrentDataIfLoggedIn();
            // Switch to main page using page manager
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load main page: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        try {
            // 保存当前数据
            saveCurrentDataIfLoggedIn();
            // Switch to analysis page using page manager
            PageManager.getInstance().switchToPage("/view/AnalysisPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load analysis page: " + e.getMessage());
        }
    }

    @FXML
    private void handleUserNav() {
        try {
            // 保存当前数据
            saveCurrentDataIfLoggedIn();
            // Switch to user page using page manager
            PageManager.getInstance().switchToPage("/view/UserPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load user page: " + e.getMessage());
        }
    }
    
    // ============================== 用户数据管理方法 ==============================
    
    /**
     * 页面初始化时加载用户数据
     */
    private void loadUserDataOnInitialize() {
        User currentUser = User.getCurrentUser();
        if (currentUser != null) {
            loadUserBillData(currentUser.getUsername());
            updateUserDataStatus("Loaded user history data - " + currentUser.getUsername());
        } else {
            updateUserDataStatus("User not logged in - data will not be saved.");
        }
    }
    
    /**
     * 加载用户账单数据
     * @param username 用户名
     */
    private void loadUserBillData(String username) {
        try {
            userBillData = userDataService.loadData(username, "bill_data.json", UserBillData.class);
            
            if (userBillData == null) {
                userBillData = new UserBillData();
                System.out.println("Created new bill data for user " + username);
            } else {
                System.out.println("Successfully loaded bill data for user " + username + ": " + userBillData.toString());
                
                // 恢复信用额度设置
                setCreditLimit(userBillData.getCreditLimit());
                
                // 恢复账单记录
                if (!userBillData.getBillRecords().isEmpty()) {
                    List<BillRecord> billRecords = convertFromUserBillData(userBillData.getBillRecords());
                    billTable.setItems(FXCollections.observableArrayList(billRecords));
                    
                    double totalAmount = userBillData.getTotalRepaymentAmount();
                    updatePieChart(totalAmount);
                    repaymentAmountLabel.setText(String.format("Repayment Amount: ¥%.2f", totalAmount));
                    updateRepaymentAmountColor(totalAmount);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load user bill data: " + e.getMessage());
            userBillData = new UserBillData();
        }
    }
    
    /**
     * 保存用户账单数据
     * @param username 用户名
     */
    private void saveUserBillData(String username) {
        if (userBillData == null) {
            userBillData = new UserBillData();
        }
        
        // 更新当前数据
        userBillData.setCreditLimit(creditLimit);
        
        // 保存当前表格中的账单记录
        ObservableList<BillRecord> currentRecords = billTable.getItems();
        if (currentRecords != null && !currentRecords.isEmpty()) {
            List<UserBillData.BillRecord> userBillRecords = convertToUserBillData(currentRecords);
            userBillData.setBillRecords(userBillRecords);
        }
        
        boolean success = userDataService.saveData(username, "bill_data.json", userBillData);
        if (success) {
            System.out.println("Successfully saved bill data for user " + username);
        } else {
            System.err.println("Failed to save bill data for user " + username);
        }
    }
    
    /**
     * 转换BillRecord到UserBillData.BillRecord
     * @param billRecords BillRecord列表
     * @return UserBillData.BillRecord列表
     */
    private List<UserBillData.BillRecord> convertToUserBillData(ObservableList<BillRecord> billRecords) {
        List<UserBillData.BillRecord> userBillRecords = new ArrayList<>();
        for (BillRecord record : billRecords) {
            UserBillData.BillRecord userRecord = new UserBillData.BillRecord(
                record.getDate(),
                record.getDescription(),
                record.getAmount(),
                record.getStatus()
            );
            userBillRecords.add(userRecord);
        }
        return userBillRecords;
    }
    
    /**
     * 转换UserBillData.BillRecord到BillRecord
     * @param userBillRecords UserBillData.BillRecord列表
     * @return BillRecord列表
     */
    private List<BillRecord> convertFromUserBillData(List<UserBillData.BillRecord> userBillRecords) {
        List<BillRecord> billRecords = new ArrayList<>();
        for (UserBillData.BillRecord userRecord : userBillRecords) {
            BillRecord record = new BillRecord(
                userRecord.getDate(),
                userRecord.getDescription(),
                userRecord.getAmount(),
                userRecord.getStatus()
            );
            billRecords.add(record);
        }
        return billRecords;
    }
    
    /**
     * 设置信用额度并保存
     * @param newCreditLimit 新的信用额度
     */
    private void setCreditLimit(double newCreditLimit) {
        this.creditLimit = newCreditLimit;
        creditLimitField.setText(String.format("%.2f", creditLimit));
        
        // 如果用户已登录，更新用户数据
        User currentUser = User.getCurrentUser();
        if (currentUser != null && userBillData != null) {
            userBillData.setCreditLimit(creditLimit);
        }
    }
    
    /**
     * 计算当前总还款金额
     * @return 总还款金额
     */
    private double calculateTotalRepayment() {
        ObservableList<BillRecord> records = billTable.getItems();
        if (records == null || records.isEmpty()) {
            return 0.0;
        }
        return records.stream().mapToDouble(BillRecord::getAmount).sum();
    }
    
    /**
     * 备份CSV文件到用户目录
     * @param sourceFile 源文件
     * @param username 用户名
     * @return 备份文件路径
     */
    private String backupCsvFileToUserDirectory(File sourceFile, String username) {
        try {
            String userDir = userDataService.getUserDataDirectory(username);
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String backupFileName = "bill_import_" + timestamp + "_" + sourceFile.getName();
            Path backupPath = Paths.get(userDir, backupFileName);
            
            Files.copy(sourceFile.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 更新用户数据中的最后导入文件名
            if (userBillData != null) {
                userBillData.setLastImportedFile(backupFileName);
            }
            
            System.out.println("CSV file has been backed up to: " + backupPath.toString());
            return backupPath.toString();
        } catch (IOException e) {
            System.err.println("Failed to backup CSV file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 如果用户已登录则保存当前数据
     */
    private void saveCurrentDataIfLoggedIn() {
        User currentUser = User.getCurrentUser();
        if (currentUser != null) {
            saveUserBillData(currentUser.getUsername());
        }
    }
    
    /**
     * 更新用户数据状态显示
     * @param status 状态信息
     */
    private void updateUserDataStatus(String status) {
        if (userDataStatusLabel != null) {
            userDataStatusLabel.setText(status);
        }
    }
    
    /**
     * 更新还款金额颜色
     * @param totalAmount 总金额
     */
    private void updateRepaymentAmountColor(double totalAmount) {
        if(totalAmount > creditLimit * 0.8) {
            repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
        } else if(totalAmount > creditLimit * 0.5) {
            repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff9900;");
        } else {
            repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #009900;");
        }
    }
    
    /**
     * 处理清除数据按钮点击
     */
    @FXML
    private void handleClearData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear");
        alert.setHeaderText("Clear All Bill Data");
        alert.setContentText("This will clear all bill records on the current page but will not delete saved historical data. Continue?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // 清除表格数据
                billTable.getItems().clear();
                
                // 重置图表
                initializePieChart();
                repaymentAmountLabel.setText("Repayment Amount: ¥0.00");
                repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
                
                updateUserDataStatus("Data cleared - current state is empty.");
            }
        });
    }
}