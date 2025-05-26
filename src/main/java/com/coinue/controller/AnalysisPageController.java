package com.coinue.controller;

import com.coinue.model.User;
import com.coinue.model.UserAnalysisData;
import com.coinue.util.CSVHandler;
import com.coinue.util.ChartGenerator;
import com.coinue.util.PageManager;
import javafx.fxml.FXML;

import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

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

    // Flag to indicate if the controller is running in a test environment
    public static boolean testModeActive = false;

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
                fileNameLabel.setText("Loaded user history - " + currentUser.getUsername());
            } else {
                // 用户没有历史数据，创建新的数据对象
                currentUserAnalysisData = new UserAnalysisData();
                fileNameLabel.setText("Welcome " + currentUser.getUsername() + ", please import CSV file to start analysis");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to load user analysis data: " + e.getMessage());
            currentUserAnalysisData = new UserAnalysisData();
            fileNameLabel.setText("Welcome " + currentUser.getUsername() + ", data loading failed, please re-import");
        }
    }

    /**
     * 显示用户未登录状态
     */
    private void showUserNotLoggedInState() {
        fileNameLabel.setText("Please log in to save and load personal analysis data");
        statisticsLabel.setText("User not logged in - imported data will not be saved");
        
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
            userDataStatusLabel.setText("Status: Not Logged In");
            userDataStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
        } else {
            userDataStatusLabel.setText("Current User: " + currentUser.getUsername());
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
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

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
                fileNameLabel.setText("Imported: " + file.getName() + " (User: " + currentUser.getUsername() + ")");
            } else {
                fileNameLabel.setText("Imported: " + file.getName() + " (Not Logged In)");
            }

            // 生成并显示饼图
            expensePieChart.setData(ChartGenerator.generateExpensePieChartData(categoryStatistics));
            
            // 生成并显示条形图
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Expense Amount");
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
                showInfo("Import Successful", "CSV file has been imported and saved to your personal data.");
            } else {
                showInfo("Import Successful", "CSV file has been imported, but not saved (Please log in to save data).");
            }
            
        } catch (IOException e) {
            showError("Import Failed", "Could not read CSV file: " + e.getMessage());
        } catch (Exception e) {
            showError("Analysis Failed", "Data analysis error: " + e.getMessage());
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
                System.out.println("User analysis data saved successfully.");
            } else {
                System.err.println("Failed to save user analysis data.");
            }
            
        } catch (Exception e) {
            System.err.println("Error saving user analysis data: " + e.getMessage());
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
            
            System.out.println("CSV file has been backed up to: " + backupFile.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("Failed to backup CSV file: " + e.getMessage());
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
            showError("Navigation Failed", "Could not load main page: " + e.getMessage());
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
            showError("Navigation Failed", "Could not load user page: " + e.getMessage());
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
                System.err.println("Failed to save user data: " + e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        if (testModeActive) {
            alert.show(); // Non-blocking for tests
        } else {
            alert.showAndWait(); // Blocking for normal operation
        }
    }

    /**
     * Show information dialog
     */
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (testModeActive) {
            alert.show(); // Non-blocking for tests
        } else {
            alert.showAndWait(); // Blocking for normal operation
        }
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
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
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
                
                PDImageXObject pieChartPdfImage = PDImageXObject.createFromByteArray(document, pieChartBytes.toByteArray(), "Pie Chart");
                PDImageXObject barChartPdfImage = PDImageXObject.createFromByteArray(document, barChartBytes.toByteArray(), "Bar Chart");
                
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
                
                showInfo("Export Successful", "Successfully exported PDF file: " + file.getName());
            }
        } catch (IOException e) {
            showError("Export Failed", "Failed to export PDF file: " + e.getMessage());
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
                double newBudget = Double.parseDouble(amount);
                if (newBudget < 0) {
                    showError("Input Error", "Budget amount cannot be negative.");
                    return;
                }
                
                currentBudget = newBudget;
                updateBudgetProgress();
                
                // 如果用户已登录，保存预算设置
                User currentUser = User.getCurrentUser();
                if (currentUser != null && currentUserAnalysisData != null) {
                    currentUserAnalysisData.updateBudgetUsage("总预算", currentBudget, totalExpense);
                    currentUser.saveAnalysisData(currentUserAnalysisData);
                    showInfo("Budget Set", "Budget amount has been saved to your personal data.");
                } else {
                    showInfo("Budget Set", "Budget has been set, but not saved (Please log in to save data).");
                }
                
            } catch (NumberFormatException e) {
                showError("Input Error", "Please enter a valid number.");
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
        VBox totalCard = createStatCard("Total Expense", 
                String.format("¥%.2f", totalExpense), 
                "#4CAF50");
        statsCardsContainer.getChildren().add(totalCard);
        
        // 创建预算卡片
        VBox budgetCard = createStatCard("Remaining Budget", 
                String.format("¥%.2f", currentBudget - totalExpense), 
                "#2196F3");
        statsCardsContainer.getChildren().add(budgetCard);
        
        // 创建类别数卡片
        VBox categoriesCard = createStatCard("Expense Categories", 
                String.valueOf(categoryStatistics.size()), 
                "#FF9800");
        statsCardsContainer.getChildren().add(categoriesCard);
        
        // 如果有分类数据，创建最大类别卡片
        if (!categoryStatistics.isEmpty()) {
            String topCategory = categoryStatistics.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> entry.getKey() + " (¥" + String.format("%.2f", entry.getValue()) + ")")
                    .orElse("No Data");
            
            VBox topCategoryCard = createStatCard("Top Spending Category", topCategory, "#E91E63");
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
            showError("Operation Failed", "Please log in to manage personal data.");
            return;
        }
        
        // 确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Clear");
        confirmAlert.setHeaderText("Clear All Analysis Data");
        confirmAlert.setContentText("This action will clear all your analysis data, including charts and history. Are you sure you want to continue?");
        
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
                statisticsLabel.setText("No data available");
                fileNameLabel.setText("Welcome " + currentUser.getUsername() + ", please import CSV file to start analysis");
                updateBudgetProgress();
                if (statsCardsContainer != null) {
                    statsCardsContainer.getChildren().clear();
                }
                
                // 保存空的数据到用户目录（覆盖原有数据）
                currentUser.saveAnalysisData(currentUserAnalysisData);
                
                showInfo("Clear Successful", "All analysis data has been cleared.");
                
            } catch (Exception e) {
                showError("Clear Failed", "Error clearing data: " + e.getMessage());
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
            showError("Operation Failed", "Please log in to refresh personal data.");
            return;
        }
        
        try {
            // 重新加载用户数据
            loadUserDataOnInitialize();
            updateUserDataStatusDisplay();
            
            showInfo("Refresh Successful", "User data has been reloaded.");
            
        } catch (Exception e) {
            showError("Refresh Failed", "Error refreshing data: " + e.getMessage());
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
            showError("Navigation Failed", "Could not load Bill Payment Analysis page: " + e.getMessage());
        } catch (Exception e) {
            showError("Navigation Failed", "An unexpected error occurred: " + e.getMessage());
        }
    }
}  // 这是类的结束大括号
