package com.coinue.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 */
public class PasswordUtil {
    
    // 私有构造函数，防止实例化
    private PasswordUtil() {
    }
    
    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String rawPassword) {
        try {
            // 生成盐值
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // 使用SHA-256加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.getBytes());
            
            // 组合盐值和密码，并转为Base64
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String encryptedPassword) {
        try {
            // 解码加密的密码
            byte[] combined = Base64.getDecoder().decode(encryptedPassword);
            
            // 提取盐值和哈希密码
            byte[] salt = new byte[16];
            byte[] hash = new byte[combined.length - salt.length];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, hash, 0, hash.length);
            
            // 使用相同的盐值加密原始密码
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedRawPassword = md.digest(rawPassword.getBytes());
            
            // 比较两个哈希值
            return MessageDigest.isEqual(hash, hashedRawPassword);
        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            // TODO: 实现更好的异常处理
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 生成随机密码
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(randomIndex));
        }
        
        return sb.toString();
    }
} 