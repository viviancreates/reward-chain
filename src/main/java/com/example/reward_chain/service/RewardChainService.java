package com.example.reward_chain.service;

import com.example.reward_chain.data.*;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.*;

import com.example.reward_chain.service.payout.PayoutService;
import com.example.reward_chain.service.wallet.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.reward_chain.service.MnemonicGeneratorService;
import com.example.reward_chain.service.pricing.CoinPriceService;




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
    private final UserCategoryRuleRepo userCategoryRuleRepo;
    private final MnemonicGeneratorService mnemonicGenerator;
    private final CoinPriceService priceService;
    private final PayoutService payoutService;


    public RewardChainService(UserRepo userRepo,
                              WalletRepo walletRepo,
                              AllocationsRepo allocationsRepo,
                              CategoryRepo categoryRepo,
                              TransactionRepo transactionRepo,
                              RewardsRepo rewardsRepo,
                              WalletService walletService,
                              UserCategoryRuleRepo userCategoryRuleRepo,
                              MnemonicGeneratorService mnemonicGenerator,
                              CoinPriceService priceService,
                              PayoutService payoutService) {
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
        this.allocationsRepo = allocationsRepo;
        this.categoryRepo = categoryRepo;
        this.transactionRepo = transactionRepo;
        this.rewardsRepo = rewardsRepo;
        this.walletService = walletService;
        this.userCategoryRuleRepo = userCategoryRuleRepo;
        this.mnemonicGenerator = mnemonicGenerator;
        this.priceService = priceService;
        this.payoutService = payoutService;

    }

    private static BigDecimal safe(BigDecimal v, String def) {
        return v != null ? v : new BigDecimal(def);
    }

    /** Prefer user rule (0..100) if present; otherwise category default (0..1). */
    private BigDecimal resolveEffectivePercent(int userId, int categoryId, BigDecimal categoryFraction)
            throws InternalErrorException {
        try {
            var rule = userCategoryRuleRepo.getForUserCategory(userId, categoryId);
            return PercentageService.toFraction(rule.getPercent()); // 0..100 → 0..1
        } catch (RecordNotFoundException e) {
            return categoryFraction; // fallback
        }
    }

    /** create ETH + USDC pending rewards for a transaction using user coin allocations. */
    @Transactional
    public List<Rewards> createPendingRewardsForTransaction(int transactionId)
            throws InternalErrorException, RecordNotFoundException {

        Transaction tx = transactionRepo.getTransactionById(transactionId);
        Category cat = categoryRepo.getCategoryById(tx.getCategoryId());
        Wallet w = walletRepo.getWalletByUserId(tx.getUserId());
        Allocations a = allocationsRepo.getAllocationByUserId(tx.getUserId());

        // ...
        BigDecimal pct = resolveEffectivePercent(tx.getUserId(), tx.getCategoryId(), cat.getRewardPercentage());

        BigDecimal baseUsd = tx.getAmount().multiply(pct).setScale(4, RoundingMode.HALF_UP);

// allocations
        BigDecimal ethUsd  = baseUsd.multiply(a.getEthPercent()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal usdcUsd = baseUsd.multiply(a.getUsdcPercent()).setScale(4, RoundingMode.HALF_UP);

// live prices (fallbacks preserved)
        BigDecimal ethUsdPrice  = safe(priceService.getUsd("ETH"),  "4000.00");
        BigDecimal usdcUsdPrice = safe(priceService.getUsd("USDC"), "1.00");

        BigDecimal ethCrypto  = ethUsd.divide(ethUsdPrice, 8, RoundingMode.HALF_UP);
        BigDecimal usdcCrypto = usdcUsd.divide(usdcUsdPrice, 8, RoundingMode.HALF_UP);

// build rewards (store price used)
        Rewards eth = new Rewards(
                tx.getTransactionId(), tx.getUserId(), "ETH",
                pct, ethUsd, ethCrypto, ethUsdPrice, w.getWalletAddress());
        eth.setStatus(RewardsStatus.PENDING);
        eth.setCreatedDate(LocalDateTime.now());

        Rewards usdc = new Rewards(
                tx.getTransactionId(), tx.getUserId(), "USDC",
                pct, usdcUsd, usdcCrypto, usdcUsdPrice, w.getWalletAddress());
        usdc.setStatus(RewardsStatus.PENDING);
        usdc.setCreatedDate(LocalDateTime.now());

        eth = rewardsRepo.addReward(eth);
        usdc = rewardsRepo.addReward(usdc);
        return List.of(eth, usdc);

    }

    public Rewards getRewardById(int rewardId) throws InternalErrorException, RecordNotFoundException {
        return rewardsRepo.getRewardById(rewardId);
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

    @Transactional(readOnly = true)
    public List<Rewards> getUserRewards(int userId) throws InternalErrorException {
        List<Rewards> list = rewardsRepo.getRewardsByUserId(userId);
        // Prefer createdDate desc, fallback to rewardId desc
        list.sort((a, b) -> {
            if (a.getCreatedDate() != null && b.getCreatedDate() != null) {
                return b.getCreatedDate().compareTo(a.getCreatedDate());
            }
            return Integer.compare(b.getRewardId(), a.getRewardId());
        });
        return list;
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

        BigDecimal pct   = cat.getRewardPercentage();
        BigDecimal usd   = tx.getAmount().multiply(pct).setScale(4, RoundingMode.HALF_UP);
        BigDecimal price = safe(priceService.getUsd(coinType), "4000.00"); // e.g., ETH fallback
        BigDecimal crypto = usd.divide(price, 8, RoundingMode.HALF_UP);

        Rewards r = new Rewards(
                tx.getTransactionId(), tx.getUserId(), coinType,
                pct, usd, crypto, price, w.getWalletAddress());
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

        try {
            // do the actual (or simulated) payout
            String txHash = payoutService.send(
                    r.getCoinType(),
                    r.getRewardAmountCrypto(),   // amount in crypto units
                    r.getWalletAddress()         // user's wallet address
            );

            r.setStatus(RewardsStatus.COMPLETED);
            r.setTransactionHash(txHash);
            rewardsRepo.updateReward(r);

            return rewardsRepo.getRewardById(rewardId);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
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
        BigDecimal price = safe(priceService.getUsd(r.getCoinType()), "4000.00");
        BigDecimal crypto = usd.divide(price, 8, RoundingMode.HALF_UP);

        r.setRewardPercentage(pct);
        r.setRewardAmountUsd(usd);
        r.setRewardAmountCrypto(crypto);
        r.setCoinPriceUsd(price);
        rewardsRepo.updateReward(r);

        return rewardsRepo.getRewardById(r.getRewardId());
    }

    @Transactional(readOnly = true)
    public List<Rewards> getRewardsByTransactionId(int txId) throws InternalErrorException {
        return rewardsRepo.getRewardsByTransactionId(txId);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(int userId)
            throws InternalErrorException, RecordNotFoundException {
        return walletRepo.getWalletByUserId(userId);
    }
}
