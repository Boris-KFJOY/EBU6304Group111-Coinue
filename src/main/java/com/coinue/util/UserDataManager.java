package com.coinue.util;

import com.coinue.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户数据管理类
 * 负责用户数据的JSON格式存储和管理
 */
public class UserDataManager {
    // 数据存储目录
    private static final String DATA_DIR = "data";
    // 用户数据文件
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.json";
    // Jackson ObjectMapper用于JSON序列化和反序列化
    private final ObjectMapper objectMapper;
    // 用户数据缓存，用户名作为键
    private Map<String, User> userCache;
    // 用户数据缓存，邮箱作为键
    private Map<String, User> emailCache;

    /**
     * 单例实例
     */
    private static UserDataManager instance;

    /**
     * 获取单例实例
     * @return UserDataManager实例
     */
    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    /**
     * 私有构造函数，初始化ObjectMapper和用户缓存
     */
    private UserDataManager() {
        objectMapper = new ObjectMapper();
        // 注册JavaTimeModule以支持Java 8日期/时间类型
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        userCache = new HashMap<>();
        emailCache = new HashMap<>();
        
        // 确保数据目录存在
        ensureDataDirectoryExists();
        // 加载现有用户数据
        loadUsers();
    }

    /**
     * 确保数据目录存在
     */
    private void ensureDataDirectoryExists() {
        Path dataDir = Paths.get(DATA_DIR);
        if (!Files.exists(dataDir)) {
            try {
                Files.createDirectories(dataDir);
            } catch (IOException e) {
                System.err.println("无法创建数据目录: " + e.getMessage());
            }
        }
    }

    /**
     * 加载用户数据
     */
    private void loadUsers() {
        File usersFile = new File(USERS_FILE);
        if (usersFile.exists()) {
            try {
                User[] users = objectMapper.readValue(usersFile, User[].class);
                for (User user : users) {
                    userCache.put(user.getUsername(), user);
                    if (user.getEmail() != null) {
                        emailCache.put(user.getEmail(), user);
                    }
                }
                System.out.println("成功加载" + users.length + "个用户数据");
            } catch (IOException e) {
                System.err.println("加载用户数据失败: " + e.getMessage());
            }
        }
    }

    /**
     * 保存用户数据
     */
    private void saveUsers() {
        try {
            List<User> users = new ArrayList<>(userCache.values());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
            System.out.println("成功保存" + users.size() + "个用户数据");
        } catch (IOException e) {
            System.err.println("保存用户数据失败: " + e.getMessage());
        }
    }

    /**
     * 创建新用户
     * @param user 用户对象
     * @return 是否成功创建
     */
    public boolean createUser(User user) {
        // 验证用户数据
        if (!validateUserData(user)) {
            return false;
        }
        
        // 检查用户名是否已存在
        if (userCache.containsKey(user.getUsername())) {
            System.out.println("用户名已存在: " + user.getUsername());
            return false;
        }
        
        // 检查邮箱是否已存在
        if (user.getEmail() != null && emailCache.containsKey(user.getEmail())) {
            System.out.println("邮箱已存在: " + user.getEmail());
            return false;
        }
        
        // 添加到缓存
        userCache.put(user.getUsername(), user);
        if (user.getEmail() != null) {
            emailCache.put(user.getEmail(), user);
        }
        
        // 保存数据
        saveUsers();
        return true;
    }

    /**
     * 验证用户数据
     * @param user 用户对象
     * @return 是否有效
     */
    private boolean validateUserData(User user) {
        // 验证用户名
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.out.println("用户名不能为空");
            return false;
        }
        
        // 验证邮箱
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            System.out.println("邮箱格式不正确");
            return false;
        }
        
        // 验证密码
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            System.out.println("密码长度不能少于6位");
            return false;
        }
        
        // 验证安全问题和答案
        if (user.getSecurityQuestion() == null || user.getSecurityQuestion().trim().isEmpty() ||
            user.getSecurityAnswer() == null || user.getSecurityAnswer().trim().isEmpty()) {
            System.out.println("安全问题和答案不能为空");
            return false;
        }
        
        // 验证生日
        if (user.getBirthday() == null) {
            System.out.println("生日不能为空");
            return false;
        }
        
        return true;
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserByUsername(String username) {
        return userCache.get(username);
    }

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserByEmail(String email) {
        return emailCache.get(email);
    }

    /**
     * 验证用户登录
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     * @return 用户对象，如果验证失败则返回null
     */
    public User validateLogin(String usernameOrEmail, String password) {
        User user;
        if (usernameOrEmail.contains("@")) {
            // 使用邮箱登录
            user = getUserByEmail(usernameOrEmail);
        } else {
            // 使用用户名登录
            user = getUserByUsername(usernameOrEmail);
        }
        
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        
        return null;
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否成功更新
     */
    public boolean updateUser(User user) {
        // 验证用户数据
        if (!validateUserData(user)) {
            return false;
        }
        
        // 检查用户是否存在
        if (!userCache.containsKey(user.getUsername())) {
            System.out.println("用户不存在: " + user.getUsername());
            return false;
        }
        
        // 获取旧用户数据
        User oldUser = userCache.get(user.getUsername());
        
        // 如果邮箱发生变化，检查新邮箱是否已存在
        boolean emailChanged = (oldUser.getEmail() == null && user.getEmail() != null) ||
                          (oldUser.getEmail() != null && !oldUser.getEmail().equals(user.getEmail()));
        if (emailChanged && user.getEmail() != null && emailCache.containsKey(user.getEmail())) {
            System.out.println("邮箱已存在: " + user.getEmail());
            return false;
        }
        
        // 从邮箱缓存中移除旧邮箱
        if (oldUser.getEmail() != null) {
            emailCache.remove(oldUser.getEmail());
        }
        
        // 更新缓存
        userCache.put(user.getUsername(), user);
        if (user.getEmail() != null) {
            emailCache.put(user.getEmail(), user);
        }
        
        // 保存数据
        saveUsers();
        return true;
    }

    /**
     * 重置用户密码
     * @param email 邮箱
     * @param securityAnswer 安全问题答案
     * @param newPassword 新密码
     * @return 是否成功重置
     */
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {
        // 查找用户
        User user = getUserByEmail(email);
        if (user == null) {
            System.out.println("用户不存在: " + email);
            return false;
        }
        
        // 验证安全问题答案
        if (!user.getSecurityAnswer().equals(securityAnswer)) {
            System.out.println("安全问题答案不正确");
            return false;
        }
        
        // 验证新密码
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("新密码长度不能少于6位");
            return false;
        }
        
        // 更新密码
        user.setPassword(newPassword);
        
        // 保存数据
        saveUsers();
        return true;
    }
}