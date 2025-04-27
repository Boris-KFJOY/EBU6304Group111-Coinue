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

/**
 * AnalysisPageController的测试类
 * 使用TestFX框架进行UI自动化测试
 * 测试分析页面导航和图表显示功能
 */
@ExtendWith(ApplicationExtension.class)
public class AnalysisPageTest {

    /**
     * 初始化测试环境
     * @param stage JavaFX主舞台
     * @throws Exception 如果加载FXML文件失败
     */
    @Start
    public void start(Stage stage) throws Exception {
        // 从资源文件加载MainPage.fxml界面
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        // 设置场景并显示舞台
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * 测试分析页面导航功能
     * 验证点击Analysis按钮后是否正确切换到分析页面
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    void testAnalysisNavigation(FxRobot robot) {
        // 模拟用户点击Analysis按钮
        robot.clickOn("Analysis");
        // 等待1秒确保页面加载完成
        robot.sleep(1000);
        // 验证分析页面的预算进度条是否可见
        verifyThat("#budgetProgressBar", isVisible());
    }
    
   
    /**
     * 测试图表分析功能
     * 验证导入测试数据后饼图和柱状图是否正确显示
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     * @throws IOException 如果文件加载失败
     */
    @Test
    void testChartAnalysis(FxRobot robot) throws IOException {
        // 首先进入分析页面
        robot.clickOn("Analysis");
        robot.sleep(1000); // 等待1秒确保页面加载完成
        
        // 获取测试数据文件路径
        String testFilePath = getClass().getResource("/test_expense_records.csv").getFile();
        // 加载AnalysisPage.fxml界面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AnalysisPage.fxml"));
        Parent root = loader.load();
        AnalysisPageController controller = loader.getController();
        
        // 在JavaFX应用线程中执行文件导入操作
        robot.interact(() -> {
            try {
                // 验证测试文件是否存在
                File testFile = new File(testFilePath);
                assertTrue(testFile.exists(), "测试文件不存在");
                // 调用控制器方法导入分析文件
                controller.handleImportAnalysisFile(testFile);
            } catch (IOException e) {
                fail("文件导入失败: " + e.getMessage());
            }
        });
        
        // 等待3秒确保数据加载和图表生成完成
        robot.sleep(3000);
        
        // 验证饼图和柱状图是否可见
        verifyThat("#expensePieChart", isVisible());
        verifyThat("#expenseBarChart", isVisible());
    }
}