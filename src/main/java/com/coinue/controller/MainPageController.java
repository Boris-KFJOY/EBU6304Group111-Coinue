package com.coinue.controller;

import com.coinue.model.Budget;
import com.coinue.model.ExpenseRecord;
import com.coinue.model.PaymentReminder;
import com.coinue.util.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 主页面控制器
 * 负责管理和控制应用程序的主界面，包括预算显示、还款提醒和消费记录等功能
 */
public class MainPageController {

    // FXML 注入的 UI 组件
    /**
     * 预算列表视图，显示所有预算项目
     */
    @FXML
    private VBox budgetContainer;

    /**
     * 还款提醒容器，显示所有待还款项目
     */
    @FXML
    private VBox reminderContainer;

    /**
     * 用于触发记录消费的图标视图
     */
    @FXML
    private ImageView coinImageView;

    /**
     * 消费记录表格，显示所有消费明细
     */
    @FXML
    private TableView<ExpenseRecord> expenseTableView;

    // 表格列定义
    /**
     * 消费日期列
     */
    @FXML
    private TableColumn<ExpenseRecord, String> dateColumn;

    /**
     * 消费类别列
     */
    @FXML
    private TableColumn<ExpenseRecord, String> categoryColumn;

    /**
     * 消费名称列
     */
    @FXML
    private TableColumn<ExpenseRecord, String> nameColumn;

    /**
     * 消费金额列
     */
    @FXML
    private TableColumn<ExpenseRecord, Double> amountColumn;

    // 数据集合
    /**
     * 预算数据集合
     */
    private ObservableList<Budget> budgets;

    /**
     * 还款提醒数据集合
     */
    private ObservableList<PaymentReminder> reminders;

    /**
     * 消费记录数据集合
     */
    private ObservableList<ExpenseRecord> expenseRecords;

    /**
     * 初始化方法，在FXML加载后自动调用
     * 负责初始化所有数据和UI组件的显示
     */
    @FXML
    public void initialize() {
        // 初始化数据
        budgets = FXCollections.observableArrayList(DataManager.loadBudgets());
        reminders = FXCollections.observableArrayList(DataManager.loadReminders());
        expenseRecords = FXCollections.observableArrayList(DataManager.loadExpenseRecords());

        // 设置预算卡片式显示
        updateBudgetCards();

        // 设置还款提醒卡片式显示
        updateReminderCards();
        
        // 设置消费记录表格
        initializeExpenseTable();
    }
    
    /**
     * 初始化消费记录表格的配置
     * 设置各列的数据绑定和显示格式
     */
    private void initializeExpenseTable() {
        // 设置表格列的单元格值工厂
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        // 设置金额列的单元格工厂，用于格式化金额显示
        amountColumn.setCellFactory(column -> new TableCell<ExpenseRecord, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    ExpenseRecord record = getTableView().getItems().get(getIndex());
                    setText(String.format("%s %.2f", record.getCurrency(), item));
                }
            }
        });
        
        // 设置表格数据
        expenseTableView.setItems(expenseRecords);
    }

    /**
     * 处理金币图标点击事件
     * 打开手动记录消费的对话框
     */
    @FXML
    private void handleCoinClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManualEntryDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("记录消费");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(coinImageView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ManualEntryDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainPageController(this);

            dialogStage.showAndWait();
            
            // 对话框关闭后刷新消费记录表格
            refreshExpenseRecords();
        } catch (IOException e) {
            showError("打开记录窗口失败", e.getMessage());
        }
    }

    /**
     * 处理添加预算按钮点击事件
     * 打开添加预算的对话框
     */
    @FXML
    private void handleAddBudget() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BudgetDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("添加预算");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(budgetContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            BudgetDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainPageController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace(); // 添加这行来打印详细错误信息
            showError("打开预算窗口失败", e.getMessage());
        }
    }

    /**
     * 处理添加还款提醒按钮点击事件
     * 打开添加还款提醒的对话框
     */
    @FXML
    private void handleAddReminder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReminderDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("添加还款提醒");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reminderContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ReminderDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainPageController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            showError("打开提醒窗口失败", e.getMessage());
        }
    }

    /**
     * 添加新的预算项目
     * @param budget 要添加的预算对象
     */
    public void addBudget(Budget budget) {
        budgets.add(budget);
        DataManager.saveBudgets(List.copyOf(budgets));
        updateBudgetCards(); // 更新预算卡片显示
    }
    
    /**
     * 更新预算卡片显示
     * 根据当前的预算列表创建卡片式UI
     */
    private void updateBudgetCards() {
        budgetContainer.getChildren().clear();
        
        for (Budget budget : budgets) {
            HBox card = new HBox();
            card.setSpacing(15);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            
            // 创建图标
            String iconPath = BudgetDialogController.getCategoryIconPath(budget.getCategory());
            Image iconImage = null;
            try {
                iconImage = new Image(getClass().getResourceAsStream(iconPath));
            } catch (Exception e) {
                try {
                    iconImage = new Image(getClass().getResourceAsStream("/images/icons/other.png"));
                } catch (Exception ex) {
                    iconImage = null;
                }
            }
            
            ImageView iconView;
            if (iconImage != null && !iconImage.isError()) {
                iconView = new ImageView(iconImage);
            } else {
                iconView = new ImageView();
            }
            
            iconView.setFitHeight(50);
            iconView.setFitWidth(50);
            iconView.setPreserveRatio(true);
            
            // 创建图标背景
            StackPane iconContainer = new StackPane(iconView);
            iconContainer.setStyle("-fx-background-color: " + BudgetDialogController.getCategoryColor(budget.getCategory()) + "; -fx-background-radius: 10;");
            iconContainer.setPadding(new Insets(10));
            
            // 创建信息区域
            VBox infoContainer = new VBox();
            infoContainer.setSpacing(5);
            infoContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            // 预算类别
            Label categoryLabel = new Label(budget.getCategory());
            categoryLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            
            // 使用百分比
            double percentage = budget.getUsagePercentage();
            Label percentageLabel = new Label(String.format("%.1f%% used", percentage));
            percentageLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #757575;");
            
            infoContainer.getChildren().addAll(categoryLabel, percentageLabel);
            
            // 创建金额区域
            VBox amountContainer = new VBox();
            amountContainer.setSpacing(5);
            amountContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            HBox.setHgrow(amountContainer, javafx.scene.layout.Priority.ALWAYS);
            
            // 已用/总额
            Label amountLabel = new Label(String.format("%s%.2f / %s%.2f", 
                    budget.getCurrency(), budget.getSpentAmount(),
                    budget.getCurrency(), budget.getAmount()));
            amountLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            
            // 进度条
            ProgressBar progressBar = new ProgressBar(percentage / 100);
            progressBar.setPrefWidth(100);
            progressBar.setStyle("-fx-accent: " + getProgressBarColor(percentage) + ";");
            
            amountContainer.getChildren().addAll(amountLabel, progressBar);
            
            // 添加删除按钮
            Button deleteButton = new Button("×");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 16; -fx-cursor: hand;");
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 16; -fx-cursor: hand;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 16; -fx-cursor: hand;"));
            
            // 添加删除功能
            deleteButton.setOnAction(e -> {
                budgets.remove(budget);
                DataManager.saveBudgets(List.copyOf(budgets));
                updateBudgetCards();
            });
            
            // 组装卡片
            card.getChildren().addAll(iconContainer, infoContainer, amountContainer, deleteButton);
            
            budgetContainer.getChildren().add(card);
        }
    }
    
    /**
     * 根据百分比获取进度条颜色
     * @param percentage 使用百分比
     * @return 颜色字符串
     */
    private String getProgressBarColor(double percentage) {
        if (percentage < 50) {
            return "#4CAF50"; // 绿色
        } else if (percentage < 80) {
            return "#FFC107"; // 黄色
        } else {
            return "#F44336"; // 红色
        }
    }

    /**
     * 添加新的还款提醒
     * @param reminder 要添加的还款提醒对象
     */
    public void addReminder(PaymentReminder reminder) {
        reminders.add(reminder);
        DataManager.saveReminders(List.copyOf(reminders));
        updateReminderCards(); // 更新卡片显示
    }
    
    /**
     * 更新还款提醒卡片显示
     * 根据当前的提醒列表创建卡片式UI
     */
    private void updateReminderCards() {
        reminderContainer.getChildren().clear();
        LocalDate currentDate = LocalDate.now();
        
        for (PaymentReminder reminder : reminders) {
            HBox card = new HBox();
            card.setSpacing(15);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            
            // 创建图标
            String iconPath = reminder.getIconPath();
            Image iconImage;
            try {
                iconImage = new Image(getClass().getResourceAsStream(iconPath));
                if (iconImage.isError()) {
                    // 如果加载失败，使用默认图标
                    iconImage = new Image(getClass().getResourceAsStream("/images/credit_card_icon.png"));
                }
            } catch (Exception e) {
                // 如果出现异常，使用默认图标
                iconImage = new Image(getClass().getResourceAsStream("/images/credit_card_icon.png"));
            }
            
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitHeight(50);
            iconView.setFitWidth(50);
            iconView.setPreserveRatio(true);
            
            // 创建图标背景
            StackPane iconContainer = new StackPane(iconView);
            iconContainer.setStyle("-fx-background-color: linear-gradient(to right, rgb(177, 214, 244), rgb(118, 189, 255)); -fx-background-radius: 10;");
            iconContainer.setPadding(new Insets(10));
            
            // 创建信息区域
            VBox infoContainer = new VBox();
            infoContainer.setSpacing(5);
            infoContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            // 平台名称
            Label platformLabel = new Label(reminder.getPlatform());
            platformLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            
            // 到期日期
            Label dateLabel = new Label(reminder.getDueDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #757575;");
            
            infoContainer.getChildren().addAll(platformLabel, dateLabel);
            
            // 创建金额和剩余天数区域
            VBox amountContainer = new VBox();
            amountContainer.setSpacing(5);
            amountContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            HBox.setHgrow(amountContainer, javafx.scene.layout.Priority.ALWAYS);
            
            // 金额
            Label amountLabel = new Label(String.format("%.0fRMB", reminder.getAmount()));
            amountLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            
            // 剩余天数
            long daysUntilDue = reminder.getDaysUntilDue(currentDate);
            Label daysLabel = new Label(String.format("%d days left", daysUntilDue));
            daysLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #757575;");
            
            amountContainer.getChildren().addAll(amountLabel, daysLabel);
            
            // 组装卡片
            // 添加删除按钮
            Button deleteButton = new Button("×");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 16; -fx-cursor: hand;");
            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4444; -fx-font-size: 16; -fx-cursor: hand;"));
            deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 16; -fx-cursor: hand;"));
            
            // 添加删除功能
            deleteButton.setOnAction(e -> {
                reminders.remove(reminder);
                DataManager.saveReminders(List.copyOf(reminders));
                updateReminderCards();
            });
            
            // 修改卡片组装顺序，添加删除按钮
            card.getChildren().addAll(iconContainer, infoContainer, amountContainer, deleteButton);
            
            reminderContainer.getChildren().add(card);
        }
    }

    /**
     * 处理主页导航按钮点击事件
     * 切换到主页视图
     */
    @FXML
    private void handleHomeNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BillPaymentPage.fxml"));
            Parent root = loader.load();
            Scene scene = budgetContainer.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载主页：" + e.getMessage());
        }
    }

    /**
     * 处理分析页面导航按钮点击事件
     * 切换到分析页面视图
     */
    @FXML
    private void handleAnalysisNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AnalysisPage.fxml"));
            Parent root = loader.load();
            Scene scene = budgetContainer.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载分析页面：" + e.getMessage());
        }
    }

    /**
     * 处理用户页面导航按钮点击事件
     * 切换到用户页面视图
     */
    @FXML
    private void handleUserNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserPage.fxml"));
            Parent root = loader.load();
            Scene scene = budgetContainer.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载用户页面：" + e.getMessage());
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * 处理手动记录按钮点击事件
     * 打开手动记录消费的对话框
     */
    @FXML
    private void handleManualEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManualEntryDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("记录消费");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // 修改这行，使用 budgetContainer 作为父窗口
            dialogStage.initOwner(budgetContainer.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ManualEntryDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainPageController(this);

            dialogStage.showAndWait();
            
            // 对话框关闭后刷新消费记录表格
            refreshExpenseRecords();
        } catch (IOException e) {
            showError("打开记录窗口失败", e.getMessage());
        }
    }
    
    /**
     * 刷新消费记录表格数据
     */
    public void refreshExpenseRecords() {
        expenseRecords.clear();
        expenseRecords.addAll(DataManager.loadExpenseRecords());
    }
    
    /**
     * 添加消费记录
     * @param record 要添加的消费记录对象
     */
    public void addExpenseRecord(ExpenseRecord record) {
        expenseRecords.add(record);
        DataManager.saveExpenseRecords(List.copyOf(expenseRecords));
    }
}