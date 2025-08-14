package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.model.RewardsStatus;

import java.util.List;

public interface RewardsRepo {

    // Get a reward by its ID
    Rewards getRewardById(int id) throws RecordNotFoundException, InternalErrorException;

    // Get all rewards earned by a specific user (Vivian's reward history)
    List<Rewards> getRewardsByUserId(int userId) throws InternalErrorException;

    // Get all rewards for a specific transaction
    List<Rewards> getRewardsByTransactionId(int transactionId) throws InternalErrorException;

    // Get rewards by status (PENDING, COMPLETED, FAILED)
    List<Rewards> getRewardsByStatus(RewardsStatus status) throws InternalErrorException;

    // Get all rewards in the system
    List<Rewards> getAllRewards() throws InternalErrorException;

    // Add a new reward (when user makes a purchase and earns crypto)
    Rewards addReward(Rewards reward) throws InternalErrorException;

    // Update a reward (change status from PENDING to COMPLETED)
    void updateReward(Rewards reward) throws InternalErrorException;

    // Delete a reward by ID - returns the deleted reward
    Rewards deleteReward(int id) throws InternalErrorException;
}