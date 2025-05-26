package com.coinue.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import com.coinue.model.ExpenseRecord;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CSVHandlerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
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
        assertEquals("食品", firstRecord.getCategory());
        assertEquals(100.0, firstRecord.getAmount());
        assertEquals(LocalDate.parse("2024-03-20"), firstRecord.getDate());
        assertEquals("午餐", firstRecord.getName());
    }

    @Test
    @Order(2)
    @DisplayName("测试无效CSV文件导入")
    void testInvalidCSVImport() throws IOException {
        // 创建一个在某些行中列数不足的CSV文件
        File partiallyInvalidFile = tempDir.resolve("partially_invalid.csv").toFile();
        try (FileWriter writer = new FileWriter(partiallyInvalidFile)) {
            writer.write("Type,Amount,Date,Additional\n"); // Header
            writer.write("餐饮,100.0,2024-03-20,午餐\n");   // Valid
            writer.write("交通,50.0,2024-03-21\n");       // Invalid - missing one column for ExpenseRecord constructor
            writer.write("购物,200.0,2024-03-22,晚餐\n"); // Valid
        }

        List<ExpenseRecord> records = CSVHandler.readExpenseRecords(partiallyInvalidFile.getPath());

        // CSVHandler的当前循环 `if (values.length >= 4)` 会跳过只有3个值的行。
        assertEquals(2, records.size(), "Should only parse records with at least 4 columns");
        assertTrue(records.stream().anyMatch(r -> r.getName().equals("午餐")));
        assertTrue(records.stream().anyMatch(r -> r.getName().equals("晚餐")));
    }

    @AfterEach
    void tearDown() {
    }
}