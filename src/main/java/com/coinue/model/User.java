package com.coinue.model;

import java.time.LocalDate;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户实体类
 * 用于存储用户基本信息，包括用户名、邮箱、密码、安全问题和答案、生日
 * 同时包含用户相关的业务逻辑方法
 */
public class User {
    // 用户名（唯一标识）
    private String username;
    // 用户邮箱（用于找回密码）
    private String email;
    // 用户密码
    private String password;
    // 安全问题
    private String securityQuestion;
    // 安全问题答案
    private String securityAnswer;
    // 生日
    private LocalDate birthday;
    
    // 用户数据文件路径（用于个人数据存储）
    @JsonIgnore
    private String userDataPath;
    
    // 当前登录的用户会话
    @JsonIgnore
    private static User currentUser;

    // 邮箱格式验证正则表达式
    @JsonIgnore
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * 无参构造函数
     */
    public User() {
    }

    /**
     * 带参构造函数
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userDataPath = "data/users/" + username;
    }

    /**
     * 完整参数构造函数
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param securityQuestion 安全问题
     * @param securityAnswer 安全问题答案
     * @param birthday 生日
     */
    public User(String username, String email, String password, String securityQuestion, String securityAnswer, LocalDate birthday) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.birthday = birthday;
        this.userDataPath = "data/users/" + username;
    }

    // ============================== 业务逻辑方法 ==============================

    /**
     * 验证用户注册数据
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param securityQuestion 安全问题
     * @param securityAnswer 安全问题答案
     * @param birthday 生日
     * @return 验证结果信息，null表示验证通过
     */
    public static String validateRegistrationData(String username, String email, String password, 
                                                String confirmPassword, String securityQuestion, 
                                                String securityAnswer, LocalDate birthday) {
        // 验证必填字段
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (email == null || email.trim().isEmpty()) {
            return "邮箱不能为空";
        }
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return "确认密码不能为空";
        }
        if (securityQuestion == null || securityQuestion.trim().isEmpty()) {
            return "安全问题不能为空";
        }
        if (securityAnswer == null || securityAnswer.trim().isEmpty()) {
            return "安全问题答案不能为空";
        }
        if (birthday == null) {
            return "生日不能为空";
        }

        // 验证用户名格式
        if (username.length() < 3 || username.length() > 20) {
            return "用户名长度必须在3-20个字符之间";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "用户名只能包含字母、数字和下划线";
        }

        // 验证邮箱格式
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "邮箱格式不正确";
        }

        // 验证密码强度
        String passwordValidation = validatePasswordStrength(password);
        if (passwordValidation != null) {
            return passwordValidation;
        }

        // 验证密码匹配
        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不匹配";
        }

        // 验证年龄（必须年满13岁）
        if (birthday.isAfter(LocalDate.now().minusYears(13))) {
            return "用户年龄必须满13岁";
        }

        return null; // 验证通过
    }

    /**
     * 验证登录数据
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     * @return 验证结果信息，null表示验证通过
     */
    public static String validateLoginData(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return "用户名/邮箱不能为空";
        }
        if (password == null || password.trim().isEmpty()) {
            return "密码不能为空";
        }
        return null; // 验证通过
    }

    /**
     * 验证密码重置数据
     * @param usernameOrEmail 用户名或邮箱
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     * @param securityAnswer 安全问题答案
     * @param birthday 生日
     * @return 验证结果信息，null表示验证通过
     */
    public static String validatePasswordResetData(String usernameOrEmail, String newPassword, 
                                                 String confirmPassword, String securityAnswer, 
                                                 LocalDate birthday) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return "用户名/邮箱不能为空";
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return "新密码不能为空";
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return "确认密码不能为空";
        }
        if (securityAnswer == null || securityAnswer.trim().isEmpty()) {
            return "安全问题答案不能为空";
        }
        if (birthday == null) {
            return "生日不能为空";
        }

        // 验证密码强度
        String passwordValidation = validatePasswordStrength(newPassword);
        if (passwordValidation != null) {
            return passwordValidation;
        }

        // 验证密码匹配
        if (!newPassword.equals(confirmPassword)) {
            return "两次输入的密码不匹配";
        }

        return null; // 验证通过
    }

    /**
     * 验证密码强度
     * @param password 密码
     * @return 验证结果信息，null表示验证通过
     */
    public static String validatePasswordStrength(String password) {
        if (password.length() < 6) {
            return "密码长度不能少于6位";
        }
        if (password.length() > 50) {
            return "密码长度不能超过50位";
        }
        
        // 检查是否包含至少一个字母和一个数字
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (!hasLetter || !hasDigit) {
            return "密码必须包含至少一个字母和一个数字";
        }
        
        return null; // 验证通过
    }

    /**
     * 验证身份信息（用于密码重置）
     * @param birthday 输入的生日
     * @param securityAnswer 输入的安全问题答案
     * @return 是否验证通过
     */
    public boolean validateIdentity(LocalDate birthday, String securityAnswer) {
        return this.birthday != null && this.birthday.equals(birthday) 
               && this.securityAnswer != null && this.securityAnswer.equals(securityAnswer);
    }

    /**
     * 重置密码
     * @param newPassword 新密码
     * @return 是否成功重置
     */
    public boolean resetPassword(String newPassword) {
        String validation = validatePasswordStrength(newPassword);
        if (validation != null) {
            return false;
        }
        this.password = newPassword;
        return save(); // 保存到数据库
    }

    // ============================== 数据管理方法 ==============================

    /**
     * 保存用户数据
     * @return 是否保存成功
     */
    public boolean save() {
        return com.coinue.util.UserDataManager.getInstance().updateUser(this);
    }

    /**
     * 创建新用户（注册）
     * @return 是否创建成功
     */
    public boolean register() {
        return com.coinue.util.UserDataManager.getInstance().createUser(this);
    }

    /**
     * 用户登录
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     * @return 登录的用户对象，失败返回null
     */
    public static User login(String usernameOrEmail, String password) {
        // 验证输入
        String validation = validateLoginData(usernameOrEmail, password);
        if (validation != null) {
            return null;
        }

        // 验证用户身份
        User user = com.coinue.util.UserDataManager.getInstance().validateLogin(usernameOrEmail, password);
        if (user != null) {
            currentUser = user;
            user.userDataPath = "data/users/" + user.username;
        }
        return user;
    }

    /**
     * 根据用户名或邮箱查找用户
     * @param usernameOrEmail 用户名或邮箱
     * @return 用户对象，未找到返回null
     */
    public static User findByUsernameOrEmail(String usernameOrEmail) {
        return com.coinue.util.UserDataManager.getInstance().findUserByUsernameOrEmail(usernameOrEmail);
    }

    /**
     * 用户注销
     */
    public static void logout() {
        currentUser = null;
    }

    /**
     * 获取当前登录用户
     * @return 当前登录的用户对象
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * 设置当前登录用户
     * @param user 用户对象
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user != null) {
            user.userDataPath = "data/users/" + user.username;
        }
    }

    /**
     * 检查是否有用户登录
     * @return 是否有用户登录
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * 获取用户数据目录路径
     * @return 用户数据目录路径
     */
    @JsonIgnore
    public String getUserDataPath() {
        if (userDataPath == null && username != null) {
            userDataPath = "data/users/" + username;
        }
        return userDataPath;
    }

    /**
     * 获取用户数据服务实例
     * @return UserDataService实例
     */
    @JsonIgnore
    public UserDataService getDataService() {
        return UserDataService.getInstance();
    }

    /**
     * 保存用户分析数据
     * @param analysisData 分析数据对象
     * @return 是否保存成功
     */
    public boolean saveAnalysisData(Object analysisData) {
        if (username == null) {
            return false;
        }
        return getDataService().saveAnalysisData(username, analysisData);
    }

    /**
     * 加载用户分析数据
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 分析数据对象
     */
    public <T> T loadAnalysisData(Class<T> dataClass) {
        if (username == null) {
            return null;
        }
        return getDataService().loadAnalysisData(username, dataClass);
    }

    /**
     * 保存用户预算数据
     * @param budgetData 预算数据对象
     * @return 是否保存成功
     */
    public boolean saveBudgetData(Object budgetData) {
        if (username == null) {
            return false;
        }
        return getDataService().saveBudgetData(username, budgetData);
    }

    /**
     * 加载用户预算数据
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 预算数据对象
     */
    public <T> T loadBudgetData(Class<T> dataClass) {
        if (username == null) {
            return null;
        }
        return getDataService().loadBudgetData(username, dataClass);
    }

    /**
     * 保存用户支出数据
     * @param expenseData 支出数据对象
     * @return 是否保存成功
     */
    public boolean saveExpenseData(Object expenseData) {
        if (username == null) {
            return false;
        }
        return getDataService().saveExpenseData(username, expenseData);
    }

    /**
     * 加载用户支出数据
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 支出数据对象
     */
    public <T> T loadExpenseData(Class<T> dataClass) {
        if (username == null) {
            return null;
        }
        return getDataService().loadExpenseData(username, dataClass);
    }

    /**
     * 保存用户自定义数据
     * @param fileName 文件名
     * @param data 数据对象
     * @return 是否保存成功
     */
    public boolean saveCustomData(String fileName, Object data) {
        if (username == null) {
            return false;
        }
        return getDataService().saveData(username, fileName, data);
    }

    /**
     * 加载用户自定义数据
     * @param fileName 文件名
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 数据对象
     */
    public <T> T loadCustomData(String fileName, Class<T> dataClass) {
        if (username == null) {
            return null;
        }
        return getDataService().loadData(username, fileName, dataClass);
    }

    // ============================== Getter和Setter方法 ==============================

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
        this.userDataPath = "data/users/" + username;
    }

    /**
     * 获取邮箱
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取安全问题
     * @return 安全问题
     */
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    /**
     * 设置安全问题
     * @param securityQuestion 安全问题
     */
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    /**
     * 获取安全问题答案
     * @return 安全问题答案
     */
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    /**
     * 设置安全问题答案
     * @param securityAnswer 安全问题答案
     */
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    /**
     * 获取生日
     * @return 生日
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     * @param birthday 生日
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    // ============================== 工具方法 ==============================

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username != null ? username.equals(user.username) : user.username == null;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}