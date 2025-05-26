package com.coinue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.coinue.controller.AnalysisPageController;
import com.coinue.util.PageManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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

    private Stage stage;

    /**
     * 初始化测试环境
     * @param stage JavaFX主舞台
     * @throws Exception 如果加载FXML文件失败
     */
    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        // 初始化PageManager
        PageManager.getInstance().initStage(stage);
        
        // 从资源文件加载MainPage.fxml界面
        URL mainPageUrl = getClass().getResource("/view/MainPage.fxml");
        if (mainPageUrl == null) {
            throw new IOException("Cannot find MainPage.fxml");
        }
        
        // 使用PageManager切换到主页面
        PageManager.getInstance().switchToPage("/view/MainPage.fxml");
        stage.show();
    }

    /**
     * 测试分析页面导航功能
     * 验证点击Analysis按钮后是否正确切换到分析页面
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    void testAnalysisNavigation(FxRobot robot) {
        // 等待主页面完全加载
        robot.sleep(2000);
        // 模拟用户点击Analysis按钮
        robot.clickOn("Analysis");
        // 等待2秒确保页面加载完成
        robot.sleep(2000);
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
        // 等待主页面完全加载
        robot.sleep(2000);
        // 首先进入分析页面
        robot.clickOn("Analysis");
        robot.sleep(2000); // 等待2秒确保页面加载完成
        
        // 获取测试数据文件路径
        URL testFileUrl = getClass().getResource("/test_expense_records.csv");
        if (testFileUrl == null) {
            fail("Cannot find test_expense_records.csv");
        }
        String testFilePath = testFileUrl.getFile();
        
        // 加载AnalysisPage.fxml界面
        URL analysisPageUrl = getClass().getResource("/view/AnalysisPage.fxml");
        if (analysisPageUrl == null) {
            fail("Cannot find AnalysisPage.fxml");
        }
        FXMLLoader loader = new FXMLLoader(analysisPageUrl);
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
        
        // 等待5秒确保数据加载和图表生成完成
        robot.sleep(5000);
        
        // 验证饼图和柱状图是否可见
        verifyThat("#expensePieChart", isVisible());
        verifyThat("#expenseBarChart", isVisible());
    }
}