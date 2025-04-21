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


@ExtendWith(ApplicationExtension.class)
public class UserPageTest {


    @Start
    public void start(Stage stage) throws Exception {
        // 初始化PageManager
        PageManager.getInstance().initStage(stage);
        
        // 加载UserPage.fxml
        Parent root = FXMLLoader.load(getClass().getResource("/view/UserPage.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testUserNavigation(FxRobot robot) {
        // 点击Dashboard按钮并验证
        robot.clickOn("Dashboard");
        robot.sleep(2000); // 增加等待时间确保UI加载
        verifyThat("#usernameLabel", isVisible());
        verifyThat("#emailLabel", isVisible());
    }
    
    @Test
    void testBillPaymentNavigation(FxRobot robot) {
        // 进入用户页面
        robot.clickOn("Dashboard");
        robot.sleep(3000); // 增加等待时间确保UI加载
        
        // 点击Bill Payment按钮并验证
        robot.clickOn("Bill Payment");
        robot.sleep(3000); // 增加等待时间确保UI加载
        
        // 验证所有UI元素 - 使用与FXML文件匹配的ID
        verifyThat("#titleLabel", isVisible());
        verifyThat("#repaymentChart", isVisible());
        verifyThat("#billTable", isVisible());
        
        // 不再测试不存在的元素
        // 原测试中的importButton在FXML中是一个没有ID的按钮，使用文本查找
        Button importButton = robot.lookup("Import CSV").queryButton();
        verifyThat(importButton, isVisible());
    }
    
}