package com.coinue.dto;

import java.time.LocalDate;

/**
 * 用户注册数据传输对象
 */
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String securityQuestion;
    private String securityAnswer;
    private LocalDate birthday;
    
    // TODO: 添加表单验证方法
    // TODO: 实现密码匹配验证
    // TODO: 添加数据格式转换方法
    
    public boolean isValid() {
        // TODO: 实现完整的验证逻辑
        return username != null && !username.trim().isEmpty()
                && password != null && !password.trim().isEmpty()
                && email != null && !email.trim().isEmpty()
                && password.equals(confirmPassword);
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
} 