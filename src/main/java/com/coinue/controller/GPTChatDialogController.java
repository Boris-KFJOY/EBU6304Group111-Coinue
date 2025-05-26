package com.coinue.controller;

import com.coinue.model.ExpenseRecord;
import com.coinue.util.DataManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.concurrent.CompletableFuture;

public class GPTChatDialogController {
    
    @FXML
    private VBox chatHistoryBox;
    
    @FXML
    private TextArea userInputArea;
    
    @FXML
    private Button sendButton;

    private static final String OPENAI_API_KEY = "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";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private final HttpClient client;
    private final Gson gson = new Gson();

    public GPTChatDialogController() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @FXML
    private void initialize() {
        sendButton.setDisable(true);
        userInputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            sendButton.setDisable(newValue.trim().isEmpty());
        });
    }

    @FXML
    private void handleSend() {
        String userMessage = userInputArea.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        // 添加用户消息到聊天界面
        addMessageToChat("You", userMessage, true);
        userInputArea.clear();
        sendButton.setDisable(true);

        // 获取支出数据
        List<ExpenseRecord> expenseRecords = DataManager.loadExpenseRecords();
        String expenseData = gson.toJson(expenseRecords);

        // 构建发送给GPT的消息
        String systemPrompt = "你是一个专业的财务顾问。我将为你提供用户的支出记录数据（JSON格式）和他们的问题。" +
                            "请根据这些数据分析用户的消费情况，并给出专业的建议。支出数据：" + expenseData;

        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-4o-mini");
        requestBody.addProperty("store", true);
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 1000);

        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);

        requestBody.add("messages", messages);

        // 异步发送请求到OpenAI API
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OPENAI_API_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + OPENAI_API_KEY)
                        .header("Accept", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                System.out.println("发送请求: " + requestBody.toString());  // 调试输出
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("响应状态码: " + response.statusCode());  // 调试输出
                System.out.println("响应内容: " + response.body());  // 调试输出

                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                    String content = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();

                    Platform.runLater(() -> {
                        addMessageToChat("GPT", content, false);
                    });
                } else {
                    JsonObject errorResponse = gson.fromJson(response.body(), JsonObject.class);
                    String errorMessage = "API返回错误";
                    if (errorResponse.has("error") && errorResponse.getAsJsonObject("error").has("message")) {
                        errorMessage = errorResponse.getAsJsonObject("error").get("message").getAsString();
                    }
                    throw new RuntimeException(errorMessage + "\n完整响应: " + response.body());
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error", "连接GPT失败: " + e.toString());
                });
            }
        });
    }

    private void addMessageToChat(String sender, String message, boolean isUser) {
        HBox messageBox = new HBox(10);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        messageBox.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        TextFlow textFlow = new TextFlow();
        Text text = new Text(message);
        textFlow.getChildren().add(text);
        textFlow.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 10; -fx-padding: 10;",
                isUser ? "#DCF8C6" : "#E8E8E8"));
        
        if (isUser) {
            messageBox.getChildren().addAll(textFlow);
        } else {
            messageBox.getChildren().addAll(textFlow);
        }

        chatHistoryBox.getChildren().add(messageBox);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 