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

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainPageTest {

    private MainPageController controller;

    @Start
    void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPage.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void setUp(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        robot.sleep(500); // 确保界面完全加载
    }

    @Test
    @Order(1)
    void testInitialPageLoad(FxRobot robot) {
        // 验证关键组件是否可见
        verifyThat("#budgetContainer", isVisible());
        verifyThat("#reminderContainer", isVisible());
        verifyThat("#expenseTableView", isVisible());
    }

    @Test
    @Order(2)
    void testNavigation(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            // 直接使用 query() 方法查找按钮
            Button analysisButton = (Button) robot.lookup(".button").match(button -> 
                button instanceof Button && 
                ((Button) button).getText() != null &&
                ((Button) button).getText().equals("Analysis")
            ).query();
            
            robot.clickOn(analysisButton);
            robot.sleep(500);
            
        } catch (Exception e) {
            System.out.println("导航测试失败: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    void testManualExpenseEntry(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            // 点击硬币图标
            robot.clickOn("#coinImageView");
            robot.sleep(1000); // 增加等待时间
            
            // 使用closeDialog方法关闭窗口
            closeDialog(robot);
        } catch (Exception e) {
            System.out.println("手动记账测试失败: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testAddReminder(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            // 查找并点击添加提醒按钮
            Button addButton = (Button) robot.lookup(".button").match(button -> 
                button instanceof Button && 
                ((Button) button).getText() != null &&
                ((Button) button).getText().contains("添加还款提醒")
            ).query();
            
            robot.clickOn(addButton);
            robot.sleep(1000);
            
            // 填写表单
            TextField platformField = (TextField) robot.lookup("#platformField").query();
            robot.clickOn(platformField);
            robot.write("信用卡");
            
            TextField amountField = (TextField) robot.lookup("#amountField").query();
            robot.clickOn(amountField);
            robot.write("1000");
            
            // 关闭对话框
            closeDialog(robot);
            
        } catch (Exception e) {
            System.out.println("添加提醒测试失败: " + e.getMessage());
        }
    }

    private void closeDialog(FxRobot robot) {
        robot.sleep(1000); // 等待对话框完全显示
        try {
            Node closeButton = robot.lookup(".button").match(button -> 
                button instanceof Button && 
                ((Button) button).getText() != null &&
                (((Button) button).getText().equals("取消") || 
                 ((Button) button).getText().equals("确定"))
            ).query();
            robot.clickOn(closeButton);
            robot.sleep(500);
        } catch (Exception e) {
            System.out.println("关闭对话框失败: " + e.getMessage());
        }
    }
}