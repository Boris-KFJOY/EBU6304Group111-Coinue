package com.coinue.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户分析数据模型类
 * 用于存储用户的财务分析数据，包括支出统计、分类汇总等
 */
public class UserAnalysisData {
    
    // 最后分析日期
    private LocalDate lastAnalysisDate;
    
    // 总支出金额
    private double totalExpenses;
    
    // 总收入金额
    private double totalIncome;
    
    // 分类支出统计
    private Map<String, Double> categoryExpenses;
    
    // 月度支出趋势
    private Map<String, Double> monthlyTrends;
    
    // 支出标签
    private List<String> expenseTags;
    
    // 预算使用情况
    private Map<String, BudgetUsage> budgetUsage;
    
    // 分析数据创建时间
    private LocalDate createdDate;
    
    // 分析数据更新时间
    private LocalDate updatedDate;

    /**
     * 默认构造函数
     */
    public UserAnalysisData() {
        this.categoryExpenses = new HashMap<>();
        this.monthlyTrends = new HashMap<>();
        this.expenseTags = new ArrayList<>();
        this.budgetUsage = new HashMap<>();
        this.createdDate = LocalDate.now();
        this.updatedDate = LocalDate.now();
    }

    /**
     * 预算使用情况内部类
     */
    public static class BudgetUsage {
        private double budgetLimit;      // 预算限额
        private double actualSpent;      // 实际支出
        private double remainingBudget;  // 剩余预算
        private double usagePercentage;  // 使用百分比

        public BudgetUsage() {}

        public BudgetUsage(double budgetLimit, double actualSpent) {
            this.budgetLimit = budgetLimit;
            this.actualSpent = actualSpent;
            this.remainingBudget = budgetLimit - actualSpent;
            this.usagePercentage = budgetLimit > 0 ? (actualSpent / budgetLimit) * 100 : 0;
        }

        // Getters and Setters
        public double getBudgetLimit() { return budgetLimit; }
        public void setBudgetLimit(double budgetLimit) { this.budgetLimit = budgetLimit; }

        public double getActualSpent() { return actualSpent; }
        public void setActualSpent(double actualSpent) { this.actualSpent = actualSpent; }

        public double getRemainingBudget() { return remainingBudget; }
        public void setRemainingBudget(double remainingBudget) { this.remainingBudget = remainingBudget; }

        public double getUsagePercentage() { return usagePercentage; }
        public void setUsagePercentage(double usagePercentage) { this.usagePercentage = usagePercentage; }
    }

    /**
     * 添加分类支出
     * @param category 支出分类
     * @param amount 支出金额
     */
    public void addCategoryExpense(String category, double amount) {
        categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0.0) + amount);
        updateAnalysisDate();
    }

    /**
     * 添加月度趋势数据
     * @param month 月份 (格式: yyyy-MM)
     * @param amount 金额
     */
    public void addMonthlyTrend(String month, double amount) {
        monthlyTrends.put(month, amount);
        updateAnalysisDate();
    }

    /**
     * 添加支出标签
     * @param tag 标签名称
     */
    public void addExpenseTag(String tag) {
        if (!expenseTags.contains(tag)) {
            expenseTags.add(tag);
            updateAnalysisDate();
        }
    }

    /**
     * 更新预算使用情况
     * @param category 预算分类
     * @param budgetLimit 预算限额
     * @param actualSpent 实际支出
     */
    public void updateBudgetUsage(String category, double budgetLimit, double actualSpent) {
        budgetUsage.put(category, new BudgetUsage(budgetLimit, actualSpent));
        updateAnalysisDate();
    }

    /**
     * 计算储蓄率
     * @return 储蓄率百分比
     */
    public double getSavingsRate() {
        if (totalIncome <= 0) {
            return 0.0;
        }
        double savings = totalIncome - totalExpenses;
        return (savings / totalIncome) * 100;
    }

    /**
     * 获取最大支出分类
     * @return 最大支出分类名称
     */
    public String getTopExpenseCategory() {
        return categoryExpenses.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("无数据");
    }

    /**
     * 更新分析日期
     */
    private void updateAnalysisDate() {
        this.lastAnalysisDate = LocalDate.now();
        this.updatedDate = LocalDate.now();
    }

    // ============================== Getters and Setters ==============================

    public LocalDate getLastAnalysisDate() {
        return lastAnalysisDate;
    }

    public void setLastAnalysisDate(LocalDate lastAnalysisDate) {
        this.lastAnalysisDate = lastAnalysisDate;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
        updateAnalysisDate();
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
        updateAnalysisDate();
    }

    public Map<String, Double> getCategoryExpenses() {
        return categoryExpenses;
    }

    public void setCategoryExpenses(Map<String, Double> categoryExpenses) {
        this.categoryExpenses = categoryExpenses;
        updateAnalysisDate();
    }

    public Map<String, Double> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(Map<String, Double> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
        updateAnalysisDate();
    }

    public List<String> getExpenseTags() {
        return expenseTags;
    }

    public void setExpenseTags(List<String> expenseTags) {
        this.expenseTags = expenseTags;
        updateAnalysisDate();
    }

    public Map<String, BudgetUsage> getBudgetUsage() {
        return budgetUsage;
    }

    public void setBudgetUsage(Map<String, BudgetUsage> budgetUsage) {
        this.budgetUsage = budgetUsage;
        updateAnalysisDate();
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

    @Override
    public String toString() {
        return "UserAnalysisData{" +
                "lastAnalysisDate=" + lastAnalysisDate +
                ", totalExpenses=" + totalExpenses +
                ", totalIncome=" + totalIncome +
                ", categoryCount=" + categoryExpenses.size() +
                ", savingsRate=" + String.format("%.2f", getSavingsRate()) + "%" +
                '}';
    }
} 