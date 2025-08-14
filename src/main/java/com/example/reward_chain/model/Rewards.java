package com.example.reward_chain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Rewards {
    private int rewardId;
    private int transactionId;
    private int userId;
    private String coinType;
    private BigDecimal rewardPercentage;
    private BigDecimal rewardAmountUsd;
    private BigDecimal rewardAmountCrypto;
    private BigDecimal coinPriceUsd;
    private String walletAddress;
    private String transactionHash;
    private RewardsStatus status;
    private LocalDateTime createdDate;

    public Rewards() {}

    public Rewards(int transactionId, int userId, String coinType, BigDecimal rewardPercentage,
                   BigDecimal rewardAmountUsd, BigDecimal rewardAmountCrypto,
                   BigDecimal coinPriceUsd, String walletAddress) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.coinType = coinType;
        this.rewardPercentage = rewardPercentage;
        this.rewardAmountUsd = rewardAmountUsd;
        this.rewardAmountCrypto = rewardAmountCrypto;
        this.coinPriceUsd = coinPriceUsd;
        this.walletAddress = walletAddress;
        this.status = RewardsStatus.PENDING;
        this.createdDate = LocalDateTime.now();
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public BigDecimal getRewardPercentage() {
        return rewardPercentage;
    }

    public void setRewardPercentage(BigDecimal rewardPercentage) {
        this.rewardPercentage = rewardPercentage;
    }

    public BigDecimal getRewardAmountUsd() {
        return rewardAmountUsd;
    }

    public void setRewardAmountUsd(BigDecimal rewardAmountUsd) {
        this.rewardAmountUsd = rewardAmountUsd;
    }

    public BigDecimal getRewardAmountCrypto() {
        return rewardAmountCrypto;
    }

    public void setRewardAmountCrypto(BigDecimal rewardAmountCrypto) {
        this.rewardAmountCrypto = rewardAmountCrypto;
    }

    public BigDecimal getCoinPriceUsd() {
        return coinPriceUsd;
    }

    public void setCoinPriceUsd(BigDecimal coinPriceUsd) {
        this.coinPriceUsd = coinPriceUsd;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public RewardsStatus getStatus() {
        return status;
    }

    public void setStatus(RewardsStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Rewards{" +
                "rewardId=" + rewardId +
                ", transactionId=" + transactionId +
                ", userId=" + userId +
                ", coinType='" + coinType + '\'' +
                ", rewardAmountUsd=" + rewardAmountUsd +
                ", rewardAmountCrypto=" + rewardAmountCrypto +
                ", status='" + status + '\'' +
                '}';
    }
}