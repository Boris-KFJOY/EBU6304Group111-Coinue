package com.coinue.model;

import java.time.LocalDate;

/**
 * 用户实体类
 * 用于存储用户基本信息，包括用户名、邮箱、密码、安全问题和答案、生日
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
    }

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
}