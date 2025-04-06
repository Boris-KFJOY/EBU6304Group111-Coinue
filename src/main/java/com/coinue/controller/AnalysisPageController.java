package com.coinue.controller;

import com.coinue.util.CSVHandler;
import com.coinue.util.ChartGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 分析页面控制器
 * 处理消费数据分析和图表展示的逻辑
 */
public class AnalysisPageController {

    @FXML
    private PieChart expensePieChart;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label statisticsLabel;

    /**
     * 处理导入分析文件按钮点击事件
     */
    @FXML
    private void handleImportAnalysisFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择CSV文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"));

        File file = fileChooser.showOpenDialog(expensePieChart.getScene().getWindow());
        if (file != null) {
            try {
                // 读取CSV文件并生成统计数据
                Map<String, Double> categoryStatistics = CSVHandler.readCategoryStatistics(file.getPath());
                
                // 更新文件名标签
                fileNameLabel.setText("当前文件：" + file.getName());

                // 生成并显示饼图
                expensePieChart.setData(ChartGenerator.generateExpensePieChartData(categoryStatistics));
                
                // 生成并显示统计摘要
                statisticsLabel.setText(ChartGenerator.generateStatisticsSummary(categoryStatistics));

                showInfo("导入成功", "成功导入并分析数据");
            } catch (IOException e) {
                showError("导入失败", "无法读取CSV文件：" + e.getMessage());
            } catch (Exception e) {
                showError("分析失败", "数据分析错误：" + e.getMessage());
            }
        }
    }

    /**
     * 处理导航到主页
     */
    @FXML
    private void handleHomeNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
            Parent root = loader.load();
            Scene scene = expensePieChart.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载主页面：" + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        // 已在分析页面，无需操作
    }

    @FXML
    private void handleUserNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserPage.fxml"));
            Parent root = loader.load();
            Scene scene = expensePieChart.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载用户页面：" + e.getMessage());
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
     * 显示信息提示对话框
     */
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}