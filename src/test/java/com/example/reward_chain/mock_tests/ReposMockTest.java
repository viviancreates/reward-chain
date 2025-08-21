package com.example.reward_chain.mock_tests;

import com.example.reward_chain.data.*;
import com.example.reward_chain.model.Transaction;
import com.example.reward_chain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("mock")
class ReposMockTest {

    @Autowired UserRepo users;
    @Autowired CategoryRepo categories;
    @Autowired TransactionRepo txs;

    @Test
    @DisplayName("Seed data should be loaded into User, Category, and Transaction repos")
    void seed_data_is_present() throws Exception {
        int expectedUsersMin = 1;
        int actualUsers = users.getAllUsers().size();
        assertThat(actualUsers)
                .as("Expected at least %d user(s), but got %d", expectedUsersMin, actualUsers)
                .isGreaterThanOrEqualTo(expectedUsersMin);

        int expectedCategoriesMin = 1;
        int actualCategories = categories.getAllCategories().size();
        assertThat(actualCategories)
                .as("Expected at least %d category, but got %d", expectedCategoriesMin, actualCategories)
                .isGreaterThanOrEqualTo(expectedCategoriesMin);

        int expectedTransactionsMin = 1;
        int actualTransactions = txs.getAllTransactions().size();
        assertThat(actualTransactions)
                .as("Expected at least %d transaction, but got %d", expectedTransactionsMin, actualTransactions)
                .isGreaterThanOrEqualTo(expectedTransactionsMin);
    }

    @Test
    @DisplayName("Each seeded user should have at least 5 transactions")
    void each_seeded_user_has_at_least_five_transactions() throws Exception {
        List<User> all = users.getAllUsers();
        assertThat(all).isNotEmpty();

        int expectedMinTransactions = 5;
        for (User u : all) {
            int actual = txs.getTransactionsByUserId(u.getUserId()).size();
            assertThat(actual)
                    .as("Expected user %d to have >= %d transactions, but got %d",
                            u.getUserId(), expectedMinTransactions, actual)
                    .isGreaterThanOrEqualTo(expectedMinTransactions);
        }
    }
}

