package com.example.reward_chain.model;

import java.math.BigDecimal;

public class UserCategoryRule {
    private int ruleId;         // DB PK
    private int userId;         // FK -> User(UserID)
    private int categoryId;     // FK -> Category(CategoryID)
    private BigDecimal percent; // 0..100

    public UserCategoryRule() {}

    public UserCategoryRule(int userId, int categoryId, BigDecimal percent) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.percent = percent;
    }

    public int getRuleId() { return ruleId; }
    public void setRuleId(int ruleId) { this.ruleId = ruleId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public BigDecimal getPercent() { return percent; }
    public void setPercent(BigDecimal percent) { this.percent = percent; }

    @Override
    public String toString() {
        return "UserCategoryRule{" +
                "ruleId=" + ruleId +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                ", percent=" + percent +
                '}';
    }
}
