package com.coinue.controller;

import com.coinue.util.CSVHandler;
import com.coinue.util.ChartGenerator;
import com.coinue.util.PageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

    private static final double DEFAULT_BUDGET = 10000.0; // Default budget amount
    private double currentBudget = DEFAULT_BUDGET;
    private double totalExpense = 0.0;

    /**
     * Handle import analysis file button click event
     */
    @FXML
    public void handleImportAnalysisFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"));

        File file = fileChooser.showOpenDialog(expensePieChart.getScene().getWindow());
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
            // 读取CSV文件并生成统计数据
            Map<String, Double> categoryStatistics = CSVHandler.readCategoryStatistics(file.getPath());
            
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
            
            // 生成并显示统计摘要
            statisticsLabel.setText(ChartGenerator.generateStatisticsSummary(categoryStatistics));

            // 计算总支出并更新进度条
            totalExpense = categoryStatistics.values().stream().mapToDouble(Double::doubleValue).sum();
            updateBudgetProgress();

            // 替换原来的统计标签更新
            updateStatisticsDisplay(categoryStatistics);
            // 已移除导入成功Notification窗口
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
            // Switch to main page using page manager
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
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
            // Switch to user page using page manager
            PageManager.getInstance().switchToPage("/view/UserPage.fxml");
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
     * Update budget progress bar
     */
    private void updateBudgetProgress() {
        double progress = totalExpense / currentBudget;
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
     * Handle export PDF button click event
     */
    @FXML
    private void handleExportPdf() {
        try {
            // 创建文件选择器
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF file");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF文件", "*.pdf"));
            File file = fileChooser.showSaveDialog(expensePieChart.getScene().getWindow());
            
            if (file != null) {
                // 创建PDF文档
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                
                // 获取图表截图
                WritableImage pieChartImage = expensePieChart.snapshot(new SnapshotParameters(), null);
                WritableImage barChartImage = expenseBarChart.snapshot(new SnapshotParameters(), null);
                
                // 将图表截图转换为PDF图片
                BufferedImage pieChartBuffered = SwingFXUtils.fromFXImage(pieChartImage, null);
                BufferedImage barChartBuffered = SwingFXUtils.fromFXImage(barChartImage, null);
                
                ByteArrayOutputStream pieChartBytes = new ByteArrayOutputStream();
                ByteArrayOutputStream barChartBytes = new ByteArrayOutputStream();
                
                ImageIO.write(pieChartBuffered, "png", pieChartBytes);
                ImageIO.write(barChartBuffered, "png", barChartBytes);
                
                PDImageXObject pieChartPdfImage = PDImageXObject.createFromByteArray(document, pieChartBytes.toByteArray(), "饼图");
                PDImageXObject barChartPdfImage = PDImageXObject.createFromByteArray(document, barChartBytes.toByteArray(), "条形图");
                
                // 在PDF中绘制图表
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float imageWidth = pageWidth * 0.8f;
                float imageHeight = imageWidth * 0.75f;
                
                // 绘制饼图
                contentStream.drawImage(pieChartPdfImage, 
                        pageWidth * 0.1f, 
                        pageHeight * 0.6f, 
                        imageWidth, 
                        imageHeight);
                
                // 绘制条形图
                contentStream.drawImage(barChartPdfImage, 
                        pageWidth * 0.1f, 
                        pageHeight * 0.1f, 
                        imageWidth, 
                        imageHeight);
                
                contentStream.close();
                document.save(file);
                document.close();
                
                showInfo("Export successful", "Successfully exported PDF file: " + file.getName());
            }
        } catch (IOException e) {
            showError("Export failed", "Failed to export PDF file: " + e.getMessage());
        }
    }

    @FXML
    private void handleSetBudget() {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(currentBudget));
        dialog.setTitle("Set Budget");
        dialog.setHeaderText("Enter your monthly budget");
        dialog.setContentText("Amount:");
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            try {
                currentBudget = Double.parseDouble(amount);
                updateBudgetProgress();
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number");
            }
        });
    }

    private void updateStatisticsDisplay(Map<String, Double> categoryStatistics) {
        if (statsCardsContainer == null) {
            System.err.println("Warning: statsCardsContainer is not initialized");
            return;
        }
        
        // 清空现有卡片
        statsCardsContainer.getChildren().clear();
        
        // 创建滚动面板
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // 创建内部容器
        HBox cardsBox = new HBox(5);
        cardsBox.setAlignment(Pos.CENTER_LEFT);
        cardsBox.setStyle("-fx-padding: 5;");
        
        // 创建卡片（保持原有卡片创建逻辑）
        VBox totalCard = createStatCard("总支出", 
                String.format("¥%.2f", totalExpense), 
                "#4CAF50");
        cardsBox.getChildren().add(totalCard);
        
        // 创建预算卡片
        VBox budgetCard = createStatCard("剩余预算", 
                String.format("¥%.2f", currentBudget - totalExpense), 
                "#2196F3");
        cardsBox.getChildren().add(budgetCard);
        
        // 创建类别数卡片
        VBox categoriesCard = createStatCard("消费类别", 
                String.valueOf(categoryStatistics.size()), 
                "#FF9800");
        cardsBox.getChildren().add(categoriesCard);
        
        scrollPane.setContent(cardsBox);
        statsCardsContainer.getChildren().add(scrollPane);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(3);  // 减少间距
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8; -fx-padding: 8;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    /**
     * Handle Bill Payment Analysis button click event
     * Navigate to Bill Payment Analysis page
     */
    @FXML
    private void handleBillPaymentAnalysis() {
        try {
            // Switch to bill payment page using improved page manager
            PageManager.getInstance().switchToPage("/view/BillPaymentPage.fxml");
        } catch (IOException e) {
            showError("Navigation Failed", "Failed to load Bill Payment Analysis page: " + e.getMessage());
        } catch (Exception e) {
            showError("Navigation Failed", "Unexpected error: " + e.getMessage());
        }
    }
}  // 这是类的结束大括号
