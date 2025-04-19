package com.coinue.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PaymentReminder {
    private String platform;
    private double amount;
    private LocalDate dueDate;
    private String iconPath; // 添加图标路径属性

    public PaymentReminder(String platform, double amount, LocalDate dueDate, String iconPath) {
        this.platform = platform;
        this.amount = amount;
        this.dueDate = dueDate;
        this.iconPath = iconPath;
    }
    
    // 兼容旧版本的构造函数
    public PaymentReminder(String platform, double amount, LocalDate dueDate) {
        this(platform, amount, dueDate, "/images/icons/credit_card.png"); // 默认图标
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
    
    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
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