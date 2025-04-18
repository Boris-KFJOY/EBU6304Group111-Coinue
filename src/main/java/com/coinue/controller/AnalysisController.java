package com.coinue.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TabPane;

import java.time.LocalDate;

public class AnalysisController {
    @FXML
    private TabPane tabPane;

    @FXML
    private PieChart categoryChart;

    @FXML
    private TableView<ExpenditureRecord> expenditureTable;

    @FXML
    private TableColumn<ExpenditureRecord, String> itemColumn;

    @FXML
    private TableColumn<ExpenditureRecord, Double> moneyColumn;

    @FXML
    private TableColumn<ExpenditureRecord, String> categoryColumn;

    @FXML
    private TableColumn<ExpenditureRecord, String> companyColumn;

    @FXML
    private TableColumn<ExpenditureRecord, LocalDate> timeColumn;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private ComboBox<String> companyFilter;

    private ObservableList<ExpenditureRecord> masterData;

    @FXML
    public void initialize() {
        tabPane.getSelectionModel().select(1); // Select Analysis tab
        initializePieChart();
        initializeTable();
    }

    private void initializePieChart() {
        // Initialize pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Health", 1230.00),
            new PieChart.Data("Shopping", 34.00),
            new PieChart.Data("Tax", 87.50),
            new PieChart.Data("Food", 60.00)
        );
        categoryChart.setData(pieChartData);
    }

    private void initializeTable() {
        // Initialize table
        // Set cell value factories for table columns
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("money"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Set table data
        masterData = FXCollections.observableArrayList(
            new ExpenditureRecord("KFC", 60.00, "Food", "Huabei", LocalDate.parse("2025-03-03")),
            new ExpenditureRecord("W-Shop", 34.00, "Shopping", "Huabei", LocalDate.parse("2025-03-05")),
            new ExpenditureRecord("Nathaniel Arnold", 1230.00, "Health", "Baitiao", LocalDate.parse("2025-03-22")),
            new ExpenditureRecord("Earl Harper", 87.50, "Tax", "Baitiao", LocalDate.parse("2025-03-19"))
        );

        // Initialize filters
        // Initialize filters
        ObservableList<String> categories = FXCollections.observableArrayList(
            "All", "Food", "Shopping", "Health", "Tax"
        );
        ObservableList<String> companies = FXCollections.observableArrayList(
            "All", "Huabei", "Baitiao"
        );

        categoryFilter.setItems(categories);
        companyFilter.setItems(companies);
        categoryFilter.setValue("All");
        companyFilter.setValue("All");

        // Create filtered list
        FilteredList<ExpenditureRecord> filteredData = new FilteredList<>(masterData, p -> true);

        // Add listeners to filters
        // Add listeners to filters
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> 
            updateFilters(filteredData));
        companyFilter.valueProperty().addListener((observable, oldValue, newValue) -> 
            updateFilters(filteredData));

        expenditureTable.setItems(filteredData);
    }

    public static class ExpenditureRecord {
        private final String item;
        private final double money;
        private final String category;
        private final String company;
        private final LocalDate time;

        public ExpenditureRecord(String item, double money, String category, String company, LocalDate time) {
            this.item = item;
            this.money = money;
            this.category = category;
            this.company = company;
            this.time = time;
        }

        public String getItem() { return item; }
        public double getMoney() { return money; }
        public String getCategory() { return category; }
        public String getCompany() { return company; }
        public LocalDate getTime() { return time; }
    }

    private void updateFilters(FilteredList<ExpenditureRecord> filteredData) {
        filteredData.setPredicate(record -> {
            String selectedCategory = categoryFilter.getValue();
            String selectedCompany = companyFilter.getValue();

            boolean categoryMatch = selectedCategory.equals("All") || record.getCategory().equals(selectedCategory);
            boolean companyMatch = selectedCompany.equals("All") || record.getCompany().equals(selectedCompany);

            return categoryMatch && companyMatch;
        });
    }

    @FXML
    private void handleClose() {
        tabPane.getScene().getWindow().hide();
    }
}