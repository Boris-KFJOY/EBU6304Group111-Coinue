package com.coinue.controller;

import com.coinue.service.AnalysisService;
import com.coinue.service.ExportService;
import com.coinue.service.impl.AnalysisServiceImpl;
import com.coinue.service.impl.ExportServiceImpl;
import com.coinue.util.CSVHandler;
import com.coinue.util.ChartGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;    
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Analysis Page Controller
 * Handles the logic of expense data analysis and chart display
 */
public class AnalysisPageController {

    @FXML
    private PieChart expensePieChart;
    @FXML
    private BarChart<String, Number> expenseBarChart;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label statisticsLabel;
    @FXML
    private ProgressBar budgetProgressBar;
    @FXML
    private Label budgetLabel;
    @FXML
    private HBox statsCardsContainer;  // 新增统计卡片容器

    // 添加服务层依赖
    private AnalysisService analysisService;
    private ExportService exportService;

    private static final double DEFAULT_BUDGET = 10000.0; // Default budget amount
    private double currentBudget = DEFAULT_BUDGET;
    private double totalExpense = 0.0;

    /**
     * 初始化方法，在FXML加载后自动调用
     */
    @FXML
    public void initialize() {
        // 初始化服务
        analysisService = new AnalysisServiceImpl();
        exportService = new ExportServiceImpl();
    }

    /**
     * Handle import analysis file button click event
     */
    @FXML
    public void handleImportAnalysisFile() throws IOException {
        // 使用ExportService来显示文件选择器
        File file = exportService.showFileChooser(
            "Select CSV file", 
            "CSV文件", 
            new String[]{"csv"}, 
            null, 
            false
        );
        
        if (file != null) {
            handleImportAnalysisFile(file);
        }
    }
    
    /**
     * Handle import analysis file
     * @param file CSV file to analyze
     */
    public void handleImportAnalysisFile(File file) throws IOException {
        try {
            // 使用分析服务读取并生成统计数据
            Map<String, Double> categoryStatistics = analysisService.readCategoryStatistics(file);
            
            // 更新文件名标签
            fileNameLabel.setText("Current file: " + file.getName());

            // 生成并显示饼图
            expensePieChart.setData(ChartGenerator.generateExpensePieChartData(categoryStatistics));
            
            // 生成并显示条形图
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Expense amount");
            categoryStatistics.forEach((category, amount) ->
                series.getData().add(new XYChart.Data<>(category, amount)));
            expenseBarChart.getData().clear();
            expenseBarChart.getData().add(series);
            
            // 使用分析服务生成统计摘要
            statisticsLabel.setText(analysisService.generateStatisticsSummary(categoryStatistics));

            // 计算总支出并更新进度条
            totalExpense = categoryStatistics.values().stream().mapToDouble(Double::doubleValue).sum();
            updateBudgetProgress();

            // 更新统计显示
            updateStatisticsDisplay(categoryStatistics);
        } catch (IOException e) {
            showError("导入失败", "无法读取CSV文件：" + e.getMessage());
        } catch (Exception e) {
            showError("分析失败", "数据分析错误：" + e.getMessage());
        }
    }

    /**
     * Handle navigation to home page
     */
    @FXML
    private void handleHomeNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
            Parent root = loader.load();
            Scene scene = expensePieChart.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Navigation failed", "Failed to load home page: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        // Already in analysis page, no action needed
    }

    @FXML
    private void handleUserNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserPage.fxml"));
            Parent root = loader.load();
            Scene scene = expensePieChart.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Navigation failed", "Failed to load user page: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Show information dialog
     */
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handle export PDF button click event
     */
    @FXML
    private void handleExportPdf() {
        try {
            // 使用ExportService来显示保存文件对话框
            File file = exportService.showFileChooser(
                "Save PDF file",
                "PDF文件",
                new String[]{"pdf"},
                null,
                true
            );
            
            if (file != null) {
                List<Node> chartNodes = new ArrayList<>();
                chartNodes.add(expensePieChart);
                chartNodes.add(expenseBarChart);
                
                // 获取当前显示的统计数据
                Map<String, Double> statistics = ChartGenerator.extractDataFromPieChart(expensePieChart);
                
                // 使用导出服务生成分析报告
                boolean success = exportService.generateAnalysisReport(
                    chartNodes, 
                    statistics, 
                    file.getPath(),
                    "Expense Analysis Report"
                );
                
                if (success) {
                    showInfo("导出成功", "分析报告已成功导出到：" + file.getPath());
                } else {
                    showError("导出失败", "无法导出分析报告");
                }
            }
        } catch (IOException e) {
            showError("导出失败", "生成PDF文件时发生错误：" + e.getMessage());
        }
    }

    /**
     * Handle set budget button click event
     */
    @FXML
    private void handleSetBudget() {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(currentBudget));
        dialog.setTitle("设置预算");
        dialog.setHeaderText("请输入新的预算金额");
        dialog.setContentText("预算金额：");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(budgetStr -> {
            try {
                double newBudget = Double.parseDouble(budgetStr);
                if (newBudget > 0) {
                    currentBudget = newBudget;
                    updateBudgetProgress();
                } else {
                    showError("无效输入", "预算金额必须大于0");
                }
            } catch (NumberFormatException e) {
                showError("无效输入", "请输入有效数字");
            }
        });
    }

    /**
     * Update budget progress bar
     */
    private void updateBudgetProgress() {
        // 使用分析服务计算预算进度
        double progress = analysisService.calculateBudgetProgress(totalExpense, currentBudget);
        budgetProgressBar.setProgress(Math.min(progress, 1.0));
        
        // 根据支出比例设置进度条颜色
        if (progress < 0.8) {
            budgetProgressBar.setStyle("-fx-accent: #28a745;"); // 绿色
        } else {
            budgetProgressBar.setStyle("-fx-accent: #ffc107;"); // 黄色
        }
        
        budgetLabel.setText(String.format("Current expense: ¥%.2f / Budget: ¥%.2f", totalExpense, currentBudget));
    }

    /**
     * Update statistics display
     * @param categoryStatistics 类别统计数据
     */
    private void updateStatisticsDisplay(Map<String, Double> categoryStatistics) {
        // 清空统计卡片容器
        statsCardsContainer.getChildren().clear();
        
        // 计算总支出
        double total = categoryStatistics.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // 添加总支出统计卡片
        statsCardsContainer.getChildren().add(
            createStatCard("总支出", String.format("¥%.2f", total), "#3498db")
        );
        
        // 使用分析服务获取按金额排序的前几个类别
        Map<String, Double> topCategories = analysisService.analyzeTopSpendingCategories();
        
        // 只取前3个类别显示
        topCategories.entrySet().stream()
            .limit(3)
            .forEach(entry -> {
                String category = entry.getKey();
                double amount = entry.getValue();
                double percentage = (amount / total) * 100;
                
                // 创建并添加类别统计卡片
                statsCardsContainer.getChildren().add(
                    createStatCard(
                        category, 
                        String.format("¥%.2f (%.1f%%)", amount, percentage), 
                        getColorForCategory(category)
                    )
                );
            });
    }

    /**
     * 为类别选择颜色
     * @param category 类别名称
     * @return 对应的颜色代码
     */
    private String getColorForCategory(String category) {
        // 简单的类别颜色映射
        if (category.toLowerCase().contains("food")) return "#e74c3c";
        if (category.toLowerCase().contains("transport")) return "#f39c12";
        if (category.toLowerCase().contains("shopping")) return "#9b59b6";
        if (category.toLowerCase().contains("entertainment")) return "#2ecc71";
        if (category.toLowerCase().contains("bill")) return "#1abc9c";
        if (category.toLowerCase().contains("health")) return "#e84393";
        if (category.toLowerCase().contains("education")) return "#3498db";
        return "#7f8c8d"; // 默认灰色
    }

    /**
     * Create statistics card
     * @param title 卡片标题
     * @param value 卡片值
     * @param color 卡片颜色
     * @return 创建的卡片节点
     */
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setMinWidth(150);
        card.setMaxWidth(180);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 18px;");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}  // 这是类的结束大括号
