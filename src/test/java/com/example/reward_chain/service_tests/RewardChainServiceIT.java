package com.example.reward_chain.service_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.model.*;
import com.example.reward_chain.service.RewardChainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RewardChainServiceIT extends BaseDbTest {

    @Autowired
    RewardChainService service;

    @Test
    @DisplayName("register: creates user + wallet + allocations")
    void register_user_wallet_allocations() throws Exception {
        User input = new User("Test","User","test.user@example.com","pw");
        BigDecimal eth = new BigDecimal("0.70");
        BigDecimal usdc = new BigDecimal("0.30");

        User saved = service.registerUserWithWalletAndAllocation(
                input, "0xTEST_WALLET", "ETH_MAINNET", eth, usdc);

        int expectedIdGreaterThan = 0;
        int actualId = saved.getUserId();
        assertTrue(actualId > expectedIdGreaterThan,
                "userId: expected> " + expectedIdGreaterThan + ", actual=" + actualId);
    }

    @Test
    @DisplayName("record + reward: $100 Groceries (3%) ⇒ $3.0000 and 0.00075000 ETH")
    void record_and_create_reward() throws Exception {
        // CategoryID=1 (Groceries) in your seed uses 0.03
        Transaction tx = service.recordTransaction(1, 1, "Test Merchant", new BigDecimal("100.00"));
        Rewards r = service.createPendingRewardForTransaction(tx.getTransactionId(), "ETH");

        BigDecimal expectedUsd = new BigDecimal("3.0000");
        BigDecimal actualUsd = r.getRewardAmountUsd();
        assertEquals(0, expectedUsd.compareTo(actualUsd),
                "rewardAmountUsd: expected=" + expectedUsd + ", actual=" + actualUsd);

        BigDecimal expectedCrypto = new BigDecimal("0.00075000"); // 3 / 4000
        BigDecimal actualCrypto = r.getRewardAmountCrypto();
        assertEquals(0, expectedCrypto.compareTo(actualCrypto),
                "rewardAmountCrypto: expected=" + expectedCrypto + ", actual=" + actualCrypto);

        RewardsStatus expectedStatus = RewardsStatus.PENDING;
        RewardsStatus actualStatus = r.getStatus();
        assertEquals(expectedStatus, actualStatus,
                "status: expected=" + expectedStatus + ", actual=" + actualStatus);
    }

    @Test
    @DisplayName("completeReward: sets COMPLETED and adds txHash")
    void complete_reward() throws Exception {
        Transaction tx = service.recordTransaction(1, 1, "Another Merchant", new BigDecimal("50.00"));
        Rewards r = service.createPendingRewardForTransaction(tx.getTransactionId(), "ETH");

        Rewards updated = service.completeReward(r.getRewardId());

        RewardsStatus expected = RewardsStatus.COMPLETED;
        RewardsStatus actual = updated.getStatus();
        assertEquals(expected, actual, "status: expected=" + expected + ", actual=" + actual);

        String hash = updated.getTransactionHash();
        assertNotNull(hash, "txHash: expected non-null, actual=null");
        assertFalse(hash.isBlank(), "txHash: expected non-blank, actual=blank");
    }
}
