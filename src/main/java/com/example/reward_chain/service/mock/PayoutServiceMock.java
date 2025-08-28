package com.example.reward_chain.service.mock;

import com.example.reward_chain.config.PayoutProperties;
import com.example.reward_chain.service.payout.PayoutService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "payout.enabled", havingValue = "false", matchIfMissing = true)
public class PayoutServiceMock implements PayoutService {

    private final PayoutProperties props;

    public PayoutServiceMock(PayoutProperties props) {
        this.props = props;
    }

    @Override
    public String send(String coinType, BigDecimal amountCrypto, String toAddress) {
        // No real transfer — just log & return a fake tx hash.
        String simHash = "0xSIM_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        System.out.printf(
                "[SIM-PAYOUT] would send %s %s → %s on %s (from %s). tx=%s%n",
                amountCrypto, coinType, toAddress, props.getNetwork(), props.getAddress(), simHash
        );
        return simHash;
    }
}
