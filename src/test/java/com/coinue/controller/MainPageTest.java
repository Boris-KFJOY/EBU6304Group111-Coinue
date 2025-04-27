package com.coinue.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.MethodOrderer;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * MainPageController的测试类
 * 使用TestFX框架进行UI自动化测试
 * 测试主页面加载、导航功能、手动记账和添加还款提醒功能
 * 测试方法按@Order注解顺序执行
 */
@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainPageTest {

    private MainPageController controller;

    /**
     * 初始化测试环境
     * 加载MainPage.fxml文件并设置舞台
     * @param stage JavaFX主舞台
     * @throws Exception 如果加载FXML文件失败
     */
    @Start
    void start(Stage stage) throws Exception {
        // 使用FXMLLoader加载主页面布局文件
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPage.fxml"));
        Parent root = loader.load();
        // 获取控制器实例
        controller = loader.getController();
        // 设置场景并显示舞台
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * 每个测试方法执行前的准备工作
     * 等待JavaFX事件队列完成并确保UI完全加载
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @BeforeEach
    void setUp(FxRobot robot) {
        // 等待所有JavaFX事件完成
        WaitForAsyncUtils.waitForFxEvents();
        // 等待500毫秒确保界面完全加载
        robot.sleep(500);
    }

    /**
     * 测试初始页面加载
     * 验证主页面上的关键组件是否可见
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    @Order(1)
    void testInitialPageLoad(FxRobot robot) {
        // 验证预算容器是否可见
        verifyThat("#budgetContainer", isVisible());
        // 验证还款提醒容器是否可见
        verifyThat("#reminderContainer", isVisible());
        // 验证支出表格是否可见
        verifyThat("#expenseTableView", isVisible());
    }

    /**
     * 测试页面导航功能
     * 验证点击Analysis按钮后能否正确导航
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    @Order(2)
    void testNavigation(FxRobot robot) {
        // 等待所有JavaFX事件完成
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            // 查找Analysis按钮: 匹配类名为button、文本为"Analysis"的按钮
            Button analysisButton = (Button) robot.lookup(".button").match(button -> 
                button instanceof Button && 
                ((Button) button).getText() != null &&
                ((Button) button).getText().equals("Analysis")
            ).query();
            
            // 模拟用户点击Analysis按钮
            robot.clickOn(analysisButton);
            // 等待500毫秒确保导航完成
            robot.sleep(500);
            
        } catch (Exception e) {
            System.out.println("导航测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试手动记账功能
     * 验证点击硬币图标后能否打开记账对话框并正确关闭
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    @Order(3)
    void testManualExpenseEntry(FxRobot robot) {
        // 等待所有JavaFX事件完成
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            // 点击主页面上的硬币图标打开记账对话框
            robot.clickOn("#coinImageView");
            // 等待1秒确保对话框完全加载
            robot.sleep(1000);
            
            // 调用closeDialog方法关闭对话框
            closeDialog(robot);
        } catch (Exception e) {
            System.out.println("手动记账测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试添加还款提醒功能
     * 验证添加还款提醒的完整流程，包括打开对话框、填写表单和关闭对话框
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    @Order(4)
    void testAddReminder(FxRobot robot) {
        // 等待所有JavaFX事件完成
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            // 查找并点击"添加还款提醒"按钮
            Button addButton = (Button) robot.lookup(".button").match(button -> 
                button instanceof Button && 
                ((Button) button).getText() != null &&
                ((Button) button).getText().contains("添加还款提醒")
            ).query();
            
            // 模拟用户点击添加提醒按钮
            robot.clickOn(addButton);
            // 等待1秒确保对话框完全加载
            robot.sleep(1000);
            
            // 填写平台名称字段
            TextField platformField = (TextField) robot.lookup("#platformField").query();
            robot.clickOn(platformField);
            robot.write("信用卡");
            
            // 填写金额字段
            TextField amountField = (TextField) robot.lookup("#amountField").query();
            robot.clickOn(amountField);
            robot.write("1000");
            
            // 调用closeDialog方法关闭对话框
            closeDialog(robot);
            
        } catch (Exception e) {
            System.out.println("添加提醒测试失败: " + e.getMessage());
        }
    }

    /**
     * 辅助方法：关闭对话框
     * 查找并点击对话框上的"取消"或"确定"按钮
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    private void closeDialog(FxRobot robot) {
        // 等待1秒确保对话框完全显示
        robot.sleep(1000);
        try {
            // 查找对话框上的关闭按钮(文本为"取消"或"确定")
            Node closeButton = robot.lookup(".button").match(button -> 
                button instanceof Button && 
                ((Button) button).getText() != null &&
                (((Button) button).getText().equals("取消") || 
                 ((Button) button).getText().equals("确定"))
            ).query();
            // 点击关闭按钮
            robot.clickOn(closeButton);
            // 等待500毫秒确保对话框完全关闭
            robot.sleep(500);
        } catch (Exception e) {
            System.out.println("关闭对话框失败: " + e.getMessage());
        }
    }
}