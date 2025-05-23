package com.coinue.service;

import com.coinue.model.ExpenseRecord;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据导出服务接口
 * 负责处理所有与数据导出相关的业务逻辑，包括PDF导出、CSV导出等
 */
public interface ExportService {
    
    /**
     * 导出消费记录到CSV文件
     * @param records 消费记录列表
     * @param filePath 目标文件路径
     * @return 导出是否成功
     * @throws IOException 如果导出过程中发生IO错误
     */
    boolean exportExpenseRecordsToCSV(List<ExpenseRecord> records, String filePath) throws IOException;
    
    /**
     * 将统计数据导出到PDF文件
     * @param statistics 统计数据
     * @param filePath 目标文件路径
     * @param title PDF标题
     * @return 导出是否成功
     * @throws IOException 如果导出过程中发生IO错误
     */
    boolean exportStatisticsToPDF(Map<String, Double> statistics, String filePath, String title) throws IOException;
    
    /**
     * 将JavaFX节点导出到PDF文件（适用于图表等可视化内容）
     * @param node JavaFX节点
     * @param filePath 目标文件路径
     * @param title PDF标题
     * @return 导出是否成功
     * @throws IOException 如果导出过程中发生IO错误
     */
    boolean exportNodeToPDF(Node node, String filePath, String title) throws IOException;
    
    /**
     * 将JavaFX节点导出为图片
     * @param node JavaFX节点
     * @param filePath 目标文件路径
     * @return 导出是否成功
     * @throws IOException 如果导出过程中发生IO错误
     */
    boolean exportNodeToImage(Node node, String filePath) throws IOException;
    
    /**
     * 生成分析报告PDF
     * @param chartNodes 图表节点列表
     * @param statisticsMap 统计数据
     * @param filePath 目标文件路径
     * @param title 报告标题
     * @return 导出是否成功
     * @throws IOException 如果导出过程中发生IO错误
     */
    boolean generateAnalysisReport(List<Node> chartNodes, Map<String, Double> statisticsMap, 
                                 String filePath, String title) throws IOException;
    
    /**
     * 获取文件选择器中选择的文件
     * @param title 文件选择器标题
     * @param extensionDescription 扩展名描述
     * @param extensions 允许的文件扩展名
     * @param initialDirectory 初始目录
     * @param saveDialog 是否为保存对话框（false表示打开对话框）
     * @return 选中的文件对象，如果用户取消则返回null
     */
    File showFileChooser(String title, String extensionDescription, 
                        String[] extensions, String initialDirectory, boolean saveDialog);
} 