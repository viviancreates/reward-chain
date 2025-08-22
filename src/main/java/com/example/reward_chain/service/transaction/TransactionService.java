package com.example.reward_chain.service.transaction;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    // Manual entry from your UI
    Transaction addManual(int userId, int categoryId, String merchant, BigDecimal amount)
            throws InternalErrorException, RecordNotFoundException;

    // Later: pull transactions from Plaid and persist
    List<Transaction> importFromPlaid(int userId, String plaidAccessToken)
            throws InternalErrorException, RecordNotFoundException;

    List<Transaction> listByUser(int userId) throws InternalErrorException;
}
