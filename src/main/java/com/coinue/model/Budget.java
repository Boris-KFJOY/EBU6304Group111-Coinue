package com.coinue.model;

public class Budget {
    private String category;
    private double amount;
    private String currency;
    private double spentAmount;

    public Budget(String category, double amount, String currency) {
        this.category = category;
        this.amount = amount;
        this.currency = currency;
        this.spentAmount = 0.0;
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    // 计算使用百分比
    public double getUsagePercentage() {
        return (spentAmount / amount) * 100;
    }

    // 添加支出
    public void addExpense(double expense) {
        this.spentAmount += expense;
    }
}