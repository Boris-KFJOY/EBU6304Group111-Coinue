package com.coinue.service;

import com.coinue.model.ExpenseRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 消费记录服务接口
 * 负责处理所有与消费记录相关的业务逻辑，包括添加、删除、查询消费记录等操作
 */
public interface ExpenseService {
    
    /**
     * 获取所有消费记录
     * @return 消费记录列表
     */
    List<ExpenseRecord> getAllExpenseRecords();
    
    /**
     * 添加消费记录
     * @param record 消费记录对象
     * @return 添加是否成功
     */
    boolean addExpenseRecord(ExpenseRecord record);
    
    /**
     * 删除消费记录
     * @param recordId 消费记录ID
     * @return 删除是否成功
     */
    boolean deleteExpenseRecord(String recordId);
    
    /**
     * 根据日期范围获取消费记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 指定日期范围内的消费记录列表
     */
    List<ExpenseRecord> getExpenseRecordsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据类别获取消费记录
     * @param category 消费类别
     * @return 指定类别的消费记录列表
     */
    List<ExpenseRecord> getExpenseRecordsByCategory(String category);
    
    /**
     * 计算总消费金额
     * @return 总消费金额
     */
    double calculateTotalExpenseAmount();
    
    /**
     * 按类别统计消费金额
     * @return 各类别消费金额的映射表
     */
    Map<String, Double> calculateExpenseStatisticsByCategory();
    
    /**
     * 保存所有消费记录
     * @param records 消费记录列表
     * @return 保存是否成功
     */
    boolean saveExpenseRecords(List<ExpenseRecord> records);
} 