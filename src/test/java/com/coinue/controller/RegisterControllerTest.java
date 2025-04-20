package com.coinue.controller;

import com.coinue.model.User;
import com.coinue.util.PageManager;
import com.coinue.util.UserDataManager;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RegisterController测试类
 * 用于测试注册控制器的各项功能
 */
public class RegisterControllerTest extends ApplicationTest {

    // 需要测试的控制器
    private RegisterController controller;
    
    // 模拟的UI组件
    @Mock
    private TextField usernameField;
    
    @Mock
    private PasswordField passwordField;
    
    @Mock
    private Hyperlink forgotPasswordLink;
    
    @Mock
    private Hyperlink signUpLink;
    
    @Mock
    private UserDataManager userDataManager;
    
    @Mock
    private PageManager pageManager;
    
    @Mock
    private Stage stage;
    
    @Mock
    private Scene scene;
    
    @Mock
    private Parent root;
    
    @Mock
    private HBox mainContainer;
    
    @Mock
    private VBox loginForm;
    
    @Mock
    private VBox parentVBox;
    
    @Mock
    private HBox parentHBox;
    
    @Mock
    private Parent grandParent;

    /**
     * 测试前的初始化方法
     */
    @BeforeEach
    public void setUp() throws Exception {
        // 初始化模拟对象
        MockitoAnnotations.openMocks(this);
        
        // 创建控制器实例
        controller = new RegisterController();
        
        // 使用反射注入私有字段
        injectField("usernameField", usernameField);
        injectField("passwordField", passwordField);
        injectField("forgotPasswordLink", forgotPasswordLink);
        injectField("signUpLink", signUpLink);
        
        // 设置模拟场景图结构
        when(forgotPasswordLink.getScene()).thenReturn(scene);
        when(signUpLink.getScene()).thenReturn(scene);
        when(usernameField.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(stage);
        
        // 模拟视图层次结构 - 为forgotPasswordLink设置父级关系
        when(forgotPasswordLink.getParent()).thenReturn(parentVBox);
        when(parentVBox.getParent()).thenReturn(grandParent);
        when(grandParent.getParent()).thenReturn(parentVBox);
        when(parentVBox.getParent()).thenReturn(mainContainer);
        
        // 模拟视图层次结构 - 为signUpLink设置父级关系
        when(signUpLink.getParent()).thenReturn(parentVBox);
        when(parentVBox.getParent()).thenReturn(loginForm);
        when(loginForm.getParent()).thenReturn(mainContainer);
        
        // 初始化页面管理器单例的模拟
        setMockPageManager();
        
        // 模拟UserDataManager单例
        setMockUserDataManager();
    }
    
    /**
     * 使用反射注入字段
     */
    private void injectField(String fieldName, Object value) throws Exception {
        Field field = RegisterController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }
    
    /**
     * 设置模拟的页面管理器
     */
    private void setMockPageManager() throws Exception {
        // 创建一个静态字段访问方法
        Field instanceField = PageManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        
        // 替换单例实例为模拟对象
        instanceField.set(null, pageManager);
    }
    
    /**
     * 设置模拟的用户数据管理器
     */
    private void setMockUserDataManager() throws Exception {
        // 创建一个静态字段访问方法
        Field instanceField = UserDataManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        
        // 替换单例实例为模拟对象
        instanceField.set(null, userDataManager);
    }
    
    /**
     * 测试正确的登录信息
     */
    @Test
    public void testHandleSignIn_ValidCredentials() throws Exception {
        // 设置测试数据
        when(usernameField.getText()).thenReturn("testuser");
        when(passwordField.getText()).thenReturn("password123");
        
        // 模拟UserDataManager行为
        User mockUser = new User();
        when(userDataManager.validateLogin("testuser", "password123")).thenReturn(mockUser);
        
        // 执行登录方法
        invokePrivateMethod("handleSignIn", null);
        
        // 验证页面切换
        verify(pageManager).initStage(stage);
        verify(pageManager).switchToPage("/view/MainPage.fxml");
    }
    
    /**
     * 测试空用户名或密码
     */
    @Test
    public void testHandleSignIn_EmptyCredentials() throws Exception {
        // 设置空用户名和密码
        when(usernameField.getText()).thenReturn("");
        when(passwordField.getText()).thenReturn("");
        
        // 执行登录方法
        invokePrivateMethod("handleSignIn", null);
        
        // 验证页面未切换
        verify(pageManager, never()).switchToPage(anyString());
    }
    
    /**
     * 测试忘记密码功能
     */
    @Test
    public void testHandleForgotPassword() throws Exception {
        // 执行忘记密码方法
        invokePrivateMethod("handleForgotPassword", null);
        
        // 由于动画和异步操作，这部分测试主要验证方法不抛出异常
        verify(stage).setTitle(contains("找回密码"));
    }
    
    /**
     * 测试注册功能
     */
    @Test
    public void testHandleSignUp() throws Exception {
        // 执行注册方法
        invokePrivateMethod("handleSignUp", null);
        
        // 验证标题更改
        verify(stage).setTitle(contains("注册"));
    }
    
    /**
     * 测试下一页功能
     */
    @Test
    public void testNextPage() throws Exception {
        // 执行下一页方法
        invokePrivateMethod("nextPage", null);
        
        // 验证页面切换
        verify(pageManager).initStage(stage);
        verify(pageManager).switchToPage("/view/MainPage.fxml");
    }
    
    /**
     * 执行私有方法的辅助方法
     */
    private void invokePrivateMethod(String methodName, Object[] args) throws Exception {
        Method method;
        if (args == null) {
            method = RegisterController.class.getDeclaredMethod(methodName, javafx.event.ActionEvent.class);
            method.setAccessible(true);
            method.invoke(controller, (javafx.event.ActionEvent) null);
        } else {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            method = RegisterController.class.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            method.invoke(controller, args);
        }
    }
} 