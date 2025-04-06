package com.coinue.model;

/**
 * 消费类别实体类
 * 用于管理消费类别及其相关预算信息
 */
public class Category {
    private String name;          // 类别名称
    private double budget;        // 类别预算
    private String description;   // 类别描述

    public Category() {}

    public Category(String name, double budget) {
        this.name = name;
        this.budget = budget;
    }

    public Category(String name, double budget, String description) {
        this.name = name;
        this.budget = budget;
        this.description = description;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("Category{name='%s', budget=%.2f, description='%s'}",
                name, budget, description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return name != null && name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}