package com.coinue.service.impl;

import com.coinue.model.Budget;
import com.coinue.service.BudgetService;
import com.coinue.util.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 预算服务实现类
 * 实现预算相关的所有业务逻辑
 */
public class BudgetServiceImpl implements BudgetService {
    
    // 预算数据缓存
    private List<Budget> budgetCache;
    
    /**
     * 构造函数，初始化时加载预算数据
     */
    public BudgetServiceImpl() {
        this.budgetCache = DataManager.loadBudgets();
    }

    @Override
    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgetCache);
    }

    @Override
    public boolean addBudget(Budget budget) {
        if (budget == null) {
            return false;
        }
        
        // 确保ID唯一
        if (budget.getId() == null || budget.getId().isEmpty()) {
            budget.setId(generateBudgetId());
        } else if (budgetCache.stream().anyMatch(b -> b.getId().equals(budget.getId()))) {
            return false; // ID冲突
        }
        
        boolean added = budgetCache.add(budget);
        if (added) {
            saveBudgets(budgetCache);
        }
        return added;
    }

    @Override
    public boolean updateBudget(Budget budget) {
        if (budget == null || budget.getId() == null || budget.getId().isEmpty()) {
            return false;
        }
        
        Optional<Budget> existingBudget = budgetCache.stream()
            .filter(b -> b.getId().equals(budget.getId()))
            .findFirst();
        
        if (existingBudget.isPresent()) {
            int index = budgetCache.indexOf(existingBudget.get());
            budgetCache.set(index, budget);
            saveBudgets(budgetCache);
            return true;
        }
        
        return false;
    }

    @Override
    public boolean deleteBudget(String budgetId) {
        if (budgetId == null || budgetId.isEmpty()) {
            return false;
        }
        
        boolean removed = budgetCache.removeIf(budget -> budget.getId().equals(budgetId));
        if (removed) {
            saveBudgets(budgetCache);
        }
        return removed;
    }

    @Override
    public double calculateBudgetUsagePercentage(Budget budget) {
        if (budget == null || budget.getAmount() <= 0) {
            return 0.0;
        }
        
        double spent = budget.getSpent();
        double amount = budget.getAmount();
        
        double percentage = (spent / amount) * 100.0;
        // 防止百分比超过100%
        return Math.min(percentage, 100.0);
    }

    @Override
    public boolean saveBudgets(List<Budget> budgets) {
        if (budgets != null) {
            try {
                DataManager.saveBudgets(budgets);
                this.budgetCache = new ArrayList<>(budgets);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public double calculateTotalBudgetAmount() {
        return budgetCache.stream()
            .mapToDouble(Budget::getAmount)
            .sum();
    }
    
    /**
     * 生成新的预算ID
     * @return 生成的唯一ID
     */
    private String generateBudgetId() {
        return "budget_" + System.currentTimeMillis();
    }
    
    /**
     * 刷新预算数据缓存
     */
    public void refreshBudgetCache() {
        this.budgetCache = DataManager.loadBudgets();
    }
} 