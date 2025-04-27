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
 * Bill Payment Page Controller
 * Handles bill import and chart display functionality
 */
public class BillPaymentPageController {

    @FXML
    private Label titleLabel;
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

    private double creditLimit = 7500.00; // Default credit limit

    @FXML
    private void initialize() {
        titleLabel.setText("Bill Payment Analysis");
        
        // Initialize table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize pie chart
        updatePieChart(0.0);
        
        // 设置信用额度输入框
        creditLimitField.setText(String.format("%.2f", creditLimit));
        creditLimitField.setOnAction(event -> {
            try {
                creditLimit = Double.parseDouble(creditLimitField.getText());
                updatePieChart(0.0); // 更新图表
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number");
                creditLimitField.setText(String.format("%.2f", creditLimit));
            }
        });
        repaymentAmountLabel.setText("Repayment Amount: 0.00");
        
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
            showError("Data Error", "Please ensure the CSV file format is correct");
            return;
        }

        billTable.setItems(FXCollections.observableArrayList(records));
        updatePieChart(totalAmount);
        repaymentAmountLabel.setText(String.format("Repayment Amount: %.2f", totalAmount));
        
        // Change color based on amount
        if(totalAmount > creditLimit * 0.8) {
            repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff0000;");
        } else if(totalAmount > creditLimit * 0.5) {
            repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ff9900;");
        } else {
            repaymentAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #009900;");
        }
    }

    private void updatePieChart(double repaymentAmount) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Used Credit", repaymentAmount),
            new PieChart.Data("Remaining Credit", creditLimit - repaymentAmount)
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
            // Switch to main page using page manager
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load main page: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        try {
            // Switch to analysis page using page manager
            PageManager.getInstance().switchToPage("/view/AnalysisPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load analysis page: " + e.getMessage());
        }
    }

    @FXML
    private void handleUserNav() {
        try {
            // Switch to user page using page manager
            PageManager.getInstance().switchToPage("/view/UserPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load user page: " + e.getMessage());
        }
    }
}