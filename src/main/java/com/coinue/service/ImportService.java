package com.coinue.service;

import com.coinue.model.Budget;
import com.coinue.model.ExpenseRecord;
import com.coinue.model.PaymentReminder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据导入服务接口
 * 负责处理所有与数据导入相关的业务逻辑，包括CSV导入、文件解析等
 */
public interface ImportService {
    
    /**
     * 从CSV文件导入消费记录
     * @param filePath CSV文件路径
     * @return 导入的消费记录列表
     * @throws IOException 如果导入过程中发生IO错误
     */
    List<ExpenseRecord> importExpenseRecordsFromCSV(String filePath) throws IOException;
    
    /**
     * 从CSV文件导入消费记录
     * @param file CSV文件对象
     * @return 导入的消费记录列表
     * @throws IOException 如果导入过程中发生IO错误
     */
    List<ExpenseRecord> importExpenseRecordsFromCSV(File file) throws IOException;
    
    /**
     * 从CSV文件导入预算数据
     * @param filePath CSV文件路径
     * @return 导入的预算列表
     * @throws IOException 如果导入过程中发生IO错误
     */
    List<Budget> importBudgetsFromCSV(String filePath) throws IOException;
    
    /**
     * 从CSV文件导入预算数据
     * @param file CSV文件对象
     * @return 导入的预算列表
     * @throws IOException 如果导入过程中发生IO错误
     */
    List<Budget> importBudgetsFromCSV(File file) throws IOException;
    
    /**
     * 从CSV文件导入还款提醒
     * @param filePath CSV文件路径
     * @return 导入的还款提醒列表
     * @throws IOException 如果导入过程中发生IO错误
     */
    List<PaymentReminder> importRemindersFromCSV(String filePath) throws IOException;
    
    /**
     * 从CSV文件导入还款提醒
     * @param file CSV文件对象
     * @return 导入的还款提醒列表
     * @throws IOException 如果导入过程中发生IO错误
     */
    List<PaymentReminder> importRemindersFromCSV(File file) throws IOException;
    
    /**
     * 解析CSV数据中的类型信息
     * @param data CSV行数据
     * @return 解析后的类型标识
     */
    String parseTypeFromCSV(String[] data);
    
    /**
     * 获取文件选择器中选择的导入文件
     * @param title 文件选择器标题
     * @param extensionDescription 扩展名描述
     * @param extensions 允许的文件扩展名
     * @return 选中的文件对象，如果用户取消则返回null
     */
    File showImportFileChooser(String title, String extensionDescription, String[] extensions);
    
    /**
     * 验证CSV文件格式是否有效
     * @param file CSV文件对象
     * @param requiredHeaders 必需的表头字段数组
     * @return 文件格式是否有效
     */
    boolean validateCSVFormat(File file, String[] requiredHeaders);
} 