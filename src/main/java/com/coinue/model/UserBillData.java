package com.coinue.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户账单数据模型类
 * 用于存储用户的账单支付数据，包括账单记录、信用额度设置等
 */
public class UserBillData {
    
    // 信用额度
    private double creditLimit;
    
    // 账单记录列表
    private List<BillRecord> billRecords;
    
    // 数据创建时间
    private LocalDate createdDate;
    
    // 数据更新时间
    private LocalDate updatedDate;
    
    // 最后导入的文件名
    private String lastImportedFile;

    /**
     * 默认构造函数
     */
    public UserBillData() {
        this.creditLimit = 7500.00; // 默认信用额度
        this.billRecords = new ArrayList<>();
        this.createdDate = LocalDate.now();
        this.updatedDate = LocalDate.now();
    }

    /**
     * 账单记录内部类
     */
    public static class BillRecord {
        private LocalDate date;
        private String description;
        private double amount;
        private String status;

        public BillRecord() {}

        public BillRecord(LocalDate date, String description, double amount, String status) {
            this.date = date;
            this.description = description;
            this.amount = amount;
            this.status = status;
        }

        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * 添加账单记录
     * @param record 账单记录
     */
    public void addBillRecord(BillRecord record) {
        billRecords.add(record);
        updateDataTime();
    }

    /**
     * 清除所有账单记录
     */
    public void clearBillRecords() {
        billRecords.clear();
        updateDataTime();
    }

    /**
     * 设置账单记录列表
     * @param records 账单记录列表
     */
    public void setBillRecords(List<BillRecord> records) {
        this.billRecords = new ArrayList<>(records);
        updateDataTime();
    }

    /**
     * 计算总还款金额
     * @return 总还款金额
     */
    @JsonIgnore
    public double getTotalRepaymentAmount() {
        return billRecords.stream().mapToDouble(BillRecord::getAmount).sum();
    }

    /**
     * 计算信用额度使用率
     * @return 使用率百分比
     */
    @JsonIgnore
    public double getCreditUsagePercentage() {
        if (creditLimit <= 0) {
            return 0.0;
        }
        return (getTotalRepaymentAmount() / creditLimit) * 100;
    }

    /**
     * 获取剩余信用额度
     * @return 剩余信用额度
     */
    @JsonIgnore
    public double getRemainingCredit() {
        return Math.max(0, creditLimit - getTotalRepaymentAmount());
    }

    /**
     * 更新数据时间
     */
    private void updateDataTime() {
        this.updatedDate = LocalDate.now();
    }

    // ============================== Getters and Setters ==============================

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
        updateDataTime();
    }

    public List<BillRecord> getBillRecords() {
        return billRecords;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getLastImportedFile() {
        return lastImportedFile;
    }

    public void setLastImportedFile(String lastImportedFile) {
        this.lastImportedFile = lastImportedFile;
        updateDataTime();
    }

    @Override
    public String toString() {
        return "UserBillData{" +
                "creditLimit=" + creditLimit +
                ", billRecordsCount=" + billRecords.size() +
                ", totalRepayment=" + String.format("%.2f", getTotalRepaymentAmount()) +
                ", usagePercentage=" + String.format("%.2f", getCreditUsagePercentage()) + "%" +
                ", lastUpdated=" + updatedDate +
                '}';
    }
} 