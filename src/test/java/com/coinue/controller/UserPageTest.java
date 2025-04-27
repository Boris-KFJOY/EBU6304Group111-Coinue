package com.coinue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.coinue.util.PageManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;


/**
 * UserPageController的测试类
 * 使用TestFX框架进行UI自动化测试
 * 测试用户页面导航和账单支付功能
 */
@ExtendWith(ApplicationExtension.class)
public class UserPageTest {


    /**
     * 初始化测试环境
     * @param stage JavaFX主舞台
     * @throws Exception 如果加载FXML文件失败
     */
    @Start
    public void start(Stage stage) throws Exception {
        // 初始化PageManager单例并设置舞台
        PageManager.getInstance().initStage(stage);
        
        // 从资源文件加载UserPage.fxml界面
        Parent root = FXMLLoader.load(getClass().getResource("/view/UserPage.fxml"));
        
        // 设置场景并显示舞台
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * 测试用户导航功能
     * 验证点击Dashboard按钮后是否正确显示用户信息
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    void testUserNavigation(FxRobot robot) {
        // 模拟用户点击Dashboard按钮
        robot.clickOn("Dashboard");
        
        // 等待2秒确保UI完全加载
        robot.sleep(2000); 
        
        // 验证用户名和邮箱标签是否可见
        verifyThat("#usernameLabel", isVisible());
        verifyThat("#emailLabel", isVisible());
    }
    
    /**
     * 测试账单支付页面导航功能
     * 验证从Dashboard进入账单支付页面的流程和UI元素
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    void testBillPaymentNavigation(FxRobot robot) {
        // 首先进入用户Dashboard页面
        robot.clickOn("Dashboard");
        robot.sleep(3000); // 等待3秒确保页面加载完成
        
        // 从Dashboard导航到账单支付页面
        robot.clickOn("Bill Payment");
        robot.sleep(3000); // 等待3秒确保页面切换完成
        
        // 验证账单支付页面的关键UI元素是否可见
        verifyThat("#titleLabel", isVisible());    // 验证标题标签
        verifyThat("#repaymentChart", isVisible()); // 验证还款图表
        verifyThat("#billTable", isVisible());     // 验证账单表格
        
        // 查找并验证"Import CSV"按钮(该按钮在FXML中没有ID，使用文本查找)
        Button importButton = robot.lookup("Import CSV").queryButton();
        verifyThat(importButton, isVisible());
    }
    
}