package com.coinue.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户数据服务类
 * 负责管理每个用户的个人数据存储，包括分析数据、预算数据、交易记录等
 * 每个用户在data/users/{username}/目录下有独立的数据存储空间
 */
public class UserDataService {
    
    // Jackson ObjectMapper用于JSON序列化和反序列化
    private final ObjectMapper objectMapper;
    
    // 基础数据目录
    private static final String BASE_DATA_DIR = "data/users";
    
    // 各种数据文件名
    private static final String ANALYSIS_DATA_FILE = "analysis_data.json";
    private static final String BUDGET_DATA_FILE = "budget_data.json";
    private static final String EXPENSE_DATA_FILE = "expense_data.json";
    private static final String SETTINGS_FILE = "user_settings.json";
    private static final String TRANSACTION_HISTORY_FILE = "transaction_history.json";
    
    // 单例实例
    private static UserDataService instance;
    
    /**
     * 获取单例实例
     * @return UserDataService实例
     */
    public static synchronized UserDataService getInstance() {
        if (instance == null) {
            instance = new UserDataService();
        }
        return instance;
    }
    
    /**
     * 私有构造函数
     */
    private UserDataService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        // 配置忽略未知字段，提高兼容性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 确保用户数据目录存在
     * @param username 用户名
     * @return 用户数据目录路径
     */
    private String ensureUserDataDirectory(String username) {
        String userDir = BASE_DATA_DIR + File.separator + username;
        Path userDirPath = Paths.get(userDir);
        
        if (!Files.exists(userDirPath)) {
            try {
                Files.createDirectories(userDirPath);
                System.out.println("为用户 " + username + " 创建数据目录: " + userDir);
            } catch (IOException e) {
                System.err.println("无法为用户 " + username + " 创建数据目录: " + e.getMessage());
            }
        }
        
        return userDir;
    }
    
    /**
     * 保存用户分析数据
     * @param username 用户名
     * @param analysisData 分析数据对象
     * @return 是否保存成功
     */
    public boolean saveAnalysisData(String username, Object analysisData) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + ANALYSIS_DATA_FILE;
        
        try {
            objectMapper.writeValue(new File(filePath), analysisData);
            System.out.println("成功保存用户 " + username + " 的分析数据");
            return true;
        } catch (IOException e) {
            System.err.println("保存用户 " + username + " 的分析数据失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 加载用户分析数据
     * @param username 用户名
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 分析数据对象，如果不存在或加载失败则返回null
     */
    public <T> T loadAnalysisData(String username, Class<T> dataClass) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + ANALYSIS_DATA_FILE;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("用户 " + username + " 的分析数据文件不存在");
            return null;
        }
        
        try {
            T data = objectMapper.readValue(file, dataClass);
            System.out.println("成功加载用户 " + username + " 的分析数据");
            return data;
        } catch (IOException e) {
            System.err.println("加载用户 " + username + " 的分析数据失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存用户预算数据
     * @param username 用户名
     * @param budgetData 预算数据对象
     * @return 是否保存成功
     */
    public boolean saveBudgetData(String username, Object budgetData) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + BUDGET_DATA_FILE;
        
        try {
            objectMapper.writeValue(new File(filePath), budgetData);
            System.out.println("成功保存用户 " + username + " 的预算数据");
            return true;
        } catch (IOException e) {
            System.err.println("保存用户 " + username + " 的预算数据失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 加载用户预算数据
     * @param username 用户名
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 预算数据对象，如果不存在或加载失败则返回null
     */
    public <T> T loadBudgetData(String username, Class<T> dataClass) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + BUDGET_DATA_FILE;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("用户 " + username + " 的预算数据文件不存在");
            return null;
        }
        
        try {
            T data = objectMapper.readValue(file, dataClass);
            System.out.println("成功加载用户 " + username + " 的预算数据");
            return data;
        } catch (IOException e) {
            System.err.println("加载用户 " + username + " 的预算数据失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存用户支出数据
     * @param username 用户名
     * @param expenseData 支出数据对象
     * @return 是否保存成功
     */
    public boolean saveExpenseData(String username, Object expenseData) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + EXPENSE_DATA_FILE;
        
        try {
            objectMapper.writeValue(new File(filePath), expenseData);
            System.out.println("成功保存用户 " + username + " 的支出数据");
            return true;
        } catch (IOException e) {
            System.err.println("保存用户 " + username + " 的支出数据失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 加载用户支出数据
     * @param username 用户名
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 支出数据对象，如果不存在或加载失败则返回null
     */
    public <T> T loadExpenseData(String username, Class<T> dataClass) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + EXPENSE_DATA_FILE;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("用户 " + username + " 的支出数据文件不存在");
            return null;
        }
        
        try {
            T data = objectMapper.readValue(file, dataClass);
            System.out.println("成功加载用户 " + username + " 的支出数据");
            return data;
        } catch (IOException e) {
            System.err.println("加载用户 " + username + " 的支出数据失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存用户设置
     * @param username 用户名
     * @param settings 用户设置Map
     * @return 是否保存成功
     */
    public boolean saveUserSettings(String username, Map<String, Object> settings) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + SETTINGS_FILE;
        
        try {
            objectMapper.writeValue(new File(filePath), settings);
            System.out.println("成功保存用户 " + username + " 的设置");
            return true;
        } catch (IOException e) {
            System.err.println("保存用户 " + username + " 的设置失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 加载用户设置
     * @param username 用户名
     * @return 用户设置Map，如果不存在则返回空Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadUserSettings(String username) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + SETTINGS_FILE;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("用户 " + username + " 的设置文件不存在，返回默认设置");
            return new HashMap<>();
        }
        
        try {
            Map<String, Object> settings = objectMapper.readValue(file, Map.class);
            System.out.println("成功加载用户 " + username + " 的设置");
            return settings;
        } catch (IOException e) {
            System.err.println("加载用户 " + username + " 的设置失败: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * 保存通用数据
     * @param username 用户名
     * @param fileName 文件名
     * @param data 数据对象
     * @return 是否保存成功
     */
    public boolean saveData(String username, String fileName, Object data) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + fileName;
        
        try {
            objectMapper.writeValue(new File(filePath), data);
            System.out.println("成功保存用户 " + username + " 的数据到文件 " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("保存用户 " + username + " 的数据到文件 " + fileName + " 失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 加载通用数据
     * @param username 用户名
     * @param fileName 文件名
     * @param dataClass 数据类型
     * @param <T> 泛型类型
     * @return 数据对象，如果不存在或加载失败则返回null
     */
    public <T> T loadData(String username, String fileName, Class<T> dataClass) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + fileName;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("用户 " + username + " 的文件 " + fileName + " 不存在");
            return null;
        }
        
        try {
            T data = objectMapper.readValue(file, dataClass);
            System.out.println("成功加载用户 " + username + " 的文件 " + fileName);
            return data;
        } catch (IOException e) {
            System.err.println("加载用户 " + username + " 的文件 " + fileName + " 失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 删除用户数据文件
     * @param username 用户名
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean deleteData(String username, String fileName) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + fileName;
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("用户 " + username + " 的文件 " + fileName + " 不存在，无需删除");
            return true;
        }
        
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("成功删除用户 " + username + " 的文件 " + fileName);
        } else {
            System.err.println("删除用户 " + username + " 的文件 " + fileName + " 失败");
        }
        
        return deleted;
    }
    
    /**
     * 检查用户数据文件是否存在
     * @param username 用户名
     * @param fileName 文件名
     * @return 文件是否存在
     */
    public boolean dataExists(String username, String fileName) {
        String userDir = ensureUserDataDirectory(username);
        String filePath = userDir + File.separator + fileName;
        return new File(filePath).exists();
    }
    
    /**
     * 获取用户数据目录路径
     * @param username 用户名
     * @return 用户数据目录的绝对路径
     */
    public String getUserDataDirectory(String username) {
        return ensureUserDataDirectory(username);
    }
    
    /**
     * 清理用户所有数据
     * @param username 用户名
     * @return 是否清理成功
     */
    public boolean cleanupUserData(String username) {
        String userDir = BASE_DATA_DIR + File.separator + username;
        Path userDirPath = Paths.get(userDir);
        
        if (!Files.exists(userDirPath)) {
            System.out.println("用户 " + username + " 的数据目录不存在，无需清理");
            return true;
        }
        
        try {
            Files.walk(userDirPath)
                .map(Path::toFile)
                .forEach(File::delete);
            Files.deleteIfExists(userDirPath);
            System.out.println("成功清理用户 " + username + " 的所有数据");
            return true;
        } catch (IOException e) {
            System.err.println("清理用户 " + username + " 的数据失败: " + e.getMessage());
            return false;
        }
    }
} 