# JSON序列化问题修复验证指南

## 修复的问题

### 🐛 原问题
```
加载用户 Test 的分析数据失败: Unrecognized field "savingsRate" 
(class com.coinue.model.UserAnalysisData), not marked as ignorable
```

### ✅ 解决方案

1. **添加@JsonIgnore注解**：
   - `getSavingsRate()` - 储蓄率计算方法
   - `getTopExpenseCategory()` - 最大支出分类计算方法
   
2. **Jackson配置优化**：
   - 添加 `FAIL_ON_UNKNOWN_PROPERTIES = false`
   - 提高JSON反序列化的兼容性

3. **清理旧数据**：
   - 删除包含错误字段的旧JSON文件
   - 让系统重新生成正确格式的文件

## 修复后的代码变化

### UserAnalysisData.java
```java
// 添加JsonIgnore注解，防止序列化计算字段
@JsonIgnore
public double getSavingsRate() {
    if (totalIncome <= 0) {
        return 0.0;
    }
    double savings = totalIncome - totalExpenses;
    return (savings / totalIncome) * 100;
}

@JsonIgnore
public String getTopExpenseCategory() {
    return categoryExpenses.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("无数据");
}
```

### UserDataService.java
```java
private UserDataService() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    // 🆕 配置忽略未知字段，提高兼容性
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
}
```

## 验证步骤

### 步骤1: 测试数据保存
```bash
# 1. 启动应用
mvn javafx:run

# 2. 注册新用户：TestUser03
# 3. 登录后进入Analysis页面
# 4. 导入test_expense_data.csv
# 5. 设置预算：3000元
```

### 步骤2: 验证JSON文件格式
```bash
# 检查生成的JSON文件
cat data/users/TestUser03/analysis_data.json

# 应该看到正确的JSON格式，不包含savingsRate和topExpenseCategory字段：
{
  "lastAnalysisDate" : "2025-05-24",
  "totalExpenses" : 1053.2,
  "totalIncome" : 0.0,
  "categoryExpenses" : {
    "食品" : 73.5,
    "交通" : 48.0,
    "购物" : 427.9,
    "娱乐" : 58.0,
    "健康" : 356.8,
    "教育" : 89.0
  },
  "monthlyTrends" : { },
  "expenseTags" : [ ],
  "budgetUsage" : {
    "总预算" : {
      "budgetLimit" : 3000.0,
      "actualSpent" : 1053.2,
      "remainingBudget" : 1946.8,
      "usagePercentage" : 35.107
    }
  },
  "createdDate" : "2025-05-24",
  "updatedDate" : "2025-05-24"
}
```

### 步骤3: 测试数据加载
```bash
# 1. 切换到User页面
# 2. 再切换回Analysis页面
# 3. 验证数据自动加载成功：
#    ✅ 图表显示正确
#    ✅ 统计卡片显示正确
#    ✅ 预算进度条正确
#    ✅ 没有控制台错误
```

### 步骤4: 测试应用重启
```bash
# 1. 完全关闭应用
# 2. 重新启动：mvn javafx:run
# 3. 登录TestUser03
# 4. 进入Analysis页面
# 5. 验证：
#    ✅ 数据完全恢复
#    ✅ 文件名显示："已加载用户历史数据 - TestUser03"
#    ✅ 控制台无错误信息
```

## 兼容性测试

### 测试旧版本JSON文件兼容性
如果您有旧的JSON文件（包含savingsRate字段），现在的系统应该能够：
- ✅ 忽略未知字段
- ✅ 正确加载已知字段
- ✅ 不报错
- ✅ 重新保存时使用新格式

## 成功标志

修复成功后，应该观察到：

1. **无错误日志**：
   - 控制台无"Unrecognized field"错误
   - 无JSON反序列化异常

2. **正确的数据流**：
   - 数据保存：生成正确格式的JSON
   - 数据加载：成功读取并显示
   - 页面切换：数据保持一致
   - 应用重启：历史数据完整恢复

3. **计算字段正常工作**：
   - 储蓄率计算正确（在UI或toString中可见）
   - 最大支出分类计算正确
   - 但这些字段不会保存到JSON中

## 常见问题

### Q: 如果仍然有加载错误怎么办？
A: 
1. 检查控制台具体错误信息
2. 删除有问题的用户数据目录：`rm -rf data/users/{username}`
3. 重新登录用户，导入数据

### Q: 旧用户的数据怎么办？
A: 
1. 新的配置已支持旧JSON格式兼容
2. 用户下次保存数据时会自动使用新格式
3. 如需立即转换，可删除旧文件重新导入

### Q: 如何确认JSON格式正确？
A: 
- 生成的JSON文件不应包含`savingsRate`和`topExpenseCategory`字段
- 只包含实际存储的数据字段
- 使用`cat data/users/{username}/analysis_data.json`检查

---

**修复完成后，Analysis页面的数据保存和加载功能应该完全正常！** 