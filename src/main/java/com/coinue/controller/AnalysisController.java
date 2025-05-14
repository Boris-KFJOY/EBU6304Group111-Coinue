package com.coinue.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TabPane;

import java.time.LocalDate;

/**
 * 分析控制器类
 * 负责处理支出分析界面的逻辑，包括饼图展示和表格数据过滤
 */
public class AnalysisController {
    // FXML注入的UI组件
    @FXML
    private TabPane tabPane; // 选项卡面板

    @FXML
    private PieChart categoryChart; // 类别饼图

    @FXML
    private TableView<ExpenditureRecord> expenditureTable; // 支出记录表格

    @FXML
    private TableColumn<ExpenditureRecord, String> itemColumn; // 项目列

    @FXML
    private TableColumn<ExpenditureRecord, Double> moneyColumn; // 金额列

    @FXML
    private TableColumn<ExpenditureRecord, String> categoryColumn; // 类别列

    @FXML
    private TableColumn<ExpenditureRecord, String> companyColumn; // 公司列

    @FXML
    private TableColumn<ExpenditureRecord, LocalDate> timeColumn; // 时间列

    @FXML
    private ComboBox<String> categoryFilter; // 类别过滤下拉框

    @FXML
    private ComboBox<String> companyFilter; // 公司过滤下拉框

    @FXML
    private DatePicker startDatePicker; // 开始日期选择器

    @FXML 
    private DatePicker endDatePicker; // 结束日期选择器

    // 数据集合
    private ObservableList<ExpenditureRecord> masterData; // 主数据集合
    private FilteredList<ExpenditureRecord> filteredData; // 过滤后的数据集合

    /**
     * 初始化方法，在FXML加载后自动调用
     * 设置默认选项卡，初始化饼图、表格和过滤器
     */
    @FXML
    public void initialize() {
        tabPane.getSelectionModel().select(1); // 选择第二个选项卡
        initializePieChart(); // 初始化饼图
        initializeTable(); // 初始化表格
        initializeFilters(); // 初始化过滤器
    }

    /**
     * 初始化饼图
     * 设置饼图的数据，展示不同类别的支出比例
     */
    private void initializePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Health", 1230.00), // 健康类别支出
            new PieChart.Data("Shopping", 34.00), // 购物类别支出
            new PieChart.Data("Tax", 87.50), // 税收类别支出
            new PieChart.Data("Food", 60.00) // 食品类别支出
        );
        categoryChart.setData(pieChartData); // 设置饼图数据
    }

    /**
     * 初始化表格
     * 设置表格列的数据绑定，加载示例数据，设置过滤器
     */
    private void initializeTable() {
        // 设置表格列与ExpenditureRecord属性的绑定
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("money"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        // 创建示例数据
        masterData = FXCollections.observableArrayList(
            new ExpenditureRecord("KFC", 60.00, "Food", "Huabei", LocalDate.parse("2025-03-03")),
            new ExpenditureRecord("W-Shop", 34.00, "Shopping", "Huabei", LocalDate.parse("2025-03-05")),
            new ExpenditureRecord("Nathaniel Arnold", 1230.00, "Health", "Baitiao", LocalDate.parse("2025-03-22")),
            new ExpenditureRecord("Earl Harper", 87.50, "Tax", "Baitiao", LocalDate.parse("2025-03-19"))
        );

        // 设置类别和公司过滤选项
        ObservableList<String> categories = FXCollections.observableArrayList(
            "All", "Food", "Shopping", "Health", "Tax"
        );
        ObservableList<String> companies = FXCollections.observableArrayList(
            "All", "Huabei", "Baitiao"
        );

        // 设置过滤下拉框的选项和默认值
        categoryFilter.setItems(categories);
        companyFilter.setItems(companies);
        categoryFilter.setValue("All");
        companyFilter.setValue("All");

        // 创建过滤数据集合，初始显示所有数据
        filteredData = new FilteredList<>(masterData, p -> true);

        // 添加过滤器监听器，当选择变化时更新过滤条件
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> 
            updateFilters(filteredData));
        companyFilter.valueProperty().addListener((observable, oldValue, newValue) -> 
            updateFilters(filteredData));

        // 设置表格数据源为过滤后的数据
        expenditureTable.setItems(filteredData);
    }

    /**
     * 初始化日期过滤器
     * 设置默认日期范围和添加监听器
     */
    private void initializeFilters() {
        System.out.println("startDatePicker: " + (startDatePicker == null ? "null" : "not null"));
        System.out.println("endDatePicker: " + (endDatePicker == null ? "null" : "not null"));
        
        if (startDatePicker != null && endDatePicker != null) {
            // 设置默认日期范围：从一个月前到今天
            startDatePicker.setValue(LocalDate.now().minusMonths(1));
            endDatePicker.setValue(LocalDate.now());
            
            // 添加日期变化监听器，更新过滤条件
            startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (filteredData != null) {
                    updateFilters(filteredData);
                }
            });
            endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (filteredData != null) {
                    updateFilters(filteredData);
                }
            });
        }
    }

    /**
     * 支出记录数据模型类
     * 用于表示单条支出记录的数据结构
     */
    public static class ExpenditureRecord {
        private final String item; // 支出项目
        private final double money; // 支出金额
        private final String category; // 支出类别
        private final String company; // 支付公司
        private final LocalDate time; // 支出时间

        /**
         * 构造函数
         * @param item 支出项目名称
         * @param money 支出金额
         * @param category 支出类别
         * @param company 支付公司
         * @param time 支出时间
         */
        public ExpenditureRecord(String item, double money, String category, String company, LocalDate time) {
            this.item = item;
            this.money = money;
            this.category = category;
            this.company = company;
            this.time = time;
        }

        // Getter方法
        public String getItem() { return item; }
        public double getMoney() { return money; }
        public String getCategory() { return category; }
        public String getCompany() { return company; }
        public LocalDate getTime() { return time; }
    }

    /**
     * 更新过滤条件
     * 根据用户选择的类别、公司和日期范围过滤表格数据
     * @param filteredData 要应用过滤条件的数据集合
     */
    private void updateFilters(FilteredList<ExpenditureRecord> filteredData) {
        filteredData.setPredicate(record -> {
            String selectedCategory = categoryFilter.getValue();
            String selectedCompany = companyFilter.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
        
            // 检查记录是否匹配所选类别（"All"表示显示所有类别）
            boolean categoryMatch = selectedCategory.equals("All") || record.getCategory().equals(selectedCategory);
            // 检查记录是否匹配所选公司（"All"表示显示所有公司）
            boolean companyMatch = selectedCompany.equals("All") || record.getCompany().equals(selectedCompany);
        
            // 检查记录日期是否在选定的日期范围内
            boolean dateMatch = (startDate == null || !record.getTime().isBefore(startDate)) &&
                              (endDate == null || !record.getTime().isAfter(endDate));
        
            // 只有同时满足所有条件的记录才会显示
            return categoryMatch && companyMatch && dateMatch;
        });
    }

    /**
     * 处理关闭按钮点击事件
     * 隐藏当前窗口
     */
    @FXML
    private void handleClose() {
        tabPane.getScene().getWindow().hide(); // 隐藏窗口
    }
}