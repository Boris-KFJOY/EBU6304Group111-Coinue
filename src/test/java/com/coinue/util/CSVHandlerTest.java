package com.coinue.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CSVHandlerTest {

    private ManualEntryDialogController controller;
    private MainPageController mockMainPageController;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        controller = new ManualEntryDialogController();
        mockMainPageController = mock(MainPageController.class);
        controller.setMainPageController(mockMainPageController);
    }

    @Test
    @Order(1)
    @DisplayName("测试有效CSV文件导入")
    void testValidCSVImport() throws IOException {
        // 创建测试用CSV文件
        File testFile = tempDir.resolve("test.csv").toFile();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("Type,Amount,Date,Additional\n");
            writer.write("餐饮,100.0,2024-03-20,午餐\n");
            writer.write("交通,50.0,2024-03-20,地铁\n");
        }

        // 测试CSV文件读取
        List<ExpenseRecord> records = CSVHandler.readExpenseRecords(testFile.getPath());
        
        assertNotNull(records);
        assertEquals(2, records.size());
        
        // 验证第一条记录
        ExpenseRecord firstRecord = records.get(0);
        assertEquals("餐饮", firstRecord.getCategory());
        assertEquals(100.0, firstRecord.getAmount());
        assertEquals(LocalDate.parse("2024-03-20"), firstRecord.getDate());
        assertEquals("午餐", firstRecord.getName());
    }

    @Test
    @Order(2)
    @DisplayName("测试无效CSV文件导入")
    void testInvalidCSVImport() throws IOException {
        // 创建无效格式的CSV文件
        File invalidFile = tempDir.resolve("invalid.csv").toFile();
        try (FileWriter writer = new FileWriter(invalidFile)) {
            writer.write("Invalid,Format,Data\n");
            writer.write("无效类别,abc,2024-03-20\n");
        }

        // 测试无效CSV文件读取
        List<ExpenseRecord> records = CSVHandler.readExpenseRecords(invalidFile.getPath());
        
        // 验证无效记录处理
        assertTrue(records.isEmpty() || records.stream()
                .noneMatch(record -> "无效类别".equals(record.getCategory())));
    }

    @Test
    @Order(3)
    @DisplayName("测试记录验证功能")
    void testRecordValidation() {
        // 创建有效记录
        ExpenseRecord validRecord = new ExpenseRecord(100.0, "餐饮", "午餐", LocalDate.now());
        validRecord.setRecordType("支出");
        
        // 创建无效记录
        ExpenseRecord invalidRecord = new ExpenseRecord(-50.0, "无效类别", "测试", LocalDate.now());
        invalidRecord.setRecordType("支出");

        // 使用反射调用私有方法进行测试
        try {
            java.lang.reflect.Method validateMethod = ManualEntryDialogController.class
                    .getDeclaredMethod("validateRecord", ExpenseRecord.class);
            validateMethod.setAccessible(true);
            
            assertTrue((Boolean) validateMethod.invoke(controller, validRecord));
            assertFalse((Boolean) validateMethod.invoke(controller, invalidRecord));
        } catch (Exception e) {
            fail("验证方法调用失败");
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试导入结果显示")
    void testImportResultDisplay() {
        // 使用反射调用私有方法
        try {
            java.lang.reflect.Method showResultMethod = ManualEntryDialogController.class
                    .getDeclaredMethod("showImportResult", int.class, int.class);
            showResultMethod.setAccessible(true);
            
            // 测试成功导入场景
            showResultMethod.invoke(controller, 5, 0);
            
            // 测试部分失败场景
            showResultMethod.invoke(controller, 3, 2);
            
        } catch (Exception e) {
            fail("显示导入结果方法调用失败");
        }
    }

    @AfterEach
    void tearDown() {
        controller = null;
        mockMainPageController = null;
    }

    private MainPageController mock(Class<MainPageController> aClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}