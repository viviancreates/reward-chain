package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.RewardsRepo;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.model.RewardsStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Repository
@Profile("mock")
public class RewardsRepoMock implements RewardsRepo {
    private final Map<Integer, Rewards> rewards = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public RewardsRepoMock() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        Rewards r1 = new Rewards(1, 1, "ETH",
                new BigDecimal("0.03"),
                new BigDecimal("3.00"),
                new BigDecimal("0.00075"),
                new BigDecimal("4000.00"),
                "0x1111");
        r1.setRewardId(idCounter.getAndIncrement());
        r1.setStatus(RewardsStatus.PENDING);
        r1.setCreatedDate(LocalDateTime.now());
        rewards.put(r1.getRewardId(), r1);
    }

    @Override
    public Rewards getRewardById(int id) throws RecordNotFoundException {
        Rewards r = rewards.get(id);
        if (r == null) throw new RecordNotFoundException();
        return r;
    }

    @Override
    public List<Rewards> getRewardsByUserId(int userId) {
        List<Rewards> result = new ArrayList<>();
        for (Rewards r : rewards.values()) {
            if (r.getUserId() == userId) result.add(r);
        }
        return result;
    }

    @Override
    public List<Rewards> getRewardsByTransactionId(int transactionId) {
        List<Rewards> result = new ArrayList<>();
        for (Rewards r : rewards.values()) {
            if (r.getTransactionId() == transactionId) result.add(r);
        }
        return result;
    }

    @Override
    public List<Rewards> getRewardsByStatus(RewardsStatus status) {
        List<Rewards> result = new ArrayList<>();
        for (Rewards r : rewards.values()) {
            if (r.getStatus() == status) result.add(r);
        }
        return result;
    }

    @Override
    public List<Rewards> getAllRewards() { return new ArrayList<>(rewards.values()); }

    @Override
    public Rewards addReward(Rewards reward) {
        reward.setRewardId(idCounter.getAndIncrement());
        rewards.put(reward.getRewardId(), reward);
        return reward;
    }

    @Override
    public void updateReward(Rewards reward) { rewards.put(reward.getRewardId(), reward); }

    @Override
    public Rewards deleteReward(int id) { return rewards.remove(id); }
}
