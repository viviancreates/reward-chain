package com.example.reward_chain.service.wallet.mock;

import com.example.reward_chain.service.wallet.WalletService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@Profile({"default","mock"})
public class WalletServiceMock implements WalletService {
    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    @Override
    public String createWallet(String network) {
        char[] buf = new char[40];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = HEX[RNG.nextInt(16)];
        }
        return "0x" + new String(buf);
    }
}