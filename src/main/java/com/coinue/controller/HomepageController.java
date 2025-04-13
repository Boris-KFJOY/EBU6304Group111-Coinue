package com.coinue.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
// 删除未使用的FileChooser导入
import javafx.geometry.Insets;
import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Dialog;
import java.io.IOException;

public class HomepageController implements Initializable {
    // FXML 注入的UI组件
    @FXML private ImageView logoImage;
    @FXML private VBox budgetList;
    @FXML private VBox reminderList;
    @FXML private AnchorPane rootPane;

    // CSV文件路径常量
    private static final String CSV_FILE_PATH = "src/main/resources/data/repayment_reminder.csv";
    private static final String ICONS_PATH = "/images/icons/";

    private Button createImportButton() {
        Button importButton = new Button("Import");
        importButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; " +
                "-fx-padding: 5 15; -fx-background-radius: 5;");
        importButton.setOnAction(e -> showImportDialog());
        return importButton;
    }

    private Button createAddReminderButton() {
        Button addButton = new Button("Add Reminder");
        addButton.setStyle("-fx-background-color: #87CEEB; -fx-text-fill: white; " +
                "-fx-padding: 5 15; -fx-background-radius: 5;");
        addButton.setOnAction(e -> showAddReminderDialog());
        return addButton;
    }

    private void addReminderItem(String type, String amount, String date, String daysLeft) {
        HBox item = new HBox(20);
        item.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 15; " +
                "-fx-background-radius: 10; -fx-margin: 5;");

        // 创建图标容器
        StackPane iconContainer = new StackPane();
        iconContainer.setStyle("-fx-min-width: 40; -fx-min-height: 40;");
        ImageView icon = createIcon(type);
        iconContainer.getChildren().add(icon);

        // 创建信息容器
        VBox infoContainer = new VBox(5);
        Label typeLabel = createStyledLabel(type, "-fx-font-weight: bold; -fx-font-size: 14;");
        Label dateLabel = createStyledLabel(date, "-fx-text-fill: #666666; -fx-font-size: 12;");
        infoContainer.getChildren().addAll(typeLabel, dateLabel);

        // 创建金额和天数容器
        VBox amountContainer = new VBox(5);
        amountContainer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Label amountLabel = createStyledLabel(amount + "RMB", "-fx-font-weight: bold; -fx-font-size: 14;");
        Label daysLeftLabel = createStyledLabel(daysLeft + " days left", "-fx-text-fill: #666666; -fx-font-size: 12;");
        amountContainer.getChildren().addAll(amountLabel, daysLeftLabel);

        // 设置HBox的对齐方式
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(infoContainer, Priority.ALWAYS);

        item.getChildren().addAll(iconContainer, infoContainer, amountContainer);
        reminderList.getChildren().add(item);
    }

    private void addBudgetItem(String category, String amount, String date, String paymentType) {
        HBox item = new HBox(15);
        item.setStyle("-fx-background-color: #F8F9FA; -fx-padding: 10; " +
                "-fx-background-radius: 8; -fx-margin: 5;");

        ImageView icon = createIcon(category);

        VBox infoBox = new VBox(5);
        Label categoryLabel = createStyledLabel(category, "-fx-font-weight: bold; -fx-font-size: 14;");
        Label dateLabel = createStyledLabel(date, "-fx-text-fill: #666666; -fx-font-size: 12;");
        infoBox.getChildren().addAll(categoryLabel, dateLabel);

        VBox amountBox = new VBox(5);
        amountBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Label amountLabel = createStyledLabel(amount, "-fx-font-weight: bold; -fx-font-size: 14;");
        Label typeLabel = createStyledLabel(paymentType, "-fx-text-fill: #666666; -fx-font-size: 12;");
        amountBox.getChildren().addAll(amountLabel, typeLabel);

        HBox.setHgrow(infoBox, Priority.ALWAYS);

        item.getChildren().addAll(icon, infoBox, amountBox);
        budgetList.getChildren().add(item);
    }

    // 数据加载和保存
    private void loadExistingData() {
        File csvFile = new File(CSV_FILE_PATH);
        if (csvFile.exists()) {
            importCSV();
        }
    }

    private void saveToCSV(String type, String amount, String date, String daysLeft) {
        File file = new File(CSV_FILE_PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (file.length() == 0) {
                writer.println("Type,Amount,Date,Additional");
            }
            writer.println(String.format("%s,%s,%s,%s", type, amount, date, daysLeft));
        } catch (IOException e) {
            showError("保存失败", "无法保存到CSV文件");
        }
    }

    // 对话框相关方法
    private void showAddReminderDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Enter reminder details");

        GridPane grid = createDialogGrid();
        TextField typeField = new TextField();
        TextField amountField = new TextField();
        TextField dateField = new TextField();
        TextField daysLeftField = new TextField();

        addFieldsToGrid(grid, typeField, amountField, dateField, daysLeftField);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        handleDialogResult(dialog, typeField, amountField, dateField, daysLeftField);
    }

    private GridPane createDialogGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    private void addFieldsToGrid(GridPane grid, TextField... fields) {
        String[] labels = {"Type:", "Amount:", "Date:", "Days Left:"};
        for (int i = 0; i < fields.length; i++) {
            grid.add(new Label(labels[i]), 0, i);
            grid.add(fields[i], 1, i);
        }
    }

    private void handleDialogResult(Dialog<ButtonType> dialog, TextField typeField,
                                    TextField amountField, TextField dateField,
                                    TextField daysLeftField) {
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String type = typeField.getText();
            String amount = amountField.getText();
            String date = dateField.getText();
            String daysLeft = daysLeftField.getText();

            addReminderItem(type, amount, date, daysLeft);
            try {
                saveToCSV(type, amount, date, daysLeft);
            } catch (Exception e) {
                showError("保存失败", "无法保存提醒项");
            }
        }
    }

    // UI项目添加方法
    private void addNewReminderItem(String type, String amount, String date, String daysLeft) {
        HBox item = new HBox(15);
        item.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");

        ImageView icon = createIcon(type);
        VBox infoBox = createInfoBox(type, date);
        VBox amountBox = createAmountBox(amount, daysLeft);

        item.getChildren().addAll(icon, infoBox, amountBox);
        reminderList.getChildren().add(item);
    }

    // 重命名方法以避免重复
    private void createBudgetListItem(String category, String amount, String date, String paymentType) {
        HBox item = new HBox(10);
        item.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8;");

        VBox categoryBox = createCategoryBox(category, date);
        Label amountLabel = createStyledLabel(amount, "-fx-font-weight: bold;");
        Label typeLabel = createStyledLabel(paymentType, "-fx-text-fill: #666666;");

        item.getChildren().addAll(categoryBox, amountLabel, typeLabel);
        budgetList.getChildren().add(item);
    }

    // 辅助方法
    private ImageView createIcon(String type) {
        String iconPath = ICONS_PATH + type.toLowerCase().replace(" ", "_") + ".png";
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(40);
        icon.setFitHeight(40);
        return icon;
    }

    private VBox createInfoBox(String type, String date) {
        VBox infoBox = new VBox(5);
        Label typeLabel = createStyledLabel(type, "-fx-font-weight: bold;");
        Label dateLabel = createStyledLabel(date, "-fx-text-fill: #666666;");
        infoBox.getChildren().addAll(typeLabel, dateLabel);
        return infoBox;
    }

    private VBox createAmountBox(String amount, String additional) {
        VBox amountBox = new VBox(5);
        Label amountLabel = createStyledLabel(amount, "-fx-font-weight: bold;");
        Label additionalLabel = createStyledLabel(additional, "-fx-text-fill: #666666;");
        amountBox.getChildren().addAll(amountLabel, additionalLabel);
        return amountBox;
    }

    private Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // 导航方法
    @FXML
    private void switchToHomepage() {
        // 当前页面，无需操作
    }

    @FXML
    private void switchToAnalysis() {
        try {
            // 加载分析页面的FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/analysis.fxml"));
            AnchorPane analysisPane = loader.load();

            // 获取当前场景并切换根节点
            javafx.scene.Scene currentScene = rootPane.getScene();
            currentScene.setRoot(analysisPane);
        } catch (IOException e) {
            showError("页面跳转失败", "无法加载分析页面");
        }
    }

    @FXML
    private void switchToDashboard() {
        // TODO: 实现仪表盘页面跳转
    }

    private void showImportDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("更新确认");
        alert.setHeaderText("是否要更新提醒列表？");
        alert.setContentText("这将从 repayment_reminder.csv 加载数据");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            importCSV();
        }
    }

    private void importCSV() {
        try {
            File csvFile = new File(CSV_FILE_PATH);
            if (csvFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                    String line;
                    boolean isFirstLine = true;

                    // 清除现有数据
                    budgetList.getChildren().clear();
                    reminderList.getChildren().clear();

                    // 重新初始化UI
// 重新初始化UI组件
                    Platform.runLater(() -> {
                        // 设置列表样式
                        budgetList.setSpacing(10);
                        budgetList.setPadding(new Insets(10));
                        reminderList.setSpacing(10);
                        reminderList.setPadding(new Insets(10));

                        // 添加标题
                        Label reminderTitle = createStyledLabel("Repayment reminder",
                                "-fx-font-size: 20; -fx-font-weight: bold;");
                        reminderList.getChildren().add(0, reminderTitle);

                        // 创建按钮容器
                        HBox buttonContainer = new HBox(10);
                        buttonContainer.getChildren().addAll(createImportButton(), createAddReminderButton());
                        reminderList.getChildren().add(1, buttonContainer);
                    });

                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        String[] data = line.split(",");
                        if (data.length >= 4) {
                            String type = data[0].trim();
                            String amount = data[1].trim();
                            String date = data[2].trim();
                            String additional = data[3].trim();

                            // 根据类型决定添加到哪个列表
                            if (isBudgetType(type)) {
                                addBudgetItem(type, amount, date, additional);
                            } else {
                                addReminderItem(type, amount, date, additional);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            showError("导入失败", "无法读取CSV文件");
        }
    }

    private boolean isBudgetType(String type) {
        return type.equalsIgnoreCase("Shopping") ||
                type.equalsIgnoreCase("Restaurant") ||
                type.equalsIgnoreCase("Transport");
    }

    private VBox createCategoryBox(String category, String date) {
        VBox categoryBox = new VBox(5);

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(ICONS_PATH + category.toLowerCase() + ".png")));
        icon.setFitWidth(30);
        icon.setFitHeight(30);

        Label categoryLabel = createStyledLabel(category, "-fx-font-weight: bold;");
        Label dateLabel = createStyledLabel(date, "-fx-text-fill: #666666; -fx-font-size: 12;");

        categoryBox.getChildren().addAll(icon, categoryLabel, dateLabel);
        return categoryBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            // 设置整体布局样式
            rootPane.setStyle("-fx-background-color: #FFFFFF;");

            // 设置列表样式
            budgetList.setSpacing(10);
            budgetList.setPadding(new Insets(10));
            reminderList.setSpacing(10);
            reminderList.setPadding(new Insets(10));

            // 添加标题
            Label reminderTitle = createStyledLabel("Repayment reminder",
                    "-fx-font-size: 20; -fx-font-weight: bold;");
            reminderList.getChildren().add(0, reminderTitle);

            // 创建按钮容器
            HBox buttonContainer = new HBox(10);
            buttonContainer.getChildren().addAll(createImportButton(), createAddReminderButton());
            reminderList.getChildren().add(1, buttonContainer);

            // 加载现有数据
            loadExistingData();
        });
    }
}