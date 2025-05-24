package com.coinue.util;

import com.coinue.model.ExpenseRecord;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * CSV文件处理工具类
 * 用于处理消费记录的CSV文件导入导出
 */
public class CSVHandler {
    private static final String CSV_SEPARATOR = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 统一类别名称
     * @param category 原始类别名称
     * @return 统一后的类别名称
     */
    private static String normalizeCategory(String category) {
        switch (category) {
            case "餐饮":
                return "食品";
            case "购物":
            case "交通":
            case "娱乐":
            case "教育":
            case "医疗":
            case "住房":
            case "其他":
                return category;
            default:
                return "其他";
        }
    }

    /**
     * 读取CSV文件并解析为消费记录列表
     * @param filePath CSV文件路径
     * @return 消费记录列表
     */
    public static List<ExpenseRecord> readExpenseRecords(String filePath) throws IOException {
        List<ExpenseRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 跳过标题行
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_SEPARATOR);
                if (values.length >= 4) {
                    ExpenseRecord record = new ExpenseRecord(
                            Double.parseDouble(values[1].trim()),  // 金额
                            normalizeCategory(values[0].trim()),  // 类别
                            values[3].trim(),                      // 名称
                            LocalDate.parse(values[2].trim(), DATE_FORMATTER)  // 日期
                    );
                    records.add(record);
                }
            }
        }
        return records;
    }

    /**
     * 将消费记录列表写入CSV文件
     * @param records 消费记录列表
     * @param filePath 目标文件路径
     */
    public static void writeExpenseRecords(List<ExpenseRecord> records, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // 写入标题行
            bw.write("日期,消费名称,类别,金额\n");
            
            // 写入数据行
            for (ExpenseRecord record : records) {
                bw.write(String.format("%s,%s,%s,%.2f\n",
                        record.getDate().format(DATE_FORMATTER),
                        record.getName(),
                        record.getCategory(),
                        record.getAmount()));
            }
        }
    }

    /**
     * 读取分析用的CSV文件，返回类别统计数据
     * @param filePath CSV文件路径
     * @return 类别-金额映射
     */
    public static Map<String, Double> readCategoryStatistics(String filePath) throws IOException {
        Map<String, Double> statistics = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 跳过标题行
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_SEPARATOR);
                if (values.length >= 4) {
                    String category = values[2].trim();
                    double amount = Double.parseDouble(values[3].trim());
                    statistics.merge(category, amount, Double::sum);
                }
            }
        }
        return statistics;
    }
}