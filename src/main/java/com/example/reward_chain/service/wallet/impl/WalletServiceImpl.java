package com.example.reward_chain.service.wallet.impl;

import com.example.reward_chain.service.wallet.WalletService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class WalletServiceImpl implements WalletService {
    @Override
    public String createWallet(String network) throws Exception {
        // TODO: integrate Tatum
        throw new UnsupportedOperationException("Wallet creation not implemented for prod yet");
    }
}
