package com.example.reward_chain.model;

import java.math.BigDecimal;

public class UserCategoryAllocation {
    private int allocationId;
    private int userId;
    private int categoryId;
    private BigDecimal percent; // 0.00–1.00

    public UserCategoryAllocation() {}

    public UserCategoryAllocation(int userId, int categoryId, BigDecimal percent) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.percent = percent;
    }

    public int getAllocationId() { return allocationId; }
    public void setAllocationId(int allocationId) { this.allocationId = allocationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public BigDecimal getPercent() { return percent; }
    public void setPercent(BigDecimal percent) { this.percent = percent; }

    @Override
    public String toString() {
        return "UserCategoryAllocation{" +
                "allocationId=" + allocationId +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                ", percent=" + percent +
                '}';
    }
}
