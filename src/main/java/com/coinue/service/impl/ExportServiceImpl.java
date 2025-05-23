package com.coinue.service.impl;

import com.coinue.model.ExpenseRecord;
import com.coinue.service.ExportService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 数据导出服务实现类
 * 实现数据导出相关的所有业务逻辑
 */
public class ExportServiceImpl implements ExportService {

    @Override
    public boolean exportExpenseRecordsToCSV(List<ExpenseRecord> records, String filePath) throws IOException {
        if (records == null || filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // 写入CSV头
            writer.println("Date,Category,Name,Amount,Currency,Description");
            
            // 写入记录
            for (ExpenseRecord record : records) {
                writer.println(String.format("%s,%s,%s,%.2f,%s,%s",
                        record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        escapeCSV(record.getCategory()),
                        escapeCSV(record.getName()),
                        record.getAmount(),
                        escapeCSV(record.getCurrency()),
                        escapeCSV(record.getDescription())
                ));
            }
            
            return true;
        } catch (IOException e) {
            throw new IOException("导出CSV失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exportStatisticsToPDF(Map<String, Double> statistics, String filePath, String title) throws IOException {
        if (statistics == null || filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDFont font = PDType1Font.HELVETICA_BOLD;
                PDFont regular = PDType1Font.HELVETICA;
                
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float width = page.getMediaBox().getWidth() - 2 * margin;
                
                // 添加标题
                contentStream.beginText();
                contentStream.setFont(font, 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                yPosition -= 30;
                
                // 添加统计数据
                double total = statistics.values().stream().mapToDouble(Double::doubleValue).sum();
                
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("总计: ¥" + String.format("%.2f", total));
                contentStream.endText();
                yPosition -= 20;
                
                for (Map.Entry<String, Double> entry : statistics.entrySet()) {
                    double percentage = (entry.getValue() / total) * 100;
                    
                    contentStream.beginText();
                    contentStream.setFont(regular, 10);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(String.format("%s: ¥%.2f (%.1f%%)", 
                            entry.getKey(), entry.getValue(), percentage));
                    contentStream.endText();
                    
                    yPosition -= 15;
                    
                    // 检查是否需要换页
                    if (yPosition < margin) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream.close();
                        try (PDPageContentStream newStream = new PDPageContentStream(document, page)) {
                            yPosition = page.getMediaBox().getHeight() - margin;
                        }
                    }
                }
            }
            
            document.save(filePath);
            return true;
        } catch (IOException e) {
            throw new IOException("导出PDF失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exportNodeToPDF(Node node, String filePath, String title) throws IOException {
        if (node == null || filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // 获取节点截图
            WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            
            PDImageXObject image = PDImageXObject.createFromByteArray(document, baos.toByteArray(), "image");
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // 绘制标题
                PDFont font = PDType1Font.HELVETICA_BOLD;
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                
                contentStream.beginText();
                contentStream.setFont(font, 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                yPosition -= 30;
                
                // 计算图片尺寸
                float imageWidth = page.getMediaBox().getWidth() - 2 * margin;
                float imageHeight = (imageWidth / image.getWidth()) * image.getHeight();
                
                // 绘制图片
                contentStream.drawImage(image, margin, yPosition - imageHeight, imageWidth, imageHeight);
            }
            
            document.save(filePath);
            return true;
        } catch (IOException e) {
            throw new IOException("导出PDF失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exportNodeToImage(Node node, String filePath) throws IOException {
        if (node == null || filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try {
            // 获取节点截图
            WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
            
            // 确定图片格式
            String format = "png";
            if (filePath.toLowerCase().endsWith(".jpg") || filePath.toLowerCase().endsWith(".jpeg")) {
                format = "jpg";
            } else if (filePath.toLowerCase().endsWith(".png")) {
                format = "png";
            } else {
                filePath += ".png"; // 默认添加png后缀
            }
            
            // 保存图片
            File file = new File(filePath);
            ImageIO.write(bufferedImage, format, file);
            return true;
        } catch (IOException e) {
            throw new IOException("导出图片失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean generateAnalysisReport(List<Node> chartNodes, Map<String, Double> statisticsMap, 
                                     String filePath, String title) throws IOException {
        if (chartNodes == null || chartNodes.isEmpty() || filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDFont font = PDType1Font.HELVETICA_BOLD;
                PDFont regular = PDType1Font.HELVETICA;
                
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float width = page.getMediaBox().getWidth() - 2 * margin;
                
                // 添加标题
                contentStream.beginText();
                contentStream.setFont(font, 16);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(title);
                contentStream.endText();
                yPosition -= 30;
                
                // 添加日期
                contentStream.beginText();
                contentStream.setFont(regular, 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("生成日期: " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                contentStream.endText();
                yPosition -= 30;
            }
            
            // 添加图表
            for (Node node : chartNodes) {
                // 为每个图表创建新页
                PDPage chartPage = new PDPage(PDRectangle.A4);
                document.addPage(chartPage);
                
                // 获取节点截图
                WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                
                PDImageXObject image = PDImageXObject.createFromByteArray(document, baos.toByteArray(), "chart");
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, chartPage)) {
                    // 计算图片尺寸
                    float margin = 50;
                    float imageWidth = chartPage.getMediaBox().getWidth() - 2 * margin;
                    float imageHeight = (imageWidth / image.getWidth()) * image.getHeight();
                    float yPosition = chartPage.getMediaBox().getHeight() - margin - imageHeight;
                    
                    // 绘制图片
                    contentStream.drawImage(image, margin, yPosition, imageWidth, imageHeight);
                }
            }
            
            // 添加统计数据页
            if (statisticsMap != null && !statisticsMap.isEmpty()) {
                PDPage statsPage = new PDPage(PDRectangle.A4);
                document.addPage(statsPage);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, statsPage)) {
                    PDFont font = PDType1Font.HELVETICA_BOLD;
                    PDFont regular = PDType1Font.HELVETICA;
                    
                    float margin = 50;
                    float yPosition = statsPage.getMediaBox().getHeight() - margin;
                    
                    // 添加统计数据标题
                    contentStream.beginText();
                    contentStream.setFont(font, 14);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("消费统计数据");
                    contentStream.endText();
                    yPosition -= 30;
                    
                    // 计算总计
                    double total = statisticsMap.values().stream().mapToDouble(Double::doubleValue).sum();
                    
                    contentStream.beginText();
                    contentStream.setFont(font, 12);
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("总计: ¥" + String.format("%.2f", total));
                    contentStream.endText();
                    yPosition -= 20;
                    
                    // 添加各类别数据
                    for (Map.Entry<String, Double> entry : statisticsMap.entrySet()) {
                        double percentage = (entry.getValue() / total) * 100;
                        
                        contentStream.beginText();
                        contentStream.setFont(regular, 10);
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(String.format("%s: ¥%.2f (%.1f%%)", 
                                entry.getKey(), entry.getValue(), percentage));
                        contentStream.endText();
                        
                        yPosition -= 15;
                    }
                }
            }
            
            document.save(filePath);
            return true;
        } catch (IOException e) {
            throw new IOException("生成分析报告失败: " + e.getMessage(), e);
        }
    }

    @Override
    public File showFileChooser(String title, String extensionDescription, String[] extensions, 
                              String initialDirectory, boolean saveDialog) {
        FileChooser fileChooser = new FileChooser();
        
        if (title != null && !title.isEmpty()) {
            fileChooser.setTitle(title);
        }
        
        if (extensions != null && extensions.length > 0) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                    extensionDescription, 
                    toExtensionPattern(extensions)
            );
            fileChooser.getExtensionFilters().add(filter);
        }
        
        if (initialDirectory != null && !initialDirectory.isEmpty()) {
            File dir = new File(initialDirectory);
            if (dir.exists() && dir.isDirectory()) {
                fileChooser.setInitialDirectory(dir);
            }
        }
        
        Stage stage = new Stage();
        
        if (saveDialog) {
            return fileChooser.showSaveDialog(stage);
        } else {
            return fileChooser.showOpenDialog(stage);
        }
    }
    
    /**
     * 将扩展名数组转换为文件选择器模式
     * @param extensions 扩展名数组
     * @return 转换后的扩展名模式
     */
    private String[] toExtensionPattern(String[] extensions) {
        String[] patterns = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            patterns[i] = "*." + extensions[i];
        }
        return patterns;
    }
    
    /**
     * 转义CSV中的特殊字符
     * @param s 原字符串
     * @return 转义后的字符串
     */
    private String escapeCSV(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
} 