package com.example.reward_chain.implementation_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.data.RewardsRepo;
import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.model.RewardsStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RewardsRepoIT extends BaseDbTest {

    @Autowired RewardsRepo rewardsRepo;

    @Test
    @DisplayName("seed: userId=1 has 3 rewards")
    void seeded_rewards_count_user1() throws Exception {
        int expected = 3; // matches 3 transactions for user 1
        int actual = rewardsRepo.getRewardsByUserId(1).size();
        assertEquals(expected, actual, "rewards count for user 1: expected=" + expected + ", actual=" + actual);
    }

    @Test
    @DisplayName("update changes status to COMPLETED (first reward of user 1)")
    void update_changes_status() throws Exception {
        Rewards r = rewardsRepo.getRewardsByUserId(1).get(0);

        RewardsStatus expected = RewardsStatus.COMPLETED;
        r.setStatus(expected);
        rewardsRepo.updateReward(r);

        RewardsStatus actual = rewardsRepo.getRewardById(r.getRewardId()).getStatus();
        assertEquals(expected, actual, "status: expected=" + expected + ", actual=" + actual);
    }
}
