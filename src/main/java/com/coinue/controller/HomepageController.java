package com.coinue.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;

// 在文件顶部的import部分添加以下导入语句
import javafx.geometry.Insets;

// 在import部分添加所需的类
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.FileWriter;
import java.io.PrintWriter;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class HomepageController {
    @FXML
    private ImageView logoImage;
    @FXML
    private VBox budgetList;
    @FXML
    private VBox reminderList;

    @FXML
    public void initialize() {
        try {
            // Check if logo image exists
            String logoPath = "/images/coinue_logo.png";
            System.out.println("Attempting to load image: " + logoPath);
            var resourceUrl = getClass().getResource(logoPath);
            if (resourceUrl != null) {
                System.out.println("Found image resource: " + resourceUrl.toString());
                logoImage.setImage(new Image(getClass().getResourceAsStream(logoPath)));
            } else {
                System.out.println("Warning: Logo image not found at " + logoPath);
            }
            
            // 添加预算项目示例
            addBudgetItem("Shopping", "544", "10 Jan 2022", "Cash");
            addBudgetItem("Restaurant", "54,417.80", "11 Jan 2022", "Card");
            addBudgetItem("Transport", "54.00", "12 Jan 2022", "Online");
            
            // 添加还款提醒示例
            addReminderItem("Rent", "7000RMB", "29 March 2025", "10 days left");
            addReminderItem("credit card", "3000RMB", "25 March 2025", "10 days left");
            addReminderItem("express", "70RMB", "25 March 2025", "10 days left");
            
            // 创建按钮
            Button importButton = new Button("Import");
            importButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-padding: 10;");
            importButton.setOnAction(e -> showImportDialog());
            
            Button addReminderButton = new Button("Add Reminder");
            addReminderButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-padding: 10;");
            addReminderButton.setOnAction(e -> showAddReminderDialog());
            
            // 创建按钮容器
            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(importButton, addReminderButton);
            VBox.setMargin(buttonBox, new Insets(0, 0, 20, 0));
            
            // 将按钮容器添加到界面上方
            reminderList.getChildren().add(0, buttonBox);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddReminderDialog() {
        // 创建对话框
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Enter reminder details");

        // 添加确认和取消按钮
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField typeField = new TextField();
        TextField amountField = new TextField();
        TextField dateField = new TextField();
        TextField daysLeftField = new TextField();

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateField, 1, 2);
        grid.add(new Label("Days Left:"), 0, 3);
        grid.add(daysLeftField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // 显示对话框并处理结果
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String type = typeField.getText();
            String amount = amountField.getText();
            String date = dateField.getText();
            String daysLeft = daysLeftField.getText();

            // 添加到界面
            addReminderItem(type, amount, date, daysLeft);

            // 保存到CSV文件
            saveToCSV(type, amount, date, daysLeft);
        }
    }

    private void saveToCSV(String type, String amount, String date, String daysLeft) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/main/resources/data/repayment_reminder.csv", true))) {
            // 如果文件为空，添加表头
            File file = new File("src/main/resources/data/repayment_reminder.csv");
            if (file.length() == 0) {
                writer.println("Type,Amount,Date,Additional");
            }
            // 添加新数据
            writer.println(String.format("%s,%s,%s,%s", type, amount, date, daysLeft));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImportDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Confirmation");
        alert.setHeaderText("Do you want to update the reminder list?");
        alert.setContentText("This will load data from repayment_reminder.csv");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            importCSV();
        }
    }

    private void importCSV() {
        try {
            File csvFile = new File("src/main/resources/data/repayment_reminder.csv");
            if (csvFile.exists()) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("选择CSV文件");
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
                );
                
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                        String line;
                        boolean isFirstLine = true;
                        
                        // 清除现有数据
                        budgetList.getChildren().clear();
                        reminderList.getChildren().clear();
                        
                        // 重新添加导入按钮
                        Button importButton = new Button("Import CSV");
                        importButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; -fx-padding: 10;");
                        importButton.setOnAction(e -> importCSV());
                        budgetList.getChildren().add(importButton);
                        
                        while ((line = br.readLine()) != null) {
                            if (isFirstLine) {
                                isFirstLine = false;
                                continue; // 跳过表头
                            }
                            
                            String[] data = line.split(",");
                            if (data.length >= 4) {
                                String type = data[0].trim();
                                String amount = data[1].trim();
                                String date = data[2].trim();
                                String additional = data[3].trim();
                                
                                // 根据类型决定添加到预算列表还是提醒列表
                                if (type.equalsIgnoreCase("Shopping") || 
                                    type.equalsIgnoreCase("Restaurant") || 
                                    type.equalsIgnoreCase("Transport")) {
                                    addBudgetItem(type, amount, date, additional);
                                } else {
                                    addReminderItem(type, amount, date, additional);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBudgetItem(String category, String amount, String date, String paymentType) {
        HBox item = new HBox(10);
        item.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8;");
        
        VBox categoryBox = new VBox(5);
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons/" + category.toLowerCase() + ".png")));
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        
        Label categoryLabel = new Label(category);
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");
        
        categoryBox.getChildren().addAll(icon, categoryLabel, dateLabel);
        
        Label amountLabel = new Label(amount);
        amountLabel.setStyle("-fx-font-weight: bold;");
        
        Label typeLabel = new Label(paymentType);
        typeLabel.setStyle("-fx-text-fill: #666666;");
        
        item.getChildren().addAll(categoryBox, amountLabel, typeLabel);
        budgetList.getChildren().add(item);
    }

    private void addReminderItem(String type, String amount, String date, String daysLeft) {
        HBox item = new HBox(15);
        item.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/icons/" + type.toLowerCase().replace(" ", "_") + ".png")));
        icon.setFitWidth(40);
        icon.setFitHeight(40);
        
        VBox infoBox = new VBox(5);
        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-font-weight: bold;");
        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-text-fill: #666666;");
        infoBox.getChildren().addAll(typeLabel, dateLabel);
        
        VBox amountBox = new VBox(5);
        Label amountLabel = new Label(amount);
        amountLabel.setStyle("-fx-font-weight: bold;");
        Label daysLeftLabel = new Label(daysLeft);
        daysLeftLabel.setStyle("-fx-text-fill: #666666;");
        amountBox.getChildren().addAll(amountLabel, daysLeftLabel);
        
        item.getChildren().addAll(icon, infoBox, amountBox);
        reminderList.getChildren().add(item);
    }

    @FXML
    private void switchToHomepage() {
        // 当前已在Homepage，无需切换
    }

    @FXML
    private void switchToAnalysis() {
        // 实现切换到Analysis页面的逻辑
    }

    @FXML
    private void switchToDashboard() {
        // 实现切换到Dashboard页面的逻辑
    }
}