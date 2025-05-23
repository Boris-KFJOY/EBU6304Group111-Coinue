package com.coinue.service.impl;

import com.coinue.model.ExpenseRecord;
import com.coinue.service.AnalysisService;
import com.coinue.service.ExpenseService;
import com.coinue.util.CSVHandler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据分析服务实现类
 * 实现数据分析相关的所有业务逻辑
 */
public class AnalysisServiceImpl implements AnalysisService {
    
    // 消费记录服务依赖
    private ExpenseService expenseService;
    
    /**
     * 构造函数，初始化消费记录服务
     */
    public AnalysisServiceImpl() {
        this.expenseService = new ExpenseServiceImpl();
    }
    
    /**
     * 构造函数，注入消费记录服务
     * @param expenseService 消费记录服务
     */
    public AnalysisServiceImpl(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Override
    public Map<String, Double> readCategoryStatistics(String filePath) throws IOException {
        return CSVHandler.readCategoryStatistics(filePath);
    }

    @Override
    public Map<String, Double> readCategoryStatistics(File file) throws IOException {
        return readCategoryStatistics(file.getPath());
    }

    @Override
    public String generateStatisticsSummary(Map<String, Double> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return "无统计数据";
        }
        
        double total = statistics.values().stream().mapToDouble(Double::doubleValue).sum();
        
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("总消费: ¥%.2f\n\n", total));
        summary.append("各类别消费统计:\n");
        
        // 按照金额降序排序
        statistics.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                double percentage = (entry.getValue() / total) * 100;
                summary.append(String.format("%s: ¥%.2f (%.1f%%)\n", 
                        entry.getKey(), entry.getValue(), percentage));
            });
        
        return summary.toString();
    }

    @Override
    public double calculateBudgetProgress(double totalExpense, double budgetAmount) {
        if (budgetAmount <= 0) {
            return 0.0;
        }
        
        return totalExpense / budgetAmount;
    }

    @Override
    public Map<LocalDate, Double> analyzeExpenseTrend(LocalDate startDate, LocalDate endDate) {
        List<ExpenseRecord> records = expenseService.getExpenseRecordsByDateRange(startDate, endDate);
        Map<LocalDate, Double> trend = new LinkedHashMap<>();
        
        // 初始化日期范围内的所有日期
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            trend.put(date, 0.0);
        }
        
        // 统计每天的消费总额
        for (ExpenseRecord record : records) {
            LocalDate date = record.getDate();
            double amount = record.getAmount();
            
            trend.put(date, trend.getOrDefault(date, 0.0) + amount);
        }
        
        return trend;
    }

    @Override
    public Map<String, Double> analyzeTopSpendingCategories() {
        Map<String, Double> categoryStatistics = expenseService.calculateExpenseStatisticsByCategory();
        
        // 转换为LinkedHashMap并按金额降序排序
        return categoryStatistics.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    @Override
    public Map<String, Double> analyzeMonthlyBudgetExecution(int month, int year) {
        // 构造月份的开始和结束日期
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // 获取指定月份内的所有消费记录
        List<ExpenseRecord> records = expenseService.getExpenseRecordsByDateRange(startDate, endDate);
        
        // 按类别统计消费金额
        Map<String, Double> categorySpending = new HashMap<>();
        for (ExpenseRecord record : records) {
            String category = record.getCategory();
            double amount = record.getAmount();
            
            categorySpending.put(category, categorySpending.getOrDefault(category, 0.0) + amount);
        }
        
        return categorySpending;
    }

    @Override
    public Map<String, Double> prepareChartData(Map<String, Double> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return new HashMap<>();
        }
        
        // 只保留前8个类别，剩余的归为"其他"
        if (statistics.size() <= 8) {
            return statistics;
        }
        
        // 转换为List并按金额排序
        List<Map.Entry<String, Double>> entries = new ArrayList<>(statistics.entrySet());
        entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        
        // 提取前7个类别
        Map<String, Double> chartData = new LinkedHashMap<>();
        for (int i = 0; i < 7 && i < entries.size(); i++) {
            Map.Entry<String, Double> entry = entries.get(i);
            chartData.put(entry.getKey(), entry.getValue());
        }
        
        // 将剩余类别归为"其他"
        double othersAmount = 0.0;
        for (int i = 7; i < entries.size(); i++) {
            othersAmount += entries.get(i).getValue();
        }
        
        if (othersAmount > 0) {
            chartData.put("其他", othersAmount);
        }
        
        return chartData;
    }
} 