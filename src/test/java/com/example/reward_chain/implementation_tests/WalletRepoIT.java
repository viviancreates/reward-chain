package com.example.reward_chain.implementation_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.data.WalletRepo;
import com.example.reward_chain.model.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class WalletRepoIT extends BaseDbTest {

    @Autowired WalletRepo walletRepo;

    @Test
    @DisplayName("seed: Alice's wallet address by userId=1")
    void seed_wallet_for_alice() throws Exception {
        String expected = "0xALICE_WALLET";
        Wallet w = walletRepo.getWalletByUserId(1);
        String actual = w.getWalletAddress();
        assertEquals(expected, actual, "walletAddress: expected=" + expected + ", actual=" + actual);
    }

    @Test
    @DisplayName("update changes network")
    void update_changes_network() throws Exception {
        Wallet w = walletRepo.getWalletByUserId(1);
        String expected = "BASE_MAINNET";
        w.setNetwork(expected);
        walletRepo.updateWallet(w);

        String actual = walletRepo.getWalletById(w.getWalletId()).getNetwork();
        assertEquals(expected, actual, "network: expected=" + expected + ", actual=" + actual);
    }
}
