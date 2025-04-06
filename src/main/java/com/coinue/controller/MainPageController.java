package com.coinue.controller;

import com.coinue.model.ExpenseRecord;
import com.coinue.util.CSVHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * 主页面控制器
 * 处理主页面的所有交互逻辑
 */
public class MainPageController {

    @FXML
    private TableView<ExpenseRecord> expenseTable;
    @FXML
    private TableColumn<ExpenseRecord, LocalDate> dateColumn;
    @FXML
    private TableColumn<ExpenseRecord, String> nameColumn;
    @FXML
    private TableColumn<ExpenseRecord, String> categoryColumn;
    @FXML
    private TableColumn<ExpenseRecord, Double> amountColumn;

    private ObservableList<ExpenseRecord> expenseData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 初始化表格列
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // 设置金额列的格式
        amountColumn.setCellFactory(column -> new TableCell<ExpenseRecord, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount));
                }
            }
        });

        // 绑定数据源
        expenseTable.setItems(expenseData);
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
            dialogStage.initOwner(expenseTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ManualEntryDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainPageController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            showError("打开记录窗口失败", e.getMessage());
        }
    }

    /**
     * 处理导入CSV按钮点击事件
     */
    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择CSV文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV文件", "*.csv"));

        File file = fileChooser.showOpenDialog(expenseTable.getScene().getWindow());
        if (file != null) {
            try {
                List<ExpenseRecord> records = CSVHandler.readExpenseRecords(file.getPath());
                expenseData.addAll(records);
                showInfo("导入成功", String.format("成功导入 %d 条记录", records.size()));
            } catch (IOException e) {
                showError("导入失败", "无法读取CSV文件：" + e.getMessage());
            } catch (Exception e) {
                showError("导入失败", "CSV文件格式错误：" + e.getMessage());
            }
        }
    }

    /**
     * 添加新的消费记录
     */
    public void addExpenseRecord(ExpenseRecord record) {
        expenseData.add(record);
    }

    /**
     * 处理导航到主页
     */
    @FXML
    private void handleHomeNav() {
        // 已在主页，无需处理
    }

    /**
     * 处理导航到分析页面
     */
    @FXML
    private void handleAnalysisNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AnalysisPage.fxml"));
            Parent root = loader.load();
            Scene scene = expenseTable.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载分析页面：" + e.getMessage());
        }
    }

    /**
     * 处理导航到用户页面
     */
    @FXML
    private void handleUserNav() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserPage.fxml"));
            Parent root = loader.load();
            Scene scene = expenseTable.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法加载用户页面：" + e.getMessage());
        }
    }

    /**
     * 显示错误提示对话框
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(header);
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