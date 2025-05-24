package com.coinue.controller;

import com.coinue.model.User;
import com.coinue.model.UserAnalysisData;
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
import javafx.scene.control.Button;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
    @FXML
    private Button clearDataButton;    // 清除数据按钮
    @FXML
    private Label userDataStatusLabel; // 用户数据状态标签

    private static final double DEFAULT_BUDGET = 10000.0; // Default budget amount
    private double currentBudget = DEFAULT_BUDGET;
    private double totalExpense = 0.0;
    
    // 当前用户的分析数据
    private UserAnalysisData currentUserAnalysisData;
    // 当前显示的类别统计数据
    private Map<String, Double> currentCategoryStatistics;

    /**
     * 初始化方法，页面加载时自动调用
     */
    @FXML
    public void initialize() {
        // 初始化变量
        currentCategoryStatistics = new HashMap<>();
        
        // 检查用户登录状态并加载数据
        loadUserDataOnInitialize();
        
        // 更新UI状态
        updateUserDataStatusDisplay();
    }

    /**
     * 页面初始化时加载用户数据
     */
    private void loadUserDataOnInitialize() {
        User currentUser = User.getCurrentUser();
        
        if (currentUser == null) {
            // 用户未登录，显示提示信息
            showUserNotLoggedInState();
            return;
        }
        
        try {
            // 尝试加载用户的历史分析数据
            currentUserAnalysisData = currentUser.loadAnalysisData(UserAnalysisData.class);
            
            if (currentUserAnalysisData != null && !currentUserAnalysisData.getCategoryExpenses().isEmpty()) {
                // 用户有历史数据，自动加载显示
                loadUserAnalysisData();
                fileNameLabel.setText("已加载用户历史数据 - " + currentUser.getUsername());
            } else {
                // 用户没有历史数据，创建新的数据对象
                currentUserAnalysisData = new UserAnalysisData();
                fileNameLabel.setText("欢迎 " + currentUser.getUsername() + "，请导入CSV文件开始分析");
            }
            
        } catch (Exception e) {
            System.err.println("加载用户分析数据失败: " + e.getMessage());
            currentUserAnalysisData = new UserAnalysisData();
            fileNameLabel.setText("欢迎 " + currentUser.getUsername() + "，数据加载失败，请重新导入");
        }
    }

    /**
     * 显示用户未登录状态
     */
    private void showUserNotLoggedInState() {
        fileNameLabel.setText("请先登录以保存和加载个人分析数据");
        statisticsLabel.setText("用户未登录 - 导入的数据将无法保存");
        
        // 清空图表
        expensePieChart.getData().clear();
        expenseBarChart.getData().clear();
        
        // 重置预算信息
        totalExpense = 0.0;
        currentBudget = DEFAULT_BUDGET;
        updateBudgetProgress();
        
        // 清空统计卡片
        if (statsCardsContainer != null) {
            statsCardsContainer.getChildren().clear();
        }
    }

    /**
     * 加载用户历史分析数据到UI
     */
    private void loadUserAnalysisData() {
        if (currentUserAnalysisData == null) return;
        
        // 获取类别支出数据
        currentCategoryStatistics = new HashMap<>(currentUserAnalysisData.getCategoryExpenses());
        
        if (!currentCategoryStatistics.isEmpty()) {
            // 更新饼图
            expensePieChart.setData(ChartGenerator.generateExpensePieChartData(currentCategoryStatistics));
            
            // 更新条形图
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("支出金额");
            currentCategoryStatistics.forEach((category, amount) ->
                series.getData().add(new XYChart.Data<>(category, amount)));
            expenseBarChart.getData().clear();
            expenseBarChart.getData().add(series);
            
            // 更新统计信息
            statisticsLabel.setText(ChartGenerator.generateStatisticsSummary(currentCategoryStatistics));
            
            // 计算总支出
            totalExpense = currentCategoryStatistics.values().stream().mapToDouble(Double::doubleValue).sum();
            
            // 从用户数据获取预算信息（如果有的话）
            var budgetUsage = currentUserAnalysisData.getBudgetUsage().get("总预算");
            if (budgetUsage != null) {
                currentBudget = budgetUsage.getBudgetLimit();
            }
            
            // 更新预算进度和统计显示
            updateBudgetProgress();
            updateStatisticsDisplay(currentCategoryStatistics);
        }
    }

    /**
     * 更新用户数据状态显示
     */
    private void updateUserDataStatusDisplay() {
        if (userDataStatusLabel == null) return;
        
        User currentUser = User.getCurrentUser();
        if (currentUser == null) {
            userDataStatusLabel.setText("状态: 未登录");
            userDataStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
        } else {
            userDataStatusLabel.setText("当前用户: " + currentUser.getUsername());
            userDataStatusLabel.setStyle("-fx-text-fill: #51cf66;");
        }
    }

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
            // 检查用户登录状态
            User currentUser = User.getCurrentUser();
            
            // 读取CSV文件并生成统计数据
            Map<String, Double> categoryStatistics = CSVHandler.readCategoryStatistics(file.getPath());
            
            // 保存当前统计数据
            currentCategoryStatistics = new HashMap<>(categoryStatistics);
            
            // 更新文件名标签
            if (currentUser != null) {
                fileNameLabel.setText("已导入: " + file.getName() + " (用户: " + currentUser.getUsername() + ")");
            } else {
                fileNameLabel.setText("已导入: " + file.getName() + " (未登录用户)");
            }

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

            // 更新统计显示
            updateStatisticsDisplay(categoryStatistics);
            
            // 如果用户已登录，保存数据到用户目录
            if (currentUser != null) {
                saveUserAnalysisData(categoryStatistics, file);
                showInfo("导入成功", "CSV文件已导入并保存到您的个人数据中");
            } else {
                showInfo("导入成功", "CSV文件已导入，但未保存（请登录以保存数据）");
            }
            
        } catch (IOException e) {
            showError("导入失败", "无法读取CSV文件：" + e.getMessage());
        } catch (Exception e) {
            showError("分析失败", "数据分析错误：" + e.getMessage());
        }
    }

    /**
     * 保存用户分析数据
     * @param categoryStatistics 类别统计数据
     * @param originalFile 原始CSV文件
     */
    private void saveUserAnalysisData(Map<String, Double> categoryStatistics, File originalFile) {
        User currentUser = User.getCurrentUser();
        if (currentUser == null) return;
        
        try {
            // 如果当前用户分析数据对象为空，创建新的
            if (currentUserAnalysisData == null) {
                currentUserAnalysisData = new UserAnalysisData();
            }
            
            // 更新分析数据
            currentUserAnalysisData.setCategoryExpenses(categoryStatistics);
            currentUserAnalysisData.setTotalExpenses(totalExpense);
            
            // 更新预算使用情况
            currentUserAnalysisData.updateBudgetUsage("总预算", currentBudget, totalExpense);
            
            // 保存分析数据到用户目录
            boolean saved = currentUser.saveAnalysisData(currentUserAnalysisData);
            
            if (saved) {
                // 备份原始CSV文件到用户目录
                backupCsvFileToUserDirectory(originalFile, currentUser);
                System.out.println("用户分析数据已保存成功");
            } else {
                System.err.println("保存用户分析数据失败");
            }
            
        } catch (Exception e) {
            System.err.println("保存用户分析数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 备份CSV文件到用户目录
     * @param csvFile 原始CSV文件
     * @param user 当前用户
     */
    private void backupCsvFileToUserDirectory(File csvFile, User user) {
        try {
            // 创建用户CSV备份目录
            File userCsvDir = new File(user.getUserDataPath() + "/imported_csv_files");
            if (!userCsvDir.exists()) {
                userCsvDir.mkdirs();
            }
            
            // 生成带时间戳的文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = timestamp + "_" + csvFile.getName();
            File backupFile = new File(userCsvDir, backupFileName);
            
            // 复制文件
            Files.copy(csvFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("CSV文件已备份到: " + backupFile.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("备份CSV文件失败: " + e.getMessage());
        }
    }
    

    /**
     * Handle navigation to home page
     */
    @FXML
    private void handleHomeNav() {
        try {
            // 切换到主页面前保存当前数据
            saveCurrentDataIfLoggedIn();
            PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法加载主页面: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        // Already in analysis page, no action needed
    }

    @FXML
    private void handleUserNav() {
        try {
            // 切换到用户页面前保存当前数据
            saveCurrentDataIfLoggedIn();
            PageManager.getInstance().switchToPage("/view/UserPage.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法加载用户页面: " + e.getMessage());
        }
    }

    /**
     * 如果用户已登录，保存当前数据
     */
    private void saveCurrentDataIfLoggedIn() {
        User currentUser = User.getCurrentUser();
        if (currentUser != null && currentUserAnalysisData != null) {
            try {
                // 更新当前数据状态
                if (!currentCategoryStatistics.isEmpty()) {
                    currentUserAnalysisData.setCategoryExpenses(currentCategoryStatistics);
                    currentUserAnalysisData.setTotalExpenses(totalExpense);
                    currentUserAnalysisData.updateBudgetUsage("总预算", currentBudget, totalExpense);
                    currentUser.saveAnalysisData(currentUserAnalysisData);
                }
            } catch (Exception e) {
                System.err.println("保存用户数据失败: " + e.getMessage());
            }
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
        dialog.setTitle("设置预算");
        dialog.setHeaderText("输入您的月度预算");
        dialog.setContentText("金额:");
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amount -> {
            try {
                double newBudget = Double.parseDouble(amount);
                if (newBudget < 0) {
                    showError("输入错误", "预算金额不能为负数");
                    return;
                }
                
                currentBudget = newBudget;
                updateBudgetProgress();
                
                // 如果用户已登录，保存预算设置
                User currentUser = User.getCurrentUser();
                if (currentUser != null && currentUserAnalysisData != null) {
                    currentUserAnalysisData.updateBudgetUsage("总预算", currentBudget, totalExpense);
                    currentUser.saveAnalysisData(currentUserAnalysisData);
                    showInfo("预算已设置", "预算金额已保存到您的个人数据中");
                } else {
                    showInfo("预算已设置", "预算已设置，但未保存（请登录以保存数据）");
                }
                
            } catch (NumberFormatException e) {
                showError("输入错误", "请输入有效的数字");
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
        
        // 创建统计卡片
        VBox totalCard = createStatCard("总支出", 
                String.format("¥%.2f", totalExpense), 
                "#4CAF50");
        statsCardsContainer.getChildren().add(totalCard);
        
        // 创建预算卡片
        VBox budgetCard = createStatCard("剩余预算", 
                String.format("¥%.2f", currentBudget - totalExpense), 
                "#2196F3");
        statsCardsContainer.getChildren().add(budgetCard);
        
        // 创建类别数卡片
        VBox categoriesCard = createStatCard("消费类别", 
                String.valueOf(categoryStatistics.size()), 
                "#FF9800");
        statsCardsContainer.getChildren().add(categoriesCard);
        
        // 如果有分类数据，创建最大类别卡片
        if (!categoryStatistics.isEmpty()) {
            String topCategory = categoryStatistics.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> entry.getKey() + " (¥" + String.format("%.2f", entry.getValue()) + ")")
                    .orElse("无数据");
            
            VBox topCategoryCard = createStatCard("最大支出类别", topCategory, "#E91E63");
            statsCardsContainer.getChildren().add(topCategoryCard);
        }
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
     * 处理清除数据按钮点击事件
     */
    @FXML
    private void handleClearData() {
        User currentUser = User.getCurrentUser();
        
        if (currentUser == null) {
            showError("操作失败", "请先登录以管理个人数据");
            return;
        }
        
        // 确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认清除");
        confirmAlert.setHeaderText("清除所有分析数据");
        confirmAlert.setContentText("此操作将清除您的所有分析数据，包括统计图表和历史记录。确定要继续吗？");
        
        Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            try {
                // 清除内存中的数据
                currentUserAnalysisData = new UserAnalysisData();
                currentCategoryStatistics.clear();
                totalExpense = 0.0;
                currentBudget = DEFAULT_BUDGET;
                
                // 清除UI显示
                expensePieChart.getData().clear();
                expenseBarChart.getData().clear();
                statisticsLabel.setText("暂无数据");
                fileNameLabel.setText("欢迎 " + currentUser.getUsername() + "，请导入CSV文件开始分析");
                updateBudgetProgress();
                if (statsCardsContainer != null) {
                    statsCardsContainer.getChildren().clear();
                }
                
                // 保存空的数据到用户目录（覆盖原有数据）
                currentUser.saveAnalysisData(currentUserAnalysisData);
                
                showInfo("清除成功", "所有分析数据已清除");
                
            } catch (Exception e) {
                showError("清除失败", "清除数据时发生错误：" + e.getMessage());
            }
        }
    }

    /**
     * 处理刷新数据按钮点击事件
     */
    @FXML
    private void handleRefreshData() {
        User currentUser = User.getCurrentUser();
        
        if (currentUser == null) {
            showError("操作失败", "请先登录以刷新个人数据");
            return;
        }
        
        try {
            // 重新加载用户数据
            loadUserDataOnInitialize();
            updateUserDataStatusDisplay();
            
            showInfo("刷新成功", "用户数据已重新加载");
            
        } catch (Exception e) {
            showError("刷新失败", "刷新数据时发生错误：" + e.getMessage());
        }
    }

    /**
     * Handle Bill Payment Analysis button click event
     * Navigate to Bill Payment Analysis page
     */
    @FXML
    private void handleBillPaymentAnalysis() {
        try {
            // 切换页面前保存当前数据
            saveCurrentDataIfLoggedIn();
            PageManager.getInstance().switchToPage("/view/BillPaymentPage.fxml");
        } catch (IOException e) {
            showError("导航失败", "无法加载账单支付分析页面: " + e.getMessage());
        } catch (Exception e) {
            showError("导航失败", "发生意外错误: " + e.getMessage());
        }
    }
}  // 这是类的结束大括号
