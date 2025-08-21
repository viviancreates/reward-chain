package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.TransactionRepo;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Repository
@Profile("mock")
public class TransactionRepoMock implements TransactionRepo {
    private final Map<Integer, Transaction> transactions = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Random random = new Random();

    public TransactionRepoMock() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        // User 1: Vivian
        addAndSeed(new Transaction(1, 1, "Trader Joe's", new BigDecimal("100.00")));
        addAndSeed(new Transaction(1, 2, "Starbucks", new BigDecimal("15.50")));
        addAndSeed(new Transaction(1, 3, "Shell Gas", new BigDecimal("40.00")));
        addAndSeed(new Transaction(1, 1, "Whole Foods", new BigDecimal("250.25")));
        addAndSeed(new Transaction(1, 2, "Chipotle", new BigDecimal("12.99")));

        // User 2: Alice
        addAndSeed(new Transaction(2, 1, "Kroger", new BigDecimal("75.10")));
        addAndSeed(new Transaction(2, 2, "Panera Bread", new BigDecimal("18.75")));
        addAndSeed(new Transaction(2, 3, "Exxon Gas", new BigDecimal("52.30")));
        addAndSeed(new Transaction(2, 1, "Costco", new BigDecimal("310.40")));
        addAndSeed(new Transaction(2, 2, "Dunkin Donuts", new BigDecimal("8.20")));

        // User 3: Bob
        addAndSeed(new Transaction(3, 1, "Safeway", new BigDecimal("64.90")));
        addAndSeed(new Transaction(3, 2, "McDonald's", new BigDecimal("9.99")));
        addAndSeed(new Transaction(3, 3, "Chevron Gas", new BigDecimal("47.15")));
        addAndSeed(new Transaction(3, 1, "Walmart", new BigDecimal("120.00")));
        addAndSeed(new Transaction(3, 2, "Subway", new BigDecimal("11.45")));
    }

    private void addAndSeed(Transaction tx) {
        tx.setTransactionId(idCounter.getAndIncrement());
        // Assign a random transaction date within the last 15 days
        tx.setTransactionDate(LocalDateTime.now().minusDays(random.nextInt(15)));
        transactions.put(tx.getTransactionId(), tx);
    }

    @Override
    public Transaction getTransactionById(int id) throws RecordNotFoundException {
        Transaction t = transactions.get(id);
        if (t == null) throw new RecordNotFoundException();
        return t;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions.values()) {
            if (t.getUserId() == userId) result.add(t);
        }
        return result;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public Transaction addTransaction(Transaction transaction) {
        transaction.setTransactionId(idCounter.getAndIncrement());
        transaction.setTransactionDate(LocalDateTime.now());
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionId(), transaction);
    }

    @Override
    public Transaction deleteTransaction(int id) {
        return transactions.remove(id);
    }
}
