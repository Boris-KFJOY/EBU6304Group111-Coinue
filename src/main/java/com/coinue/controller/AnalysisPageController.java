package com.coinue.controller;

import com.coinue.util.CSVHandler;
import com.coinue.util.ChartGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;    
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
}
