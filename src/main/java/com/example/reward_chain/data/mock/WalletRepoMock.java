package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.WalletRepo;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Wallet;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Repository
@Profile("mock")
public class WalletRepoMock implements WalletRepo {
    private final Map<Integer, Wallet> wallets = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public WalletRepoMock() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        addWallet(new Wallet(1, "0x1111", "ETH_MAINNET"));
        addWallet(new Wallet(2, "0x2222", "ETH_MAINNET"));
    }

    @Override
    public Wallet getWalletById(int id) throws RecordNotFoundException {
        Wallet w = wallets.get(id);
        if (w == null) throw new RecordNotFoundException("Wallet not found");
        return w;
    }

    @Override
    public Wallet getWalletByUserId(int userId) throws RecordNotFoundException {
        return wallets.values().stream()
                .filter(w -> w.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Wallet not found for user"));
    }

    @Override
    public Wallet getWalletByAddress(String walletAddress) throws RecordNotFoundException {
        return wallets.values().stream()
                .filter(w -> w.getWalletAddress().equals(walletAddress))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Wallet not found by address"));
    }

    @Override
    public List<Wallet> getAllWallets() { return new ArrayList<>(wallets.values()); }

    @Override
    public Wallet addWallet(Wallet wallet) {
        wallet.setWalletId(idCounter.getAndIncrement());
        wallets.put(wallet.getWalletId(), wallet);
        return wallet;
    }

    @Override
    public void updateWallet(Wallet wallet) {
        wallets.put(wallet.getWalletId(), wallet);
    }

    @Override
    public Wallet deleteWallet(int id) { return wallets.remove(id); }
}
