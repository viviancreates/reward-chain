package com.example.reward_chain.model;

public enum RewardsStatus {
    PENDING,    // Reward calculated but crypto not sent yet
    COMPLETED,  // Crypto successfully sent to wallet
    FAILED      // Something went wrong with crypto transfer
}
