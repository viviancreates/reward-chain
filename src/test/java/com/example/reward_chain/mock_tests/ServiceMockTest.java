package com.example.reward_chain.mock_tests;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.TransactionRepo;
import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.model.*;
import com.example.reward_chain.service.RewardChainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ServiceMockTest {

    @Autowired RewardChainService service;
    @Autowired UserRepo users;
    @Autowired CategoryRepo categories;
    @Autowired TransactionRepo txs;

    private Predicate<User> named(String first) {
        return u -> u.getFirstName() != null && u.getFirstName().equalsIgnoreCase(first);
    }

    @Test
    @DisplayName("Seed includes users named Vivian, Alice, and Bob")
    void seed_contains_vivian_alice_bob() throws Exception {
        List<User> all = service.getAllUsers();
        assertThat(all).as("Users list should not be empty").isNotEmpty();

        boolean hasVivian = all.stream().anyMatch(named("Vivian"));
        boolean hasAlice  = all.stream().anyMatch(named("Alice"));
        boolean hasBob    = all.stream().anyMatch(named("Bob"));

        assertThat(Map.of("Vivian", hasVivian, "Alice", hasAlice, "Bob", hasBob))
                .as("Expected seeded names to be present (Vivian, Alice, Bob)")
                .allSatisfy((name, present) ->
                        assertThat(present).as("Expected %s to be present in seed", name).isTrue());
    }

    // ---------- READ HELPERS ----------

    @Test
    @DisplayName("getAllUsers(): returns non-empty list")
    void getAllUsers_returnsNonEmpty() throws Exception {
        List<User> all = service.getAllUsers();

        int expectedMin = 1;
        int actual = all.size();
        assertThat(actual)
                .as("Expected at least %d user(s), but got %d", expectedMin, actual)
                .isGreaterThanOrEqualTo(expectedMin);
    }

    @Test
    @DisplayName("getAllCategories(): returns non-empty list")
    void getAllCategories_returnsNonEmpty() throws Exception {
        List<Category> all = service.getAllCategories();

        int expectedMin = 1;
        int actual = all.size();
        assertThat(actual)
                .as("Expected at least %d category, but got %d", expectedMin, actual)
                .isGreaterThanOrEqualTo(expectedMin);
    }

    @Test
    @DisplayName("getUserTransactions(userId): seeded users have ≥ 5 transactions")
    void getUserTransactions_seededUsersHaveFivePlus() throws Exception {
        for (User u : service.getAllUsers()) {
            int expectedMin = 5;
            int actual = service.getUserTransactions(u.getUserId()).size();
            assertThat(actual)
                    .as("Expected user %d to have >= %d tx, but got %d", u.getUserId(), expectedMin, actual)
                    .isGreaterThanOrEqualTo(expectedMin);
        }
    }

    @Test
    @DisplayName("getUserRewards(userId): returns list (may be empty until we create rewards)")
    void getUserRewards_returnsListPossiblyEmpty() throws Exception {
        User any = service.getAllUsers().get(0);
        List<Rewards> list = service.getUserRewards(any.getUserId());

        assertThat(list)
                .as("Expected non-null list for user %d", any.getUserId())
                .isNotNull();
    }

    // ---------- BUSINESS FLOWS ----------

    @Test
    @DisplayName("registerUserWithWalletAndAllocation(): happy path 0.70 + 0.30")
    void registerUser_happyPath() throws Exception {
        User u = new User("VivTest","User","viv.test@example.com","pw");
        User saved = service.registerUserWithWalletAndAllocation(
                u, "0xTESTWALLET", "ETH_MAINNET",
                new BigDecimal("0.70"), new BigDecimal("0.30"));

        int expectedMinId = 1;
        int actualId = saved.getUserId();
        assertThat(actualId)
                .as("Expected userId >= %d, but got %d", expectedMinId, actualId)
                .isGreaterThanOrEqualTo(expectedMinId);
    }

    @Test
    @DisplayName("registerUserWithWalletAndAllocation(): throws when percentages != 1.00")
    void registerUser_badAllocations_throws() {
        User u = new User("Bad","Alloc","bad.alloc@example.com","pw");

        assertThatThrownBy(() ->
                service.registerUserWithWalletAndAllocation(
                        u, "0xBAD", "ETH_MAINNET",
                        new BigDecimal("0.60"), new BigDecimal("0.30")    // sums to 0.90
                ))
                .as("Expected InternalErrorException due to bad allocation sum")
                .isInstanceOf(Exception.class) // InternalErrorException is checked; service wraps/throws it
                .hasMessageContaining("Allocations must sum to 1.00");
    }

    @Test
    @DisplayName("recordTransaction(): happy path for existing user + category")
    void recordTransaction_happyPath() throws Exception {
        User u = service.getAllUsers().get(0);
        int categoryId = categories.getAllCategories().get(0).getCategoryId();

        Transaction tx = service.recordTransaction(u.getUserId(), categoryId, "UnitTest Merchant", new BigDecimal("42.50"));

        int expectedMinId = 1;
        int actualId = tx.getTransactionId();
        assertThat(actualId)
                .as("Expected tx id >= %d, but got %d", expectedMinId, actualId)
                .isGreaterThanOrEqualTo(expectedMinId);
    }

    @Test
    @DisplayName("recordTransaction(): throws for missing user or category")
    void recordTransaction_invalidRefs_throws() {
        int badUserId = -999;
        int badCategoryId = -888;

        assertThatThrownBy(() ->
                service.recordTransaction(badUserId, badCategoryId, "Nowhere", new BigDecimal("10.00")))
                .as("Expected RecordNotFoundException for invalid refs")
                .isInstanceOf(Exception.class)
                .hasMessageContaining("not found"); // message from your repos’ exceptions
    }

    @Test
    @DisplayName("createPendingRewardForTransaction(): computes USD/crypto and persists PENDING")
    void createPendingRewardForTransaction_happyPath() throws Exception {
        // Arrange: create a new tx via service so we know it exists
        User u = service.getAllUsers().get(0);
        int categoryId = categories.getAllCategories().get(0).getCategoryId();
        Transaction tx = service.recordTransaction(u.getUserId(), categoryId, "Reward-Merchant", new BigDecimal("100.00"));

        // Act
        Rewards r = service.createPendingRewardForTransaction(tx.getTransactionId(), "ETH");

        // Assert
        assertThat(r.getRewardId()).as("reward id should be assigned").isPositive();
        assertThat(r.getStatus()).as("status should be PENDING").isEqualTo(RewardsStatus.PENDING);
        assertThat(r.getRewardAmountUsd()).as("USD reward should be > 0").isGreaterThan(BigDecimal.ZERO);
        assertThat(r.getCoinPriceUsd()).as("Coin price set").isNotNull();
        assertThat(r.getWalletAddress()).as("Wallet address should be set").isNotBlank();
    }

    @Test
    @DisplayName("completeReward(): flips status to COMPLETED and sets tx hash")
    void completeReward_happyPath() throws Exception {
        // Make a reward first
        User u = service.getAllUsers().get(0);
        int categoryId = categories.getAllCategories().get(0).getCategoryId();
        Transaction tx = service.recordTransaction(u.getUserId(), categoryId, "Complete-Merchant", new BigDecimal("55.00"));
        Rewards pending = service.createPendingRewardForTransaction(tx.getTransactionId(), "ETH");

        Rewards done = service.completeReward(pending.getRewardId());

        assertThat(done.getStatus()).as("expected COMPLETED").isEqualTo(RewardsStatus.COMPLETED);
        assertThat(done.getTransactionHash()).as("hash should be set").isNotBlank();
    }

    @Test
    @DisplayName("recalcReward(): recalculates amounts and keeps status")
    void recalcReward_updatesNumbersKeepsStatus() throws Exception {
        // Create a pending reward
        User u = service.getAllUsers().get(0);
        int categoryId = categories.getAllCategories().get(0).getCategoryId();
        Transaction tx = service.recordTransaction(u.getUserId(), categoryId, "Recalc-Merchant", new BigDecimal("80.00"));
        Rewards pending = service.createPendingRewardForTransaction(tx.getTransactionId(), "ETH");

        Rewards before = pending;
        Rewards after = service.recalcReward(before.getRewardId());

        // Status should remain whatever it was (PENDING)
        assertThat(after.getStatus())
                .as("recalc should not change status")
                .isEqualTo(before.getStatus());

        // Amounts should be non-null and >= 0 (deterministic given your ETH_USD stub)
        assertThat(after.getRewardAmountUsd())
                .as("reward USD should be set")
                .isNotNull()
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);

        assertThat(after.getRewardAmountCrypto())
                .as("reward crypto should be set")
                .isNotNull();
    }
}
