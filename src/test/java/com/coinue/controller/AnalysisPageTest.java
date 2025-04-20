package com.coinue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.coinue.controller.AnalysisPageController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
public class AnalysisPageTest {

    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testAnalysisNavigation(FxRobot robot) {
        // 点击Analysis按钮并验证
        robot.clickOn("Analysis");
        // 验证页面已切换到分析页面
        robot.sleep(1000); // 等待页面加载
        verifyThat("#budgetProgressBar", isVisible());
    }
    
   
    @Test
    void testChartAnalysis(FxRobot robot) throws IOException {
        // 点击Analysis按钮进入分析页面
        robot.clickOn("Analysis");
        robot.sleep(1000);
        
        // 使用相对路径获取测试文件
        String testFilePath = getClass().getResource("/test_expense_records.csv").getFile();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AnalysisPage.fxml"));
        Parent root = loader.load();
        AnalysisPageController controller = loader.getController();
        
        // 添加更健壮的异常处理和验证
        robot.interact(() -> {
            try {
                File testFile = new File(testFilePath);
                assertTrue(testFile.exists(), "Test file does not exist");
                controller.handleImportAnalysisFile(testFile);
            } catch (IOException e) {
                fail("File import failed: " + e.getMessage());
            }
        });
        
        // 等待数据加载和图表生成
        robot.sleep(3000);
        
        // 验证图表是否正确显示
        verifyThat("#expensePieChart", isVisible());
        verifyThat("#expenseBarChart", isVisible());
    }
}