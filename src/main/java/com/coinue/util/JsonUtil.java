package com.coinue.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * JSON工具类
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    // 私有构造函数，防止实例化
    private JsonUtil() {
    }
    
    /**
     * 将对象转换为JSON字符串
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将对象转换为美化的JSON字符串
     * @param obj 对象
     * @return 美化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将JSON字符串转换为对象
     * @param json JSON字符串
     * @param clazz 对象类
     * @param <T> 对象类型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 验证JSON是否有效
     * @param json JSON字符串
     * @return 是否有效
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
    /**
     * 获取ObjectMapper实例
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
} 