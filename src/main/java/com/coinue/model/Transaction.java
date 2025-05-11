package com.coinue.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 交易记录模型类
 */
public class Transaction {
    private String id;
    private String userId;
    private BigDecimal amount;
    private TransactionType type; // 收入或支出
    private String category;
    private LocalDateTime transactionTime;
    private String description;
    private String location;
    
    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.transactionTime = LocalDateTime.now();
    }
    
    // TODO: 添加更多交易相关字段（如交易方式、标签等）
    // TODO: 实现金额验证逻辑
    // TODO: 添加统计和分析方法
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        // TODO: 验证金额有效性
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

/**
 * 交易类型枚举
 */
enum TransactionType {
    INCOME,    // 收入
    EXPENSE    // 支出
} 