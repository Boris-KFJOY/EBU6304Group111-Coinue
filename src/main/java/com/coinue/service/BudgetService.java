package com.coinue.service;

import com.coinue.model.Budget;
import java.util.List;

/**
 * 预算服务接口
 * 负责处理所有与预算相关的业务逻辑，包括添加、更新、删除、加载预算等操作
 */
public interface BudgetService {
    
    /**
     * 获取所有预算
     * @return 预算列表
     */
    List<Budget> getAllBudgets();
    
    /**
     * 添加新预算
     * @param budget 预算对象
     * @return 添加是否成功
     */
    boolean addBudget(Budget budget);
    
    /**
     * 更新预算
     * @param budget 更新后的预算对象
     * @return 更新是否成功
     */
    boolean updateBudget(Budget budget);
    
    /**
     * 删除预算
     * @param budgetId 预算ID
     * @return 删除是否成功
     */
    boolean deleteBudget(String budgetId);
    
    /**
     * 计算预算使用百分比
     * @param budget 预算对象
     * @return 使用百分比
     */
    double calculateBudgetUsagePercentage(Budget budget);
    
    /**
     * 保存所有预算
     * @param budgets 预算列表
     * @return 保存是否成功
     */
    boolean saveBudgets(List<Budget> budgets);
    
    /**
     * 计算所有预算的总金额
     * @return 总预算金额
     */
    double calculateTotalBudgetAmount();
} 