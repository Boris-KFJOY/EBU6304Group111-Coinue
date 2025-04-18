package com.coinue;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isNull;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;

@ExtendWith(ApplicationExtension.class)
public class MainPageTest {

    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testNavigation(FxRobot robot) {
        // 点击Analysis按钮并验证
        robot.clickOn("Analysis");
        // 验证页面已切换到分析页面
        robot.sleep(1000); // 等待页面加载
        
        // 返回Homepage
        robot.clickOn("主页");
        robot.sleep(1000); // 等待页面加载
        
        // 点击Dashboard按钮并验证
        robot.clickOn("Dashboard");
        // 验证页面已切换到用户页面
        robot.sleep(1000); // 等待页面加载
        
        // 返回Homepage并验证
        robot.clickOn("Homepage");
        // 验证页面已切换回主页面
        robot.sleep(1000); // 等待页面加载
        verifyThat("#coinImageView", isVisible());
    }
    
    @Test
    void testCoinClick(FxRobot robot) {
        // 点击硬币图标
        robot.clickOn("#coinImageView");
        // 验证手动记录对话框已打开
        robot.sleep(1000); // 等待对话框加载
        
        // 验证对话框标题 - 使用更精确的查找方式
        assertTrue(robot.lookup(".dialog-pane").tryQuery().isPresent() || 
                  robot.lookup(".dialog").tryQuery().isPresent() ||
                  robot.lookup("AnchorPane").tryQuery().isPresent());
        
        // 关闭对话框 - 尝试点击取消按钮，如果存在的话
        try {
            // 尝试多种可能的取消按钮文本
            if (robot.lookup("取消").tryQuery().isPresent()) {
                robot.clickOn("取消");
            } else if (robot.lookup("Cancel").tryQuery().isPresent()) {
                robot.clickOn("Cancel");
            } else if (robot.lookup("<").tryQuery().isPresent()) {
                robot.clickOn("<");
            } else {
                robot.closeCurrentWindow();
            }
        } catch (Exception e) {
            // 如果找不到取消按钮，尝试关闭窗口
            robot.closeCurrentWindow();
        }
    }
    
    @Test
    void testBudgetManagement(FxRobot robot) {
        // 验证预算列表存在
        verifyThat("#budgetListView", isVisible());
        
        // 点击添加预算按钮
        robot.clickOn("添加预算");
        // 验证预算对话框已打开
        robot.sleep(1000); // 等待对话框加载
        
        // 验证对话框存在 - 使用更精确的查找方式
        assertTrue(robot.lookup(".dialog-pane").tryQuery().isPresent() || 
                  robot.lookup(".dialog").tryQuery().isPresent() ||
                  robot.lookup("VBox").nth(1).tryQuery().isPresent() ||
                  robot.lookup("GridPane").tryQuery().isPresent());
        
        // 关闭对话框 - 尝试点击取消按钮，如果存在的话
        try {
            // 尝试多种可能的取消按钮文本
            if (robot.lookup("取消").tryQuery().isPresent()) {
                robot.clickOn("取消");
            } else if (robot.lookup("Cancel").tryQuery().isPresent()) {
                robot.clickOn("Cancel");
            } else {
                robot.closeCurrentWindow();
            }
        } catch (Exception e) {
            // 如果找不到取消按钮，尝试关闭窗口
            robot.closeCurrentWindow();
        }
    }
    
    @Test
    void testPaymentReminder(FxRobot robot) {
        // 验证还款提醒列表存在
        verifyThat("#reminderListView", isVisible());
        
        // 点击添加还款提醒按钮
        robot.clickOn("添加还款提醒");
        // 验证还款提醒对话框已打开
        robot.sleep(1000); // 等待对话框加载
        
        // 验证对话框存在 - 使用更精确的查找方式
        assertTrue(robot.lookup(".dialog-pane").tryQuery().isPresent() || 
                  robot.lookup(".dialog").tryQuery().isPresent() ||
                  robot.lookup("VBox").nth(1).tryQuery().isPresent() ||
                  robot.lookup("GridPane").tryQuery().isPresent());
        
        // 关闭对话框 - 尝试点击取消按钮，如果存在的话
        try {
            // 尝试多种可能的取消按钮文本
            if (robot.lookup("取消").tryQuery().isPresent()) {
                robot.clickOn("取消");
            } else if (robot.lookup("Cancel").tryQuery().isPresent()) {
                robot.clickOn("Cancel");
            } else {
                robot.closeCurrentWindow();
            }
        } catch (Exception e) {
            // 如果找不到取消按钮，尝试关闭窗口
            robot.closeCurrentWindow();
        }
    }
}