package com.coinue.controller;

import com.coinue.util.PageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 还款账单页面控制器
 * 处理账单导入和图表展示功能
 */
public class BillPaymentPageController {

    @FXML
    private Label titleLabel;
    @FXML
    private PieChart repaymentChart;
    @FXML
    private Label creditLimitLabel;
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

    private double creditLimit = 7500.00; // 默认信用额度

    @FXML
    public void initialize() {
        titleLabel.setText("还款账单分析");
        
        // 初始化表格列
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 初始化环形图
        updatePieChart(0.0);
        
        // 设置信用额度标签
        creditLimitLabel.setText(String.format("信用额度：%.2f", creditLimit));
        repaymentAmountLabel.setText("还款金额：0.00");
    }

    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择CSV文件");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV文件", "*.csv")
        );

        File file = fileChooser.showOpenDialog(billTable.getScene().getWindow());
        if (file != null) {
            importCSVFile(file);
        }
    }

    private void importCSVFile(File file) {
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
            showError("导入错误", "导入CSV文件失败: " + e.getMessage());
            return;
        } catch (Exception e) {
            showError("数据错误", "请确保CSV文件格式正确");
            return;
        }

        billTable.setItems(FXCollections.observableArrayList(records));
        updatePieChart(totalAmount);
        repaymentAmountLabel.setText(String.format("还款金额：%.2f", totalAmount));
    }

    private void updatePieChart(double repaymentAmount) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("已用额度", repaymentAmount),
            new PieChart.Data("剩余额度", creditLimit - repaymentAmount)
        );
        repaymentChart.setData(pieChartData);
        repaymentChart.setTitle(String.format("%.0f%%", (repaymentAmount / creditLimit) * 100));
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 账单记录数据模型
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
    private void switchToHomepage() {
        try {
            // 使用页面管理器切换到主页
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            showError("导航错误", "无法加载主页面");
        }
    }

    @FXML
    private void switchToAnalysis() {
        try {
            // 使用页面管理器切换到分析页面
            PageManager.getInstance().switchToPage("/view/AnalysisPage.fxml");
        } catch (IOException e) {
            showError("导航错误", "无法加载分析页面");
        }
    }

    @FXML
    private void switchToDashboard() {
        try {
            // 使用页面管理器切换到用户页面
            PageManager.getInstance().switchToPage("/view/UserPage.fxml");
        } catch (IOException e) {
            showError("导航错误", "无法加载仪表盘页面");
        }
    }
}