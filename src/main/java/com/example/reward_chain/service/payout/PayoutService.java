package com.example.reward_chain.service.payout;

import java.math.BigDecimal;

public interface PayoutService {
    /**
     * Send a reward from the payout wallet to the user's wallet.
     * @param coinType  e.g. "ETH" or "USDC"
     * @param amountCrypto amount in native units (ETH, or ERC-20 tokens)
     * @param toAddress destination wallet (user's wallet)
     * @return tx hash (or a simulated hash when payout is disabled)
     */
    String send(String coinType, BigDecimal amountCrypto, String toAddress) throws Exception;
}
