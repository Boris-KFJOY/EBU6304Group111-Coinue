package com.coinue.model;

import java.time.LocalDate;

/**
 * 消费记录实体类
 * 用于存储单条消费记录的详细信息
 */
public class ExpenseRecord {
    private double amount;        // 金额
    private String category;      // 类别
    private String name;          // 名称
    private LocalDate date;       // 日期
    private String description;   // 备注
    private String recordType;    // 记录类型（支出或收入）
    private String currency;      // 币种

    public ExpenseRecord() {
        this.recordType = "支出";  // 默认为支出
        this.currency = "CNY";    // 默认为人民币
    }

    public ExpenseRecord(double amount, String category, String name, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.name = name;
        this.date = date;
        this.recordType = "支出";  // 默认为支出
        this.currency = "CNY";    // 默认为人民币
    }

    // 添加带备注的构造函数
    public ExpenseRecord(double amount, String category, String name, LocalDate date, String description) {
        this.amount = amount;
        this.category = category;
        this.name = name;
        this.date = date;
        this.description = description;
        this.recordType = "支出";  // 默认为支出
        this.currency = "CNY";    // 默认为人民币
    }

    // 添加完整的构造函数
    public ExpenseRecord(double amount, String category, String name, LocalDate date, 
                         String description, String recordType, String currency) {
        this.amount = amount;
        this.category = category;
        this.name = name;
        this.date = date;
        this.description = description;
        this.recordType = recordType;
        this.currency = currency;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return String.format("ExpenseRecord{amount=%.2f, currency='%s', category='%s', name='%s', date=%s, description='%s', recordType='%s'}",
                amount, currency, category, name, date, description, recordType);
    }
}