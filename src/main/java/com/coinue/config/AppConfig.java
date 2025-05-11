package com.coinue.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 应用程序配置类
 */
public class AppConfig {
    private static final AppConfig instance = new AppConfig();
    private final Properties properties = new Properties();
    
    // 默认配置
    private static final String DEFAULT_DATA_DIR = "data";
    private static final String DEFAULT_CURRENCY = "CNY";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_LANGUAGE = "zh_CN";
    
    private AppConfig() {
        loadProperties();
    }
    
    /**
     * 获取实例
     * @return 实例
     */
    public static AppConfig getInstance() {
        return instance;
    }
    
    /**
     * 加载配置
     */
    private void loadProperties() {
        try {
            // 首先加载默认配置
            InputStream defaultConfig = getClass().getResourceAsStream("/config/default.properties");
            if (defaultConfig != null) {
                properties.load(defaultConfig);
                defaultConfig.close();
            }
            
            // 然后加载用户配置（如果存在）
            File userConfig = new File("config/app.properties");
            if (userConfig.exists()) {
                try (FileInputStream fis = new FileInputStream(userConfig)) {
                    properties.load(fis);
                }
            }
        } catch (IOException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据目录
     * @return 数据目录
     */
    public String getDataDirectory() {
        return properties.getProperty("app.data.directory", DEFAULT_DATA_DIR);
    }
    
    /**
     * 获取默认货币
     * @return 默认货币
     */
    public String getDefaultCurrency() {
        return properties.getProperty("app.default.currency", DEFAULT_CURRENCY);
    }
    
    /**
     * 获取日期格式
     * @return 日期格式
     */
    public String getDateFormat() {
        return properties.getProperty("app.date.format", DEFAULT_DATE_FORMAT);
    }
    
    /**
     * 获取语言
     * @return 语言
     */
    public String getLanguage() {
        return properties.getProperty("app.language", DEFAULT_LANGUAGE);
    }
    
    /**
     * 获取属性
     * @param key 键
     * @param defaultValue 默认值
     * @return 值
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 设置属性
     * @param key 键
     * @param value 值
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        // TODO: 实现配置持久化
    }
} 