package com.coinue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import com.coinue.util.PageManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * UserPageController的测试类
 * 使用TestFX框架进行UI自动化测试
 * 测试用户页面导航和账单支付功能
 */
@ExtendWith(ApplicationExtension.class)
public class UserPageTest {

    private Stage stage;

    /**
     * 初始化测试环境
     * @param stage JavaFX主舞台
     * @throws Exception 如果加载FXML文件失败
     */
    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        // 初始化PageManager单例并设置舞台
        PageManager.getInstance().initStage(stage);
        
        // 从资源文件加载UserPage.fxml界面
        Parent root = FXMLLoader.load(getClass().getResource("/view/UserPage.fxml"));
        
        // 设置场景并显示舞台
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 测试用户导航功能
     * 验证用户页面的基本元素是否正确显示
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    void testUserPageElements(FxRobot robot) {
        // 验证导航按钮是否存在
        verifyThat("Homepage", isVisible());
        verifyThat("Analysis", isVisible());
        verifyThat("User", isVisible());
        
        // 等待2秒确保UI完全加载
        robot.sleep(2000); 
        
        // 验证用户信息区域是否可见
        verifyThat("#usernameLabel", isVisible());
        verifyThat("#emailLabel", isVisible());
        
        // 验证功能按钮是否存在
        verifyThat("Change Password", isVisible());
        verifyThat("Bill Payment", isVisible());
        verifyThat("Export Data", isVisible());
        verifyThat("Logout", isVisible());
    }
    
    /**
     * 测试账单支付页面导航功能
     * 验证从用户页面到账单支付页面的导航
     * @param robot TestFX提供的机器人对象，用于模拟用户操作
     */
    @Test
    void testBillPaymentNavigation(FxRobot robot) {
        // 点击账单支付按钮
        robot.clickOn("Bill Payment");
        robot.sleep(2000); // 等待页面切换完成
        
        // 验证账单支付页面的标题是否正确显示
        verifyThat("Bill Payment Management", isVisible());
        
        // 验证关键UI元素是否可见
        verifyThat("#repaymentAmountLabel", isVisible());
        verifyThat("#creditLimitField", isVisible());
        verifyThat("#dateFilterPicker", isVisible());
        verifyThat("#billTable", isVisible());
        verifyThat("#repaymentChart", isVisible());
        
        // 验证操作按钮是否存在
        verifyThat("📄 Import CSV", isVisible());
        verifyThat("🗑️ Clear Data", isVisible());
        verifyThat("💳 Pay Bills", isVisible());
        verifyThat("📊 Generate Report", isVisible());
    }
}