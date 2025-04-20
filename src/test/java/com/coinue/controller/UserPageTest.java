package com.coinue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
public class UserPageTest {

    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainPage.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testUserNavigation(FxRobot robot) {
        // 点击User按钮并验证
        robot.clickOn("User");
        robot.sleep(1000); // 等待页面加载
        verifyThat("#usernameLabel", isVisible());
    }
    
    @Test
    void testBillPaymentNavigation(FxRobot robot) {
        // 进入用户页面
        robot.clickOn("User");
        robot.sleep(1000);
        
        // 点击Bill Payment按钮并验证
        robot.clickOn("Bill Payment");
        robot.sleep(3000); // 等待页面加载
        verifyThat("#titleLabel", isVisible());
        verifyThat("#chartContainer", isVisible());
        verifyThat("#paymentTable", isVisible());
        verifyThat("#importButton", isVisible());
        
        // 测试按钮点击功能
        robot.clickOn("#importButton");
        robot.sleep(1000);
        verifyThat("#fileChooserDialog", isVisible());
    }
}