package com.coinue.controller;

import com.coinue.model.ExpenseRecord;
import com.coinue.util.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ExpenseRecordPageController {
    
    @FXML
    private ListView<ExpenseRecord> expenseListView;
    
    private ObservableList<ExpenseRecord> expenseRecords = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        // 加载已有记录
        loadExpenseRecords();
        
        // 设置ListView的单元格工厂
        expenseListView.setItems(expenseRecords);
        expenseListView.setCellFactory(param -> new ListCell<ExpenseRecord>() {
            @Override
            protected void updateItem(ExpenseRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s %.2f %s (%s)",
                            item.getDate(),
                            item.getName(),
                            item.getAmount(),
                            item.getCurrency(),
                            item.getCategory()));
                }
            }
        });
    }
    
    @FXML
    private void handleAddRecord() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ManualEntryDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("添加记录");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(expenseListView.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ManualEntryDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setExpenseRecordPageController(this);

            dialogStage.showAndWait();
        } catch (IOException e) {
            showError("打开添加窗口失败", e.getMessage());
        }
    }
    
    public void addExpenseRecord(ExpenseRecord record) {
        expenseRecords.add(record);
        DataManager.saveExpenseRecords(new ArrayList<>(expenseRecords));
    }
    
    private void loadExpenseRecords() {
        expenseRecords.addAll(DataManager.loadExpenseRecords());
    }
    
    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPage.fxml"));
            Parent root = loader.load();
            Scene scene = expenseListView.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("导航失败", "无法返回主页：" + e.getMessage());
        }
    }
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}