package com.coinue.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * 数据分析服务接口
 * 负责处理所有与数据分析相关的业务逻辑，包括CSV分析、统计数据生成、图表数据准备等
 */
public interface AnalysisService {
    
    /**
     * 从CSV文件中读取并分析类别统计数据
     * @param filePath CSV文件路径
     * @return 各类别金额统计的映射表
     * @throws IOException 如果读取文件失败
     */
    Map<String, Double> readCategoryStatistics(String filePath) throws IOException;
    
    /**
     * 从CSV文件对象中读取并分析类别统计数据
     * @param file CSV文件对象
     * @return 各类别金额统计的映射表
     * @throws IOException 如果读取文件失败
     */
    Map<String, Double> readCategoryStatistics(File file) throws IOException;
    
    /**
     * 生成统计摘要文本
     * @param statistics 统计数据
     * @return 格式化的统计摘要文本
     */
    String generateStatisticsSummary(Map<String, Double> statistics);
    
    /**
     * 计算预算使用进度
     * @param totalExpense 总支出
     * @param budgetAmount 预算金额
     * @return 预算使用百分比
     */
    double calculateBudgetProgress(double totalExpense, double budgetAmount);
    
    /**
     * 根据时间范围分析支出趋势
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期和金额的映射表
     */
    Map<LocalDate, Double> analyzeExpenseTrend(LocalDate startDate, LocalDate endDate);
    
    /**
     * 分析花费最多的类别
     * @return 类别和金额的映射表（按金额降序排列）
     */
    Map<String, Double> analyzeTopSpendingCategories();
    
    /**
     * 分析月度预算执行情况
     * @param month 月份（1-12）
     * @param year 年份
     * @return 预算类别和使用百分比的映射表
     */
    Map<String, Double> analyzeMonthlyBudgetExecution(int month, int year);
    
    /**
     * 生成用于图表显示的类别统计数据
     * @param statistics 原始统计数据
     * @return 处理后的适合图表显示的数据
     */
    Map<String, Double> prepareChartData(Map<String, Double> statistics);
} 