package com.coinue.model;

import java.time.LocalDate;

/**
 * 消费记录实体类
 * 用于存储单条消费记录的详细信息
 */
public class ExpenseRecord {
    private double amount;        // 消费金额
    private String category;      // 消费类别
    private String name;          // 消费名称
    private LocalDate date;       // 消费日期

    public ExpenseRecord() {}

    public ExpenseRecord(double amount, String category, String name, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.name = name;
        this.date = date;
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

    @Override
    public String toString() {
        return String.format("ExpenseRecord{amount=%.2f, category='%s', name='%s', date=%s}",
                amount, category, name, date);
    }
}