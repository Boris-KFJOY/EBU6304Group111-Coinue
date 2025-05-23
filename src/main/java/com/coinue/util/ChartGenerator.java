package com.coinue.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图表生成工具类
 * 用于生成各类统计图表，如消费类型占比饼图
 */
public class ChartGenerator {

    /**
     * 生成消费类型占比饼图
     * @param categoryAmounts 类别-金额映射
     * @return 饼图数据
     */
    public static ObservableList<PieChart.Data> generateExpensePieChartData(Map<String, Double> categoryAmounts) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        double total = categoryAmounts.values().stream().mapToDouble(Double::doubleValue).sum();

        categoryAmounts.forEach((category, amount) -> {
            // 计算百分比
            double percentage = (amount / total) * 100;
            // 创建饼图数据项，显示类别名称和百分比
            String label = String.format("%s (%.1f%%)", category, percentage);
            pieChartData.add(new PieChart.Data(label, amount));
        });

        return pieChartData;
    }

    /**
     * 生成消费统计摘要
     * @param categoryAmounts 类别-金额映射
     * @return 统计摘要文本
     */
    public static String generateStatisticsSummary(Map<String, Double> categoryAmounts) {
        StringBuilder summary = new StringBuilder();
        double total = categoryAmounts.values().stream().mapToDouble(Double::doubleValue).sum();

        summary.append(String.format("总支出：%.2f元\n\n", total));
        summary.append("各类别支出占比：\n");

        categoryAmounts.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    double percentage = (entry.getValue() / total) * 100;
                    summary.append(String.format("%s: %.2f元 (%.1f%%)\n",
                            entry.getKey(), entry.getValue(), percentage));
                });

        return summary.toString();
    }
    
    /**
     * 从饼图中提取数据
     * @param pieChart 饼图对象
     * @return 类别-金额映射
     */
    public static Map<String, Double> extractDataFromPieChart(PieChart pieChart) {
        Map<String, Double> data = new HashMap<>();
        
        if (pieChart == null || pieChart.getData() == null || pieChart.getData().isEmpty()) {
            return data;
        }
        
        // 提取类别名称的正则表达式模式
        Pattern pattern = Pattern.compile("^(.*?)\\s*\\(.*?\\)$");
        
        for (PieChart.Data pieData : pieChart.getData()) {
            String label = pieData.getName();
            double value = pieData.getPieValue();
            
            // 从标签中提取类别名称（去掉百分比部分）
            Matcher matcher = pattern.matcher(label);
            String category = matcher.matches() ? matcher.group(1).trim() : label;
            
            data.put(category, value);
        }
        
        return data;
    }
}