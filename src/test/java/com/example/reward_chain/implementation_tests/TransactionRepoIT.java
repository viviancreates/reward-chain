package com.example.reward_chain.implementation_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.data.TransactionRepo;
import com.example.reward_chain.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepoIT extends BaseDbTest {

    @Autowired TransactionRepo transactionRepo;

    @Test
    @DisplayName("seed: userId=1 has 3 transactions")
    void seeded_count_for_user1() throws Exception {
        int expected = 3; // per seed
        int actual = transactionRepo.getTransactionsByUserId(1).size();
        assertEquals(expected, actual, "tx count for user 1: expected=" + expected + ", actual=" + actual);
    }

    @Test
    @DisplayName("add assigns id")
    void add_assigns_id() throws Exception {
        Transaction t = new Transaction(1, 1, "Test Merchant", new BigDecimal("10.00"));
        t = transactionRepo.addTransaction(t);
        int actualId = t.getTransactionId();
        assertTrue(actualId > 0, "transactionId: expected>0, actual=" + actualId);
    }
}
