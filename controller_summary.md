# Coinue 应用Controller分析

本文档总结了Coinue应用中各Controller的FXML文件对应关系、交互逻辑和业务逻辑。

## 1. AnalysisPageController

**对应FXML文件**: `/view/AnalysisPage.fxml`

**可从以下页面跳转到此页面**:
- MainPageController (`/view/MainPage.fxml`) - handleAnalysisNav()
- UserPageController (`/view/UserPage.fxml`) - handleAnalysisNav()
- BillPaymentPageController (`/view/BillPaymentPage.fxml`) - handleAnalysisNav()
- SharingPageController (`/view/SharingPage.fxml`) - handleAnalysisNav()
- SyncPageController (`/view/SyncPage.fxml`) - handleAnalysisNav()
- EncryptionPageController (`/view/EncryptionPage.fxml`) - handleAnalysisNav()

**交互逻辑**:
- 导航功能：可跳转至主页(`/view/MainPage.fxml`)和用户页面(`/view/UserPage.fxml`)
- 导入分析文件：可选择CSV文件进行消费分析
- 设置预算：允许用户设置月度预算金额
- 导出PDF：可将分析图表导出为PDF文件

**业务逻辑**:
- 读取CSV文件中的消费数据
- 生成消费类别饼图
- 生成消费金额条形图
- 计算并显示预算使用情况
- 创建统计卡片展示消费摘要

## 2. MainPageController

**对应FXML文件**: `/view/MainPage.fxml`

**可从以下页面跳转到此页面**:
- RegisterController (`/view/Register.fxml`) - handleSignIn()
- AnalysisPageController (`/view/AnalysisPage.fxml`) - handleHomeNav()
- UserPageController (`/view/UserPage.fxml`) - handleHomeNav()
- BillPaymentPageController (`/view/BillPaymentPage.fxml`) - handleHomeNav()
- SharingPageController (`/view/SharingPage.fxml`) - handleHomeNav()
- SyncPageController (`/view/SyncPage.fxml`) - handleHomeNav()
- EncryptionPageController (`/view/EncryptionPage.fxml`) - handleHomeNav()

**交互逻辑**:
- 导航功能：可跳转至分析页面(`/view/AnalysisPage.fxml`)和用户页面(`/view/UserPage.fxml`)
- 点击金币图标：打开消费记录对话框(`/view/ManualEntryDialog.fxml`)
- 添加预算按钮：打开预算设置对话框(`/view/BudgetDialog.fxml`)
- 添加还款提醒按钮：打开还款提醒设置对话框(`/view/ReminderDialog.fxml`)
- 消费记录表格：显示所有消费明细，支持删除操作

**业务逻辑**:
- 初始化并显示预算卡片
- 初始化并显示还款提醒卡片
- 管理消费记录数据（增删改查）
- 计算预算使用百分比并显示相应颜色的进度条
- 维护预算列表和还款提醒列表

## 3. RegisterController

**对应FXML文件**: `/view/Register.fxml`

**可从以下页面跳转到此页面**:
- SignUpController (`/view/SignUp.fxml`) - handleBackToSignIn()
- UserPageController (`/view/UserPage.fxml`) - handleLogout()
- ForgetController (`/view/Forget.fxml`) - handleBackToLogin()

**交互逻辑**:
- 登录功能：验证用户凭据并跳转至主页(`/view/MainPage.fxml`)
- 注册链接：通过动画切换到注册页面(`/view/SignUp.fxml`)
- 忘记密码链接：通过动画切换到找回密码页面(`/view/Forget.fxml`)

**业务逻辑**:
- 验证用户名/邮箱和密码
- 调用UserDataManager验证登录信息
- 管理页面之间的平滑过渡动画

## 4. SignUpController

**对应FXML文件**: `/view/SignUp.fxml`

**可从以下页面跳转到此页面**:
- RegisterController (`/view/Register.fxml`) - handleSignUp()

**交互逻辑**:
- 创建账户按钮：验证输入并创建新用户
- 返回登录链接：通过动画切换回登录页面(`/view/Register.fxml`)
- 服务条款、隐私政策、行为准则链接：显示相应文档

**业务逻辑**:
- 验证用户输入的所有字段（用户名、密码、邮箱等）
- 创建用户对象并保存
- 管理页面之间的平滑过渡动画
- 显示文档内容（服务条款、隐私政策等）

## 5. UserPageController

**对应FXML文件**: `/view/UserPage.fxml`

**可从以下页面跳转到此页面**:
- MainPageController (`/view/MainPage.fxml`) - handleUserNav()
- AnalysisPageController (`/view/AnalysisPage.fxml`) - handleUserNav()
- BillPaymentPageController (`/view/BillPaymentPage.fxml`) - handleUserNav()
- SharingPageController (`/view/SharingPage.fxml`) - handleUserNav()
- SyncPageController (`/view/SyncPage.fxml`) - handleUserNav()
- EncryptionPageController (`/view/EncryptionPage.fxml`) - handleUserNav()

**交互逻辑**:
- 导航功能：可跳转至主页(`/view/MainPage.fxml`)和分析页面(`/view/AnalysisPage.fxml`)
- 还款账单按钮：跳转至还款账单页面(`/view/BillPaymentPage.fxml`)
- 退出登录按钮：返回登录页面(`/view/Register.fxml`)
- 同步导航：跳转至同步页面(`/view/SyncPage.fxml`)
- 分享导航：跳转至分享页面(`/view/SharingPage.fxml`)
- 加密功能：跳转至加密页面(`/view/EncryptionPage.fxml`)
- 更换头像功能：允许用户选择新头像图片

**业务逻辑**:
- 显示用户基本信息（用户名、邮箱）
- 提供用户相关功能入口
- 管理用户头像更换

## 6. ManualEntryDialogController

**对应FXML文件**: `/view/ManualEntryDialog.fxml`

**可从以下页面跳转到此页面**:
- MainPageController (`/view/MainPage.fxml`) - handleCoinClick() 和 handleManualEntry()

**交互逻辑**:
- 保存按钮：验证并保存消费记录
- 取消按钮：关闭对话框
- 导入按钮：打开文件选择器导入CSV文件
- 收支类型单选按钮：切换收入/支出模式

**业务逻辑**:
- 根据收支类型更新类别下拉框选项
- 根据选中类别更新子类别列表
- 验证输入数据的有效性
- 创建ExpenseRecord对象并保存
- 导入CSV文件并批量添加消费记录

## 7. AnalysisController

**对应FXML文件**: `/view/Analysis.fxml`

**可从以下页面跳转到此页面**:
- 目前暂无明确的跳转来源页面

**交互逻辑**:
- 图表生成与展示
- 数据导入与分析

**业务逻辑**:
- 分析消费数据并生成可视化图表
- 提供数据分析报告

## 8. SharingPageController

**对应FXML文件**: `/view/SharingPage.fxml`

**可从以下页面跳转到此页面**:
- UserPageController (`/view/UserPage.fxml`) - handleSharingNav()

**交互逻辑**:
- 导航功能：可跳转至主页(`/view/MainPage.fxml`)、分析页面(`/view/AnalysisPage.fxml`)、用户页面(`/view/UserPage.fxml`)、同步页面(`/view/SyncPage.fxml`)、加密页面(`/view/EncryptionPage.fxml`)
- 分享功能操作界面
- 更换头像功能

**业务逻辑**:
- 数据分享功能
- 导出与共享消费数据报告

## 9. SyncPageController

**对应FXML文件**: `/view/SyncPage.fxml`

**可从以下页面跳转到此页面**:
- UserPageController (`/view/UserPage.fxml`) - handleSyncNav()
- SharingPageController (`/view/SharingPage.fxml`) - handleSyncNav()

**交互逻辑**:
- 导航功能：可跳转至其他主要页面
- 数据同步操作界面

**业务逻辑**:
- 数据同步与备份功能
- 云端数据同步管理

## 10. BillPaymentPageController

**对应FXML文件**: `/view/BillPaymentPage.fxml`

**可从以下页面跳转到此页面**:
- UserPageController (`/view/UserPage.fxml`) - handleBillPayment()

**交互逻辑**:
- 导航功能：可跳转至主页(`/view/MainPage.fxml`)、分析页面(`/view/AnalysisPage.fxml`)、用户页面(`/view/UserPage.fxml`)
- 账单支付与管理界面
- CSV文件导入功能
- 日期筛选功能

**业务逻辑**:
- 账单管理与支付提醒功能
- 处理账单数据的增删改查
- 信用额度管理和使用率计算
- 生成账单使用情况饼图

## 11. EncryptionPageController

**对应FXML文件**: `/view/EncryptionPage.fxml`

**可从以下页面跳转到此页面**:
- UserPageController (`/view/UserPage.fxml`) - handleEncryptionNav()
- SharingPageController (`/view/SharingPage.fxml`) - handleEncryptionNav()

**交互逻辑**:
- 导航功能：可跳转至其他主要页面
- 加密功能操作界面

**业务逻辑**:
- 数据加密与安全管理
- 个人财务信息保护

## 12. ReminderDialogController

**对应FXML文件**: `/view/ReminderDialog.fxml`

**可从以下页面跳转到此页面**:
- MainPageController (`/view/MainPage.fxml`) - handleAddReminder()

**交互逻辑**:
- 保存按钮：验证并保存还款提醒
- 取消按钮：关闭对话框

**业务逻辑**:
- 创建PaymentReminder对象并保存
- 验证还款提醒数据的有效性

## 13. BudgetDialogController

**对应FXML文件**: `/view/BudgetDialog.fxml`

**可从以下页面跳转到此页面**:
- MainPageController (`/view/MainPage.fxml`) - handleAddBudget()

**交互逻辑**:
- 保存按钮：验证并保存预算设置
- 取消按钮：关闭对话框

**业务逻辑**:
- 创建Budget对象并保存
- 验证预算数据的有效性

## 14. HomepageController

**对应FXML文件**: `/view/Homepage.fxml`

**可从以下页面跳转到此页面**:
- 目前暂无明确的跳转来源页面

**交互逻辑**:
- 主页面导航与操作
- 主要功能入口

**业务逻辑**:
- 提供应用主要功能的入口
- 展示用户总体财务概况

## 15. ForgetController

**对应FXML文件**: `/view/Forget.fxml`

**可从以下页面跳转到此页面**:
- RegisterController (`/view/Register.fxml`) - handleForgotPassword()

**交互逻辑**:
- 找回密码表单操作
- 返回登录页面(`/view/Register.fxml`)

**业务逻辑**:
- 验证用户身份
- 处理密码重置流程

## 16. BarChartController

**对应FXML文件**: `/view/BarChart.fxml`

**可从以下页面跳转到此页面**:
- 目前暂无明确的跳转来源页面

**交互逻辑**:
- 条形图显示与交互

**业务逻辑**:
- 生成消费数据条形图
- 提供图表交互功能

## 17. ExpenseRecordPageController

**对应FXML文件**: `/view/ExpenseRecordPage.fxml`

**可从以下页面跳转到此页面**:
- 目前暂无明确的跳转来源页面

**交互逻辑**:
- 消费记录表格展示与操作
- 添加新记录功能

**业务逻辑**:
- 管理消费记录数据
- 提供消费记录的筛选与排序功能 