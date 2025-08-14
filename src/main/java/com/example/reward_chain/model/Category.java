package com.example.reward_chain.model;

import java.math.BigDecimal;

public class Category {
    private int categoryId;
    private String categoryName;
    private BigDecimal rewardPercentage;

    public Category() {}

    public Category(String categoryName, BigDecimal rewardPercentage) {
        this.categoryName = categoryName;
        this.rewardPercentage = rewardPercentage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getRewardPercentage() {
        return rewardPercentage;
    }

    public void setRewardPercentage(BigDecimal rewardPercentage) {
        this.rewardPercentage = rewardPercentage;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", rewardPercentage=" + rewardPercentage +
                '}';
    }
}