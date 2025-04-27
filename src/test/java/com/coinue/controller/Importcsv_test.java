package com.coinue.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import javafx.application.Platform;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import com.coinue.controller.MainPageController;
import com.coinue.controller.ManualEntryDialogController;
import com.coinue.model.ExpenseRecord;
import com.coinue.util.CSVHandler;
import org.mockito.Mockito;

/**
 * Importcsv_test 测试类
 * 用于测试ManualEntryDialogController中的CSV导入功能
 * 包含以下测试场景：
 * 1. 测试有效CSV文件导入
 * 2. 测试无效CSV文件导入
 * 3. 测试记录验证功能
 * 4. 测试导入结果显示
 * 
 * 测试方法按@Order注解顺序执行
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Importcsv_test {

    private ManualEntryDialogController controller;
    private MainPageController mockMainPageController;
    
    /**
     * 临时目录，用于存放测试过程中生成的CSV文件
     * JUnit会自动创建并在测试完成后清理
     */
    @TempDir
    Path tempDir;

    /**
     * 每个测试方法执行前的初始化方法
     * 1. 创建ManualEntryDialogController实例
     * 2. 使用Mockito创建MainPageController的mock对象
     * 3. 将mock对象设置到controller中
     */
    @BeforeEach
    void setUp() {
        controller = new ManualEntryDialogController();
        // 使用Mockito创建mock对象
        mockMainPageController = Mockito.mock(MainPageController.class);
        controller.setMainPageController(mockMainPageController);
    }

    /**
     * 测试有效CSV文件导入
     * 测试场景：
     * 1. 创建一个符合格式要求的CSV文件
     * 2. 使用CSVHandler读取该文件
     * 3. 验证读取的记录数量和内容是否正确
     * 
     * @throws IOException 如果文件操作失败
     */
    @Test
    @Order(1)
    @DisplayName("测试有效CSV文件导入")
    void testValidCSVImport() throws IOException {
        // 在临时目录中创建测试CSV文件
        File testFile = tempDir.resolve("test.csv").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            // 写入CSV文件头，格式为：Category,Amount,Date,Name
            writer.write("Category,Amount,Date,Name\n");
            // 写入两条测试记录
            writer.write("餐饮,100.0,2024-03-20,午餐\n");  // 餐饮支出记录
            writer.write("交通,50.0,2024-03-20,地铁\n");   // 交通支出记录
        }

        // 调用CSVHandler读取CSV文件
        List<ExpenseRecord> records = CSVHandler.readExpenseRecords(testFile.getPath());
        
        // 验证返回的记录列表不为空且包含2条记录
        assertNotNull(records, "读取的记录列表不应为null");
        assertEquals(2, records.size(), "应正确读取2条记录");
        
        // 验证第一条记录的内容是否正确
        ExpenseRecord firstRecord = records.get(0);
        assertEquals("餐饮", firstRecord.getCategory(), "类别应为'餐饮'");
        assertEquals(100.0, firstRecord.getAmount(), "金额应为100.0");
        assertEquals(LocalDate.parse("2024-03-20"), firstRecord.getDate(), "日期应为2024-03-20");
        assertEquals("午餐", firstRecord.getName(), "名称应为'午餐'");
    }

    /**
     * 测试无效CSV文件导入
     * 测试场景：
     * 1. 创建一个不符合格式要求的CSV文件
     * 2. 使用CSVHandler读取该文件
     * 3. 验证系统是否正确处理无效格式
     * 
     * @throws IOException 如果文件操作失败
     */
    @Test
    @Order(2)
    @DisplayName("测试无效CSV文件导入")
    void testInvalidCSVImport() throws IOException {
        // 在临时目录中创建无效格式的CSV文件
        File invalidFile = tempDir.resolve("invalid.csv").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            // 写入无效的CSV文件头
            writer.write("Invalid,Format,Data\n");
            // 写入无效的记录数据
            writer.write("无效类别,abc,2024-03-20\n");
        }

        // 调用CSVHandler读取无效格式的CSV文件
        List<ExpenseRecord> records = CSVHandler.readExpenseRecords(invalidFile.getPath());
        
        // 验证系统是否正确处理无效格式：
        // 1. 返回的记录列表应为空，或
        // 2. 列表中不应包含类别为"无效类别"的记录
        assertTrue(records.isEmpty() || records.stream()
                .noneMatch(record -> "无效类别".equals(record.getCategory())), 
                "应正确处理无效格式的CSV文件");
    }

    /**
     * 测试记录验证功能
     * 测试场景：
     * 1. 创建一个有效的支出记录
     * 2. 创建一个无效的支出记录（金额为负，类别无效）
     * 3. 使用反射调用ManualEntryDialogController的私有validateRecord方法
     * 4. 验证方法对有效和无效记录的正确判断
     */
    @Test
    @Order(3)
    @DisplayName("测试记录验证功能")
    void testRecordValidation() {
        // 创建有效的支出记录
        ExpenseRecord validRecord = new ExpenseRecord(100.0, "餐饮", "午餐", LocalDate.now());
        validRecord.setRecordType("支出");  // 设置记录类型为支出
        
        // 创建无效的支出记录（金额为负，类别无效）
        ExpenseRecord invalidRecord = new ExpenseRecord(-50.0, "无效类别", "测试", LocalDate.now());
        invalidRecord.setRecordType("支出");  // 设置记录类型为支出

        // 使用反射调用ManualEntryDialogController的私有validateRecord方法
        try {
            // 获取私有validateRecord方法并设置可访问
            java.lang.reflect.Method validateMethod = ManualEntryDialogController.class
                    .getDeclaredMethod("validateRecord", ExpenseRecord.class);
            validateMethod.setAccessible(true);
            
            // 验证方法对有效记录的判断应为true
            assertTrue((Boolean) validateMethod.invoke(controller, validRecord), 
                    "有效的支出记录应通过验证");
            
            // 验证方法对无效记录的判断应为false
            assertFalse((Boolean) validateMethod.invoke(controller, invalidRecord), 
                    "无效的支出记录不应通过验证");
        } catch (Exception e) {
            fail("验证方法调用失败: " + e.getMessage());
        }
    }

    /**
     * 测试导入结果显示功能
     * 测试场景：
     * 1. 确保controller不为null
     * 2. 初始化JavaFX环境
     * 3. 使用反射调用ManualEntryDialogController的私有showImportResult方法
     * 4. 测试两种场景：
     *    - 全部导入成功（5条成功，0条失败）
     *    - 部分导入成功（3条成功，2条失败）
     * 5. 等待JavaFX线程执行完成
     * 
     * 注意：由于涉及JavaFX UI操作，测试需要在JavaFX Application Thread上运行
     */
    @Test
    @Order(4)
    @DisplayName("测试导入结果显示")
    void testImportResultDisplay() {
        try {
            // 验证controller已正确初始化
            assertNotNull(controller, "Controller实例不应为null");
            
            // 初始化JavaFX环境
            Platform.startup(() -> {});
            
            // 获取私有showImportResult方法并设置可访问
            java.lang.reflect.Method showResultMethod = ManualEntryDialogController.class
                    .getDeclaredMethod("showImportResult", int.class, int.class);
            showResultMethod.setAccessible(true);
            
            // 在JavaFX Application Thread上执行测试
            Platform.runLater(() -> {
                try {
                    // 测试全部导入成功的场景（5条成功，0条失败）
                    showResultMethod.invoke(controller, 5, 0);
                    
                    // 测试部分导入成功的场景（3条成功，2条失败）
                    showResultMethod.invoke(controller, 3, 2);
                } catch (Exception e) {
                    fail("显示导入结果方法调用失败: " + e.getMessage());
                }
            });
            
            // 等待JavaFX线程执行完成（1秒）
            Thread.sleep(1000);
            
        } catch (Exception e) {
            fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 每个测试方法执行后的清理方法
     * 1. 将controller置为null
     * 2. 将mockMainPageController置为null
     */
    @AfterEach
    void tearDown() {
        controller = null;
        mockMainPageController = null;
    }

    // 删除原来的mock方法
    // private MainPageController mock(Class<MainPageController> aClass) {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
}