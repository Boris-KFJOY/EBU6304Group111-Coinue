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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 主页面控制器
 * 处理主页面的所有交互逻辑
 */
public class MainPageController {

    @FXML
    private ListView<Budget> budgetListView;
    @FXML
    private ListView<PaymentReminder> reminderListView;
    @FXML
    private ImageView coinImageView;
    @FXML
    private TableView<ExpenseRecord> expenseTableView;
    @FXML
    private TableColumn<ExpenseRecord, String> dateColumn;
    @FXML
    private TableColumn<ExpenseRecord, String> categoryColumn;
    @FXML
    private TableColumn<ExpenseRecord, String> nameColumn;
    @FXML
    private TableColumn<ExpenseRecord, Double> amountColumn;

    private ObservableList<Budget> budgets;
    private ObservableList<PaymentReminder> reminders;
    private ObservableList<ExpenseRecord> expenseRecords;

    @FXML
    public void initialize() {
        // 初始化数据
        budgets = FXCollections.observableArrayList(DataManager.loadBudgets());
        reminders = FXCollections.observableArrayList(DataManager.loadReminders());
        expenseRecords = FXCollections.observableArrayList(DataManager.loadExpenseRecords());

        // 设置预算列表显示
        budgetListView.setItems(budgets);
        budgetListView.setCellFactory(param -> new ListCell<Budget>() {
            @Override
            protected void updateItem(Budget item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s: %.2f/%s%.2f (%.1f%%)",
                            item.getCategory(),
                            item.getSpentAmount(),
                            item.getCurrency(),
                            item.getAmount(),
                            item.getUsagePercentage()));
                }
            }
        });

        // 设置还款提醒列表显示
        reminderListView.setItems(reminders);
        reminderListView.setCellFactory(param -> new ListCell<PaymentReminder>() {
            @Override
            protected void updateItem(PaymentReminder item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    long daysUntilDue = item.getDaysUntilDue(LocalDate.now());
                    setText(String.format("%s: %.2f (还有%d天到期)",
                            item.getPlatform(),
                            item.getAmount(),
                            daysUntilDue));
                }
            }
        });
        
        // 设置消费记录表格
        initializeExpenseTable();

        // 检查并显示到期提醒
        checkDueReminders();
    }
    
    /**
     * 初始化消费记录表格
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

    @FXML
    private void handleAddBudget() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BudgetDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("添加预算");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(budgetListView.getScene().getWindow());
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

    @FXML
    private void handleAddReminder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReminderDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("添加还款提醒");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(reminderListView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ReminderDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainPageController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            showError("打开提醒窗口失败", e.getMessage());
        }
    }

    public void addBudget(Budget budget) {
        budgets.add(budget);
        DataManager.saveBudgets(List.copyOf(budgets));
    }

    public void addReminder(PaymentReminder reminder) {
        reminders.add(reminder);
        DataManager.saveReminders(List.copyOf(reminders));
    }

    private void checkDueReminders() {
        LocalDate currentDate = LocalDate.now();
        for (PaymentReminder reminder : reminders) {
            if (reminder.needsReminder(currentDate)) {
                showInfo("还款提醒",
                        String.format("%s将在%d天后到期，需要还款%.2f元",
                                reminder.getPlatform(),
                                reminder.getDaysUntilDue(currentDate),
                                reminder.getAmount()));
            }
        }
    }

    @FXML
    private void handleHomeNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
            Parent root = loader.load();
            Scene scene = budgetListView.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载主页：" + e.getMessage());
        }
    }

    @FXML
    private void handleAnalysisNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AnalysisPage.fxml"));
            Parent root = loader.load();
            Scene scene = budgetListView.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载分析页面：" + e.getMessage());
        }
    }

    @FXML
    private void handleUserNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserPage.fxml"));
            Parent root = loader.load();
            Scene scene = budgetListView.getScene();
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
     */
    @FXML
    private void handleManualEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManualEntryDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("记录消费");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // 修改这行，使用 budgetListView 作为父窗口
            dialogStage.initOwner(budgetListView.getScene().getWindow());
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
     * @param record 消费记录
     */
    public void addExpenseRecord(ExpenseRecord record) {
        expenseRecords.add(record);
        DataManager.saveExpenseRecords(List.copyOf(expenseRecords));
    }
}