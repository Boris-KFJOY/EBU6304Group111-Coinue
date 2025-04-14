package com.coinue.util;

import com.coinue.model.Budget;
import com.coinue.model.ExpenseRecord;
import com.coinue.model.PaymentReminder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import com.google.gson.*;
import java.time.LocalDate;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String EXPENSE_FILE = DATA_DIR + "/expense.json";
    private static final String BUDGET_FILE = DATA_DIR + "/budget.json";
    private static final String REMINDER_FILE = DATA_DIR + "/reminder.json";
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
                return new JsonPrimitive(date.toString());
            }
        })
        .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
                return LocalDate.parse(json.getAsString());
            }
        })
        .setPrettyPrinting()
        .create();

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveExpenseRecords(List<ExpenseRecord> records) {
        File file = new File(EXPENSE_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(records, writer);
            }
        } catch (IOException e) {
            System.err.println("保存支出记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<ExpenseRecord> loadExpenseRecords() {
        File file = new File(EXPENSE_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<ExpenseRecord>>(){}.getType();
            List<ExpenseRecord> records = gson.fromJson(reader, type);
            return records != null ? records : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("加载支出记录失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveBudgets(List<Budget> budgets) {
        File file = new File(BUDGET_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(budgets, writer);
            }
        } catch (IOException e) {
            System.err.println("保存预算数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Budget> loadBudgets() {
        File file = new File(BUDGET_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Budget>>(){}.getType();
            List<Budget> budgets = gson.fromJson(reader, type);
            return budgets != null ? budgets : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("加载预算数据失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveReminders(List<PaymentReminder> reminders) {
        File file = new File(REMINDER_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(reminders, writer);
            }
        } catch (IOException e) {
            System.err.println("保存还款提醒失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<PaymentReminder> loadReminders() {
        File file = new File(REMINDER_FILE);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            if (content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            Type type = new TypeToken<List<PaymentReminder>>(){}.getType();
            List<PaymentReminder> reminders = gson.fromJson(reader, type);
            return reminders != null ? reminders : new ArrayList<>();
        } catch (JsonSyntaxException | IOException e) {
            System.err.println("加载还款提醒失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}