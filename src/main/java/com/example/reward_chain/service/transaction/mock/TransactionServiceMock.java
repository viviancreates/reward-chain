package com.example.reward_chain.service.transaction.mock;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.TransactionRepo;
import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Transaction;
import com.example.reward_chain.service.transaction.TransactionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("mock")
public class TransactionServiceMock implements TransactionService {
    private final TransactionRepo txRepo;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;

    public TransactionServiceMock(TransactionRepo txRepo, UserRepo userRepo, CategoryRepo categoryRepo) {
        this.txRepo = txRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public Transaction addManual(int userId, int categoryId, String merchant, BigDecimal amount)
            throws InternalErrorException, RecordNotFoundException {
        // validate references like your RewardChainService does
        userRepo.getUserById(userId);
        categoryRepo.getCategoryById(categoryId);
        Transaction t = new Transaction(userId, categoryId, merchant, amount);
        return txRepo.addTransaction(t);
    }

    @Override
    public List<Transaction> importFromPlaid(int userId, String plaidAccessToken)
            throws InternalErrorException, RecordNotFoundException {
        // Mock: just seed a few fake transactions. In real impl, call Plaid.
        userRepo.getUserById(userId);

        List<Transaction> created = new ArrayList<>();
        created.add(txRepo.addTransaction(new Transaction(userId, 1, "Starbucks", new BigDecimal("5.25"))));
        created.add(txRepo.addTransaction(new Transaction(userId, 2, "Shell", new BigDecimal("42.10"))));
        created.add(txRepo.addTransaction(new Transaction(userId, 3, "Whole Foods", new BigDecimal("89.99"))));

        // Give them recent-ish timestamps (optional)
        for (int i = 0; i < created.size(); i++) {
            Transaction t = created.get(i);
            t.setTransactionDate(LocalDateTime.now().minusDays(i));
            txRepo.updateTransaction(t);
        }
        return created;
    }

    @Override
    public List<Transaction> listByUser(int userId) throws InternalErrorException {
        return txRepo.getTransactionsByUserId(userId);
    }
}
