package com.coinue.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 用户数据导出服务类
 * 负责整合用户的所有数据类型并导出为CSV格式
 * 支持用户基本信息、账单数据、分析数据、支出记录等多种数据类型的统一导出
 */
public class UserDataExportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String EXPORT_DIR = "data/exports";
    
    private UserDataService userDataService;
    private ObjectMapper objectMapper;
    
    /**
     * 单例实例
     */
    private static UserDataExportService instance;
    
    /**
     * 获取单例实例
     * @return UserDataExportService实例
     */
    public static synchronized UserDataExportService getInstance() {
        if (instance == null) {
            instance = new UserDataExportService();
        }
        return instance;
    }
    
    /**
     * 私有构造函数
     */
    private UserDataExportService() {
        this.userDataService = UserDataService.getInstance();
        this.objectMapper = new ObjectMapper();
        ensureExportDirectoryExists();
    }
    
    /**
     * 确保导出目录存在
     */
    private void ensureExportDirectoryExists() {
        Path exportDir = Paths.get(EXPORT_DIR);
        if (!Files.exists(exportDir)) {
            try {
                Files.createDirectories(exportDir);
                System.out.println("创建导出目录: " + EXPORT_DIR);
            } catch (IOException e) {
                System.err.println("无法创建导出目录: " + e.getMessage());
            }
        }
    }
    
    /**
     * 导出用户完整数据
     * @param user 用户对象
     * @return 导出的CSV文件路径，失败返回null
     */
    public String exportUserCompleteData(User user) {
        if (user == null || user.getUsername() == null) {
            System.err.println("用户信息无效，无法导出数据");
            return null;
        }
        
        String username = user.getUsername();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String fileName = username + "_complete_data_" + timestamp + ".csv";
        String filePath = EXPORT_DIR + File.separator + fileName;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // 写入用户基本信息
            writeUserBasicInfo(writer, user);
            
            // 写入账单数据
            writeBillData(writer, username);
            
            // 写入分析数据
            writeAnalysisData(writer, username);
            
            // 写入支出记录
            writeExpenseData(writer, username);
            
            // 写入预算数据
            writeBudgetData(writer, username);
            
            // 写入用户设置
            writeUserSettings(writer, username);
            
            // 写入数据导出摘要
            writeExportSummary(writer, username);
            
            System.out.println("用户 " + username + " 的完整数据已导出到: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出用户数据失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 写入用户基本信息
     * @param writer PrintWriter对象
     * @param user 用户对象
     */
    private void writeUserBasicInfo(PrintWriter writer, User user) {
        writer.println("=== 用户基本信息 ===");
        writer.println("数据类型,字段名,值");
        writer.println("用户信息,用户名," + escapeCSV(user.getUsername()));
        writer.println("用户信息,邮箱," + escapeCSV(user.getEmail()));
        writer.println("用户信息,注册日期," + (user.getBirthday() != null ? user.getBirthday().format(DATE_FORMATTER) : ""));
        writer.println("用户信息,安全问题," + escapeCSV(user.getSecurityQuestion()));
        writer.println("用户信息,数据导出时间," + LocalDate.now().format(DATE_FORMATTER));
        writer.println(); // 空行分隔
    }
    
    /**
     * 写入账单数据
     * @param writer PrintWriter对象
     * @param username 用户名
     */
    private void writeBillData(PrintWriter writer, String username) {
        writer.println("=== 账单支付数据 ===");
        
        UserBillData billData = userDataService.loadData(username, "bill_data.json", UserBillData.class);
        if (billData != null) {
            writer.println("数据类型,信用额度,账单记录数,总还款金额,信用使用率,最后更新时间");
            writer.println("账单摘要," + billData.getCreditLimit() + "," + billData.getBillRecords().size() + 
                          "," + String.format("%.2f", billData.getTotalRepaymentAmount()) + 
                          "," + String.format("%.2f", billData.getCreditUsagePercentage()) + "%" +
                          "," + (billData.getUpdatedDate() != null ? billData.getUpdatedDate().format(DATE_FORMATTER) : ""));
            
            writer.println();
            writer.println("账单记录详情:");
            writer.println("日期,描述,金额,状态");
            
            for (UserBillData.BillRecord record : billData.getBillRecords()) {
                writer.println(record.getDate().format(DATE_FORMATTER) + "," +
                              escapeCSV(record.getDescription()) + "," +
                              String.format("%.2f", record.getAmount()) + "," +
                              escapeCSV(record.getStatus()));
            }
        } else {
            writer.println("账单数据,无数据,无数据,无数据,无数据,无数据");
        }
        writer.println(); // 空行分隔
    }
    
    /**
     * 写入分析数据
     * @param writer PrintWriter对象
     * @param username 用户名
     */
    private void writeAnalysisData(PrintWriter writer, String username) {
        writer.println("=== 财务分析数据 ===");
        
        UserAnalysisData analysisData = userDataService.loadData(username, "analysis_data.json", UserAnalysisData.class);
        if (analysisData != null) {
            writer.println("数据类型,总支出,总收入,储蓄率,最高支出类别,最后分析时间");
            writer.println("分析摘要," + String.format("%.2f", analysisData.getTotalExpenses()) +
                          "," + String.format("%.2f", analysisData.getTotalIncome()) +
                          "," + String.format("%.2f", analysisData.getSavingsRate()) + "%" +
                          "," + escapeCSV(analysisData.getTopExpenseCategory()) +
                          "," + (analysisData.getLastAnalysisDate() != null ? analysisData.getLastAnalysisDate().format(DATE_FORMATTER) : ""));
            
            writer.println();
            writer.println("分类支出明细:");
            writer.println("支出类别,金额,占比");
            
            double totalExpenses = analysisData.getTotalExpenses();
            for (Map.Entry<String, Double> entry : analysisData.getCategoryExpenses().entrySet()) {
                double percentage = totalExpenses > 0 ? (entry.getValue() / totalExpenses) * 100 : 0;
                writer.println(escapeCSV(entry.getKey()) + "," +
                              String.format("%.2f", entry.getValue()) + "," +
                              String.format("%.2f", percentage) + "%");
            }
            
            writer.println();
            writer.println("预算使用情况:");
            writer.println("预算类别,预算限额,实际支出,剩余预算,使用率");
            
            for (Map.Entry<String, UserAnalysisData.BudgetUsage> entry : analysisData.getBudgetUsage().entrySet()) {
                UserAnalysisData.BudgetUsage usage = entry.getValue();
                writer.println(escapeCSV(entry.getKey()) + "," +
                              String.format("%.2f", usage.getBudgetLimit()) + "," +
                              String.format("%.2f", usage.getActualSpent()) + "," +
                              String.format("%.2f", usage.getRemainingBudget()) + "," +
                              String.format("%.2f", usage.getUsagePercentage()) + "%");
            }
        } else {
            writer.println("分析数据,无数据,无数据,无数据,无数据,无数据");
        }
        writer.println(); // 空行分隔
    }
    
    /**
     * 写入支出记录数据
     * @param writer PrintWriter对象
     * @param username 用户名
     */
    private void writeExpenseData(PrintWriter writer, String username) {
        writer.println("=== 支出记录数据 ===");
        
        // 尝试加载支出记录数据（可能存在多种格式）
        List<ExpenseRecord> expenseRecords = loadExpenseRecords(username);
        
        if (!expenseRecords.isEmpty()) {
            writer.println("支出记录详情:");
            writer.println("日期,消费名称,类别,金额,记录类型,币种,备注");
            
            for (ExpenseRecord record : expenseRecords) {
                writer.println(record.getDate().format(DATE_FORMATTER) + "," +
                              escapeCSV(record.getName()) + "," +
                              escapeCSV(record.getCategory()) + "," +
                              String.format("%.2f", record.getAmount()) + "," +
                              escapeCSV(record.getRecordType()) + "," +
                              escapeCSV(record.getCurrency()) + "," +
                              escapeCSV(record.getDescription() != null ? record.getDescription() : ""));
            }
            
            // 统计信息
            writer.println();
            writer.println("支出记录统计:");
            writer.println("统计项,值");
            writer.println("记录总数," + expenseRecords.size());
            
            double totalAmount = expenseRecords.stream().mapToDouble(ExpenseRecord::getAmount).sum();
            writer.println("总金额," + String.format("%.2f", totalAmount));
            
            // 按类别统计
            Map<String, Double> categorySum = new HashMap<>();
            for (ExpenseRecord record : expenseRecords) {
                categorySum.put(record.getCategory(), 
                               categorySum.getOrDefault(record.getCategory(), 0.0) + record.getAmount());
            }
            
            writer.println();
            writer.println("类别统计:");
            writer.println("类别,金额,占比");
            for (Map.Entry<String, Double> entry : categorySum.entrySet()) {
                double percentage = totalAmount > 0 ? (entry.getValue() / totalAmount) * 100 : 0;
                writer.println(escapeCSV(entry.getKey()) + "," +
                              String.format("%.2f", entry.getValue()) + "," +
                              String.format("%.2f", percentage) + "%");
            }
        } else {
            writer.println("支出记录,无数据");
        }
        writer.println(); // 空行分隔
    }
    
    /**
     * 写入预算数据
     * @param writer PrintWriter对象
     * @param username 用户名
     */
    private void writeBudgetData(PrintWriter writer, String username) {
        writer.println("=== 预算数据 ===");
        
        // 尝试加载预算数据
        Object budgetData = userDataService.loadData(username, "budget_data.json", Object.class);
        
        if (budgetData != null) {
            writer.println("预算数据详情:");
            writer.println("数据内容," + escapeCSV(budgetData.toString()));
        } else {
            writer.println("预算数据,无数据");
        }
        writer.println(); // 空行分隔
    }
    
    /**
     * 写入用户设置
     * @param writer PrintWriter对象
     * @param username 用户名
     */
    private void writeUserSettings(PrintWriter writer, String username) {
        writer.println("=== 用户设置 ===");
        
        Map<String, Object> settings = userDataService.loadUserSettings(username);
        
        if (settings != null && !settings.isEmpty()) {
            writer.println("设置项,值");
            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                writer.println(escapeCSV(entry.getKey()) + "," + escapeCSV(entry.getValue().toString()));
            }
        } else {
            writer.println("用户设置,无数据");
        }
        writer.println(); // 空行分隔
    }
    
    /**
     * 写入数据导出摘要
     * @param writer PrintWriter对象
     * @param username 用户名
     */
    private void writeExportSummary(PrintWriter writer, String username) {
        writer.println("=== 数据导出摘要 ===");
        writer.println("摘要项,值");
        writer.println("用户名," + escapeCSV(username));
        writer.println("导出时间," + LocalDate.now().format(DATE_FORMATTER));
        writer.println("导出者,Coinue财务管理系统");
        writer.println("数据版本,1.0");
        
        // 统计用户数据目录下的文件数量
        String userDir = userDataService.getUserDataDirectory(username);
        File userDirFile = new File(userDir);
        int fileCount = 0;
        if (userDirFile.exists() && userDirFile.isDirectory()) {
            File[] files = userDirFile.listFiles();
            fileCount = files != null ? files.length : 0;
        }
        writer.println("数据文件数量," + fileCount);
        writer.println("备注,此文件包含用户所有财务数据的完整导出");
    }
    
    /**
     * 加载用户支出记录
     * @param username 用户名
     * @return 支出记录列表
     */
    private List<ExpenseRecord> loadExpenseRecords(String username) {
        List<ExpenseRecord> records = new ArrayList<>();
        
        // 尝试从用户数据服务加载
        Object expenseData = userDataService.loadData(username, "expense_data.json", Object.class);
        if (expenseData != null) {
            // 这里可以根据实际的数据格式进行解析
            // 暂时返回空列表，等待实际数据格式确认
        }
        
        // 也可以尝试从全局expense.json加载（如果用户数据与全局数据关联）
        // 这部分可以根据实际需求进行扩展
        
        return records;
    }
    
    /**
     * 导出仅账单数据
     * @param user 用户对象
     * @return 导出的CSV文件路径，失败返回null
     */
    public String exportUserBillDataOnly(User user) {
        if (user == null || user.getUsername() == null) {
            System.err.println("用户信息无效，无法导出账单数据");
            return null;
        }
        
        String username = user.getUsername();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String fileName = username + "_bill_data_" + timestamp + ".csv";
        String filePath = EXPORT_DIR + File.separator + fileName;
        
        UserBillData billData = userDataService.loadData(username, "bill_data.json", UserBillData.class);
        if (billData == null) {
            System.err.println("用户 " + username + " 没有账单数据可导出");
            return null;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("日期,描述,金额,状态,信用额度,数据更新时间");
            
            for (UserBillData.BillRecord record : billData.getBillRecords()) {
                writer.println(record.getDate().format(DATE_FORMATTER) + "," +
                              escapeCSV(record.getDescription()) + "," +
                              String.format("%.2f", record.getAmount()) + "," +
                              escapeCSV(record.getStatus()) + "," +
                              String.format("%.2f", billData.getCreditLimit()) + "," +
                              (billData.getUpdatedDate() != null ? billData.getUpdatedDate().format(DATE_FORMATTER) : ""));
            }
            
            System.out.println("用户 " + username + " 的账单数据已导出到: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出账单数据失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 导出仅分析数据
     * @param user 用户对象
     * @return 导出的CSV文件路径，失败返回null
     */
    public String exportUserAnalysisDataOnly(User user) {
        if (user == null || user.getUsername() == null) {
            System.err.println("用户信息无效，无法导出分析数据");
            return null;
        }
        
        String username = user.getUsername();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String fileName = username + "_analysis_data_" + timestamp + ".csv";
        String filePath = EXPORT_DIR + File.separator + fileName;
        
        UserAnalysisData analysisData = userDataService.loadData(username, "analysis_data.json", UserAnalysisData.class);
        if (analysisData == null) {
            System.err.println("用户 " + username + " 没有分析数据可导出");
            return null;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("支出类别,金额,占比,最后分析时间");
            
            double totalExpenses = analysisData.getTotalExpenses();
            for (Map.Entry<String, Double> entry : analysisData.getCategoryExpenses().entrySet()) {
                double percentage = totalExpenses > 0 ? (entry.getValue() / totalExpenses) * 100 : 0;
                writer.println(escapeCSV(entry.getKey()) + "," +
                              String.format("%.2f", entry.getValue()) + "," +
                              String.format("%.2f", percentage) + "%," +
                              (analysisData.getLastAnalysisDate() != null ? analysisData.getLastAnalysisDate().format(DATE_FORMATTER) : ""));
            }
            
            System.out.println("用户 " + username + " 的分析数据已导出到: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("导出分析数据失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 转义CSV字段中的特殊字符
     * @param value 原始值
     * @return 转义后的值
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * 获取导出目录路径
     * @return 导出目录路径
     */
    public String getExportDirectory() {
        return EXPORT_DIR;
    }
    
    /**
     * 清理旧的导出文件（保留最近30天的文件）
     */
    public void cleanupOldExports() {
        File exportDir = new File(EXPORT_DIR);
        if (!exportDir.exists()) {
            return;
        }
        
        File[] files = exportDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null) {
            return;
        }
        
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        
        for (File file : files) {
            if (file.lastModified() < thirtyDaysAgo) {
                if (file.delete()) {
                    System.out.println("删除旧导出文件: " + file.getName());
                }
            }
        }
    }
} 