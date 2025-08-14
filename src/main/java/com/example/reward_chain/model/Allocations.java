package com.example.reward_chain.model;

import java.math.BigDecimal;

public class Allocations {
    private int allocationId;
    private int userId;
    private BigDecimal ethPercent;
    private BigDecimal usdcPercent;

    // Empty constructor
    public Allocations() {}

    // Constructor with parameters
    public Allocations(int userId, BigDecimal ethPercent, BigDecimal usdcPercent) {
        this.userId = userId;
        this.ethPercent = ethPercent;
        this.usdcPercent = usdcPercent;
    }

    // Getters and Setters
    public int getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(int allocationId) {
        this.allocationId = allocationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getEthPercent() {
        return ethPercent;
    }

    public void setEthPercent(BigDecimal ethPercent) {
        this.ethPercent = ethPercent;
    }

    public BigDecimal getUsdcPercent() {
        return usdcPercent;
    }

    public void setUsdcPercent(BigDecimal usdcPercent) {
        this.usdcPercent = usdcPercent;
    }

    @Override
    public String toString() {
        return "Allocations{" +
                "allocationId=" + allocationId +
                ", userId=" + userId +
                ", ethPercent=" + ethPercent +
                ", usdcPercent=" + usdcPercent +
                '}';
    }
}