package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Transaction;

import java.util.List;

public interface TransactionRepo {

    // Get a transaction by its ID
    Transaction getTransactionById(int id) throws RecordNotFoundException, InternalErrorException;

    // Get all transactions for a specific user (like Vivian's purchase history)
    List<Transaction> getTransactionsByUserId(int userId) throws InternalErrorException;

    // Get all transactions in the system
    List<Transaction> getAllTransactions() throws InternalErrorException;

    // Add a new transaction (user makes a purchase)
    Transaction addTransaction(Transaction transaction) throws InternalErrorException;

    // Update an existing transaction
    void updateTransaction(Transaction transaction) throws InternalErrorException;

    // Delete a transaction by ID - returns the deleted transaction
    Transaction deleteTransaction(int id) throws InternalErrorException;
}