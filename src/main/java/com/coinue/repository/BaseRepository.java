package com.coinue.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础仓库类
 * @param <T> 实体类型
 */
public abstract class BaseRepository<T> {
    protected final String filePath;
    protected final ObjectMapper objectMapper;
    
    public BaseRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 确保数据目录存在
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }
    
    /**
     * 读取所有数据
     * @return 实体列表
     */
    protected List<T> readAll() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, getEntityClass()));
        } catch (IOException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * 写入所有数据
     * @param entities 实体列表
     */
    protected void writeAll(List<T> entities) {
        try {
            File file = new File(filePath);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, entities);
        } catch (IOException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
        }
    }
    
    /**
     * 获取实体类
     * @return 实体类
     */
    protected abstract Class<T> getEntityClass();
} 