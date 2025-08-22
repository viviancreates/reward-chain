package com.example.reward_chain.service;

import com.example.reward_chain.data.*;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.*;

import com.example.reward_chain.service.wallet.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Rewards;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RewardChainService {

    private final UserRepo userRepo;
    private final WalletRepo walletRepo;
    private final AllocationsRepo allocationsRepo;
    private final CategoryRepo categoryRepo;
    private final TransactionRepo transactionRepo;
    private final RewardsRepo rewardsRepo;
    private final WalletService walletService;

    // For now keep pricing simple like Bistro taxes; swap to a CoinPriceService later
    private static final BigDecimal ETH_USD = new BigDecimal("4000.00");

    public Rewards getRewardById(int rewardId) throws InternalErrorException, RecordNotFoundException {
        return rewardsRepo.getRewardById(rewardId);
    }

    public RewardChainService(UserRepo userRepo,
                              WalletRepo walletRepo,
                              AllocationsRepo allocationsRepo,
                              CategoryRepo categoryRepo,
                              TransactionRepo transactionRepo,
                              RewardsRepo rewardsRepo,
                              WalletService walletService) {
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
        this.allocationsRepo = allocationsRepo;
        this.categoryRepo = categoryRepo;
        this.transactionRepo = transactionRepo;
        this.rewardsRepo = rewardsRepo;
        this.walletService = walletService;
    }

    // -------- Read helpers (similar to BistroService’s “get*” methods) --------

    @Transactional(readOnly = true)
    public List<User> getAllUsers() throws InternalErrorException {
        return userRepo.getAllUsers();
    }

    public List<Category> getAllCategories() throws InternalErrorException {
        return categoryRepo.getAllCategories();
    }

    public List<Transaction> getUserTransactions(int userId) throws InternalErrorException {
        return transactionRepo.getTransactionsByUserId(userId);
    }

    public List<Rewards> getUserRewards(int userId) throws InternalErrorException {
        return rewardsRepo.getRewardsByUserId(userId);
    }

    // -------- Business flows (the important part) --------

    /** Register a user and set up their wallet + allocations in one step. */
    @Transactional
    public User registerUserWithWalletAndAllocation(User user,
                                                    String walletAddress,
                                                    String network,
                                                    BigDecimal ethPercent,
                                                    BigDecimal usdcPercent)
            throws InternalErrorException {

        BigDecimal sum = ethPercent.add(usdcPercent);
        if (sum.compareTo(new BigDecimal("1.00")) != 0) {
            throw new InternalErrorException(
                    new IllegalArgumentException("Allocations must sum to 1.00"));
        }

        User saved = userRepo.addUser(user);

        String finalNetwork = (network == null || network.isBlank()) ? "ETH_MAINNET" : network;
        String finalAddress;
        try {
            finalAddress = (walletAddress == null || walletAddress.isBlank())
                    ? walletService.createWallet(finalNetwork)  // may throw
                    : walletAddress;
        } catch (Exception e) {
            // keep your service contract (InternalErrorException)
            throw new InternalErrorException(e);
        }
        walletRepo.addWallet(new Wallet(saved.getUserId(), finalAddress, finalNetwork));
        allocationsRepo.addAllocation(new Allocations(saved.getUserId(), ethPercent, usdcPercent));

        return saved;
    }

    /** Record a purchase. (Keeps it explicit and short like Bistro’s addOrder.) */
    @Transactional
    public Transaction recordTransaction(int userId, int categoryId, String merchant, BigDecimal amount)
            throws InternalErrorException, RecordNotFoundException {
        // sanity: ensure referenced rows exist; fail fast like Bistro validations
        userRepo.getUserById(userId);
        categoryRepo.getCategoryById(categoryId);

        Transaction tx = new Transaction(userId, categoryId, merchant, amount);
        return transactionRepo.addTransaction(tx);
    }

    /**
     * Calculate and persist a PENDING reward for a transaction.
     * Mirrors Bistro’s calculateOrderTotals(): do the math in one place, then save.
     */
    @Transactional
    public Rewards createPendingRewardForTransaction(int transactionId, String coinType)
            throws InternalErrorException, RecordNotFoundException {

        Transaction tx = transactionRepo.getTransactionById(transactionId);
        Category cat = categoryRepo.getCategoryById(tx.getCategoryId());
        Wallet w = walletRepo.getWalletByUserId(tx.getUserId());

        BigDecimal pct = cat.getRewardPercentage();                    // e.g., 0.03
        BigDecimal usd = tx.getAmount().multiply(pct)                  // amount * %
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal price = ETH_USD;                                    // stubbed price
        BigDecimal crypto = usd.divide(price, 8, RoundingMode.HALF_UP);

        Rewards r = new Rewards(
                tx.getTransactionId(),
                tx.getUserId(),
                coinType,
                pct,
                usd,
                crypto,
                price,
                w.getWalletAddress()
        );
        r.setStatus(RewardsStatus.PENDING);
        r.setTransactionHash(null);
        r.setCreatedDate(LocalDateTime.now());

        return rewardsRepo.addReward(r);
    }

    /** Mark a reward COMPLETED. Later you can inject a CryptoTransferService to get a real tx hash. */
    @Transactional
    public Rewards completeReward(int rewardId)
            throws InternalErrorException, RecordNotFoundException {
        Rewards r = rewardsRepo.getRewardById(rewardId);
        r.setStatus(RewardsStatus.COMPLETED);
        r.setTransactionHash("0xSIM_" + rewardId); // placeholder
        rewardsRepo.updateReward(r);
        return rewardsRepo.getRewardById(rewardId);
    }

    // -------- Optional utility if you ever change Category percentages --------

    /** Recalculate an existing reward using current category %, keep status as-is. */
    @Transactional
    public Rewards recalcReward(int rewardId)
            throws InternalErrorException, RecordNotFoundException {
        Rewards r = rewardsRepo.getRewardById(rewardId);
        Transaction tx = transactionRepo.getTransactionById(r.getTransactionId());
        Category cat = categoryRepo.getCategoryById(tx.getCategoryId());

        BigDecimal pct = cat.getRewardPercentage();
        BigDecimal usd = tx.getAmount().multiply(pct).setScale(4, RoundingMode.HALF_UP);
        BigDecimal crypto = usd.divide(ETH_USD, 8, RoundingMode.HALF_UP);

        r.setRewardPercentage(pct);
        r.setRewardAmountUsd(usd);
        r.setRewardAmountCrypto(crypto);
        r.setCoinPriceUsd(ETH_USD);
        rewardsRepo.updateReward(r);

        return rewardsRepo.getRewardById(r.getRewardId());
    }
}
