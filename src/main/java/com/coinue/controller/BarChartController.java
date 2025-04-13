package com.coinue.controller;

import com.coinue.util.ChartGenerator;
// 使用JavaFX 8及以上版本的SwingFXUtils
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BarChartController {

    @FXML
    private BarChart<String, Number> expenseBarChart;

    @FXML
    private Button exportPdfButton;

    @FXML
    public void initialize() {
        loadDataAndUpdateChart();
    }

    private void loadDataAndUpdateChart() {
        Map<String, Double> categoryAmounts = new HashMap<>();
        try {
            // 读取CSV文件
            Files.lines(Paths.get("src/test/resources/test_expense_records.csv"))
                    .skip(1) // 跳过标题行
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        String category = parts[2];
                        double amount = Double.parseDouble(parts[3]);
                        categoryAmounts.merge(category, amount, Double::sum);
                    });

            // 创建数据系列
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("消费金额");

            // 添加数据点
            categoryAmounts.forEach((category, amount) ->
                    series.getData().add(new XYChart.Data<>(category, amount)));

            // 更新图表
            expenseBarChart.getData().clear();
            expenseBarChart.getData().add(series);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存PDF文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF文件", "*.pdf"));
        File file = fileChooser.showSaveDialog(expenseBarChart.getScene().getWindow());

        if (file != null) {
            try {
                // 创建图表快照
                WritableImage snapshot = expenseBarChart.snapshot(new SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

                // 创建PDF文档
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                // 将图表添加到PDF
                PDImageXObject image = LosslessFactory.createFromImage(document, bufferedImage);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // 计算图片在页面中的位置和大小
                float scale = 0.8f;
                float width = page.getMediaBox().getWidth() * scale;
                float height = width * bufferedImage.getHeight() / bufferedImage.getWidth();
                float x = (page.getMediaBox().getWidth() - width) / 2;
                float y = (page.getMediaBox().getHeight() - height) / 2;

                contentStream.drawImage(image, x, y, width, height);
                contentStream.close();

                // 保存PDF文件
                document.save(file);
                document.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}