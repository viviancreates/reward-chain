package com.example.reward_chain.service.transaction.impl;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.TransactionRepo;
import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Transaction;
import com.example.reward_chain.service.RewardChainService;
import com.example.reward_chain.service.transaction.TransactionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Profile("!mock")
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo txRepo;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final RewardChainService rcService;

    public TransactionServiceImpl(TransactionRepo txRepo, UserRepo userRepo, CategoryRepo categoryRepo, RewardChainService rcService) {
        this.txRepo = txRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.rcService = rcService;
    }

    @Transactional
    @Override
    public Transaction addManual(int userId, int categoryId, String merchant, BigDecimal amount)
            throws InternalErrorException, RecordNotFoundException {
        userRepo.getUserById(userId);
        categoryRepo.getCategoryById(categoryId);

        Transaction tx = new Transaction(userId, categoryId, merchant, amount);
        tx = txRepo.addTransaction(tx);

        // auto-create ETH + USDC pending rewards (uses user rules + coin allocation)
        try {
            rcService.createPendingRewardsForTransaction(tx.getTransactionId());
        } catch (Exception e) {
            // don’t fail tx save if rewards fail
            // log.warn("Reward creation failed for tx {}", tx.getTransactionId(), e);
        }
        return tx;
    }

    @Transactional
    @Override
    public List<Transaction> importFromPlaid(int userId, String plaidAccessToken)
            throws InternalErrorException, RecordNotFoundException {
        // TODO: call Plaid /transactions/sync or /transactions/get,
        // map Plaid categories -> your categoryId, persist via txRepo.addTransaction(...)
        // For now, return empty list so the endpoint is wired.
        return List.of();
    }

    @Override
    public List<Transaction> listByUser(int userId) throws InternalErrorException {
        return txRepo.getTransactionsByUserId(userId);
    }
}
