package com.coinue.model;

/**
 * 用户实体类
 * 用于存储用户基本信息，包括用户名、邮箱和密码
 */
public class User {
    // 用户名
    private String username;
    // 用户邮箱
    private String email;
    // 用户密码
    private String password;

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
}