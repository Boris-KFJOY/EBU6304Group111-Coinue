# Analysis页面用户数据关联功能修复调试指南

## 已修复的问题

### 1. ✅ statsCardsContainer未初始化警告
- **问题**: FXML文件中缺少统计卡片容器定义
- **修复**: 在AnalysisPage.fxml中添加了完整的统计卡片区域
- **位置**: 预算进度条下方，图表上方

### 2. ✅ 数据目录结构问题
- **问题**: 缺少`data/users/`目录结构
- **修复**: 创建了`data/users/`目录，UserDataService会自动为每个用户创建子目录

### 3. ✅ 页面跳转数据丢失
- **问题**: 切换页面时没有保存当前数据
- **修复**: 所有导航方法都会先调用`saveCurrentDataIfLoggedIn()`保存数据

### 4. ✅ 用户界面增强
- **新增**: 用户状态显示标签（显示当前登录用户或未登录状态）
- **新增**: 清除数据和刷新数据按钮
- **新增**: 设置预算按钮（直接在页面上）

## 验证修复的测试步骤

### 步骤1: 基础功能验证
```bash
# 1. 启动应用
cd /Users/boriskfjoy/Project/EBU6304Group111-Coinue
mvn javafx:run

# 2. 观察Analysis页面初始状态
# 应该看到：
# - 用户状态: "状态: 未登录" (红色文本)
# - 统计卡片区域为空
# - 文件名显示: "请先登录以保存和加载个人分析数据"
```

### 步骤2: 未登录状态测试
```
1. 直接进入Analysis页面
2. 点击"Import Analysis File"导入test_expense_data.csv
3. 观察结果：
   ✅ 应该显示: "CSV文件已导入，但未保存（请登录以保存数据）"
   ✅ 图表正常显示
   ✅ 统计卡片显示数据
   ✅ 但没有文件保存到data/users/目录
```

### 步骤3: 用户登录测试
```
1. 注册一个新用户: TestUser01
2. 登录成功后进入Analysis页面
3. 观察变化：
   ✅ 用户状态: "当前用户: TestUser01" (绿色文本)
   ✅ 文件名显示: "欢迎 TestUser01，请导入CSV文件开始分析"
   ✅ 清除数据和刷新数据按钮可点击
```

### 步骤4: 数据保存测试
```
1. 导入test_expense_data.csv文件
2. 观察结果：
   ✅ 显示: "CSV文件已导入并保存到您的个人数据中"
   ✅ 检查目录: data/users/TestUser01/ 应该存在
   ✅ 文件存在: data/users/TestUser01/analysis_data.json
   ✅ 备份文件: data/users/TestUser01/imported_csv_files/时间戳_test_expense_data.csv

3. 设置预算（比如5000元）
   ✅ 显示: "预算金额已保存到您的个人数据中"
   ✅ analysis_data.json文件更新
```

### 步骤5: 数据持久化测试
```
1. 切换到User页面
2. 再切换回Analysis页面
3. 验证：
   ✅ 图表数据保持显示
   ✅ 统计卡片数据保持
   ✅ 预算设置保持
   ✅ 文件名显示: "已加载用户历史数据 - TestUser01"

4. 完全退出应用并重新启动
5. 登录TestUser01，进入Analysis页面
6. 验证：
   ✅ 所有数据自动恢复
   ✅ 图表自动显示
```

### 步骤6: 多用户隔离测试
```
1. 注销TestUser01
2. 注册新用户: TestUser02
3. 登录TestUser02进入Analysis页面
4. 验证：
   ✅ 看到全新的界面
   ✅ 没有TestUser01的数据
   ✅ 文件名显示欢迎TestUser02信息

5. 导入不同的CSV文件或数据
6. 切换回TestUser01登录
7. 验证：
   ✅ TestUser01的数据完整保留
   ✅ 两个用户数据完全独立
```

## 检查文件结构

成功测试后，应该看到以下目录结构：

```
data/
├── users.json
├── budget.json
├── expense.json
├── reminder.json
└── users/
    ├── TestUser01/
    │   ├── analysis_data.json
    │   └── imported_csv_files/
    │       └── 20250524_132X_test_expense_data.csv
    └── TestUser02/
        ├── analysis_data.json
        └── imported_csv_files/
            └── 20250524_133X_another_file.csv
```

## 验证JSON文件内容

可以查看保存的JSON文件内容：

```bash
# 查看用户分析数据
cat data/users/TestUser01/analysis_data.json

# 应该包含类似内容：
{
  "lastAnalysisDate" : "2025-05-24",
  "totalExpenses" : 1053.2,
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
      "budgetLimit" : 5000.0,
      "actualSpent" : 1053.2,
      "remainingBudget" : 3946.8,
      "usagePercentage" : 21.064
    }
  },
  "createdDate" : "2025-05-24",
  "updatedDate" : "2025-05-24"
}
```

## 常见问题解决

### 如果statsCardsContainer仍然报警告
```
1. 确认FXML文件已更新
2. 重新编译: mvn clean compile
3. 重启应用
```

### 如果数据没有保存
```
1. 确认用户已登录（检查用户状态标签）
2. 查看控制台输出，寻找错误信息
3. 检查data/users/目录权限
4. 确认UserDataService正常工作
```

### 如果页面跳转后数据消失
```
1. 确认saveCurrentDataIfLoggedIn()方法被调用
2. 检查控制台是否有保存错误
3. 确认initialize()方法正确加载数据
```

## 成功标志

完成所有测试后，以下功能应该正常工作：

- ✅ 无控制台警告或错误
- ✅ 用户状态正确显示
- ✅ 统计卡片正常显示
- ✅ 数据自动保存到用户目录
- ✅ CSV文件自动备份
- ✅ 页面切换数据保持
- ✅ 应用重启数据恢复
- ✅ 多用户数据隔离
- ✅ 清除和刷新功能正常

---

**如果所有测试都通过，说明Analysis页面的用户数据关联功能已经完全修复！** 