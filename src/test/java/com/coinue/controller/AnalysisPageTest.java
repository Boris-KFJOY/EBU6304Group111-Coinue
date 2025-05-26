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
import javafx.scene.control.DialogPane;
import javafx.scene.Node;

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
        AnalysisPageController.testModeActive = true; // Activate test mode
        try {
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
            robot.sleep(1000); // 缩短等待时间，因为show()是非阻塞的
            
            // 关闭可能出现的"导入成功"通知对话框
            try {
                // 等待对话框出现并查找 DialogPane
                DialogPane dialogPane = robot.lookup(".dialog-pane").queryAs(DialogPane.class);
                org.testfx.util.WaitForAsyncUtils.waitForFxEvents(); // 确保UI更新

                // 在DialogPane中查找按钮。标准的 AlertType.INFORMATION 的"确定"按钮
                // 通常是 ButtonType.OK。我们可以查找所有按钮并点击第一个，
                // 或者如果知道 fx:id 或特定文本，可以使用更精确的查找。
                // 由于直接文本查找"确定"失败，我们尝试查找通用的button并点击。
                // 假设"确定"按钮是对话框中的主要/默认按钮。
                
                // 尝试查找文本为"确定"的按钮，如果 Alert 的按钮是标准 ButtonType.OK，它的文本通常是本地化的"确定"
                // Node okButton = robot.from(dialogPane).lookup((Node node) -> node instanceof javafx.scene.control.Button && "确定".equals(((javafx.scene.control.Button)node).getText())).query();
                // robot.clickOn(okButton);

                // 更通用的方法：查找 .dialog-pane 内的 .button
                // 如果有多个按钮，这可能会点击错误的按钮。但对于只有一个"确定"按钮的INFO Alert，这通常是安全的。
                Node buttonInDialog = robot.from(dialogPane).lookup(".button").queryButton();
                robot.clickOn(buttonInDialog);
                
                org.testfx.util.WaitForAsyncUtils.waitForFxEvents(); // 等待对话框关闭
                robot.sleep(500); // 短暂等待
            } catch (RuntimeException e) { 
                System.out.println("Warning: Could not find or click button on notification dialog. Error: " + e.getMessage());
                // 打印更详细的堆栈跟踪以便调试
                e.printStackTrace();
            }
            
            // 验证饼图和柱状图是否可见
            verifyThat("#expensePieChart", isVisible());
            verifyThat("#expenseBarChart", isVisible());
        } finally {
            AnalysisPageController.testModeActive = false; // Deactivate test mode
        }
    }
}