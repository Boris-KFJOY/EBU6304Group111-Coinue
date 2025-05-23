package com.coinue.service.impl;

import com.coinue.model.ExpenseRecord;
import com.coinue.service.ExpenseService;
import com.coinue.util.DataManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消费记录服务实现类
 * 实现消费记录相关的所有业务逻辑
 */
public class ExpenseServiceImpl implements ExpenseService {
    
    // 消费记录数据缓存
    private List<ExpenseRecord> expenseCache;
    
    /**
     * 构造函数，初始化时加载消费记录数据
     */
    public ExpenseServiceImpl() {
        this.expenseCache = DataManager.loadExpenseRecords();
    }

    @Override
    public List<ExpenseRecord> getAllExpenseRecords() {
        return new ArrayList<>(expenseCache);
    }

    @Override
    public boolean addExpenseRecord(ExpenseRecord record) {
        if (record == null) {
            return false;
        }
        
        // 确保ID唯一
        if (record.getId() == null || record.getId().isEmpty()) {
            record.setId(generateExpenseId());
        } else if (expenseCache.stream().anyMatch(r -> r.getId().equals(record.getId()))) {
            return false; // ID冲突
        }
        
        boolean added = expenseCache.add(record);
        if (added) {
            saveExpenseRecords(expenseCache);
        }
        return added;
    }

    @Override
    public boolean deleteExpenseRecord(String recordId) {
        if (recordId == null || recordId.isEmpty()) {
            return false;
        }
        
        boolean removed = expenseCache.removeIf(record -> record.getId().equals(recordId));
        if (removed) {
            saveExpenseRecords(expenseCache);
        }
        return removed;
    }

    @Override
    public List<ExpenseRecord> getExpenseRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseCache.stream()
            .filter(record -> !record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate))
            .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseRecord> getExpenseRecordsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return new ArrayList<>();
        }
        
        return expenseCache.stream()
            .filter(record -> category.equals(record.getCategory()))
            .collect(Collectors.toList());
    }

    @Override
    public double calculateTotalExpenseAmount() {
        return expenseCache.stream()
            .mapToDouble(ExpenseRecord::getAmount)
            .sum();
    }

    @Override
    public Map<String, Double> calculateExpenseStatisticsByCategory() {
        Map<String, Double> statistics = new HashMap<>();
        
        expenseCache.forEach(record -> {
            String category = record.getCategory();
            double amount = record.getAmount();
            
            statistics.put(category, statistics.getOrDefault(category, 0.0) + amount);
        });
        
        return statistics;
    }

    @Override
    public boolean saveExpenseRecords(List<ExpenseRecord> records) {
        if (records != null) {
            try {
                DataManager.saveExpenseRecords(records);
                this.expenseCache = new ArrayList<>(records);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    /**
     * 生成新的消费记录ID
     * @return 生成的唯一ID
     */
    private String generateExpenseId() {
        return "expense_" + System.currentTimeMillis();
    }
    
    /**
     * 刷新消费记录数据缓存
     */
    public void refreshExpenseCache() {
        this.expenseCache = DataManager.loadExpenseRecords();
    }
} 