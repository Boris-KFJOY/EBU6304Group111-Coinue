package com.coinue.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PaymentReminder {
    private String platform;
    private double amount;
    private LocalDate dueDate;

    public PaymentReminder(String platform, double amount, LocalDate dueDate) {
        this.platform = platform;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    // Getters
    public String getPlatform() {
        return platform;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // 计算距离到期还有多少天
    public long getDaysUntilDue(LocalDate currentDate) {
        return ChronoUnit.DAYS.between(currentDate, dueDate);
    }

    // 检查是否需要提醒（比如距离到期日小于7天）
    public boolean needsReminder(LocalDate currentDate) {
        long daysUntilDue = getDaysUntilDue(currentDate);
        return daysUntilDue >= 0 && daysUntilDue <= 7;
    }
}