package com.coinue.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录数据传输对象
 */
public class TransactionDTO {
    private String type; // INCOME 或 EXPENSE
    private BigDecimal amount;
    private String category;
    private LocalDateTime transactionTime;
    private String description;
    private String location;
    
    // TODO: 添加表单验证方法
    // TODO: 实现金额有效性验证
    // TODO: 添加数据格式转换方法
    
    public boolean isValid() {
        // TODO: 实现完整的验证逻辑
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0
                && category != null && !category.trim().isEmpty()
                && type != null && (type.equals("INCOME") || type.equals("EXPENSE"));
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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