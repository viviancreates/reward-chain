package com.example.reward_chain.service.pricing;

import java.math.BigDecimal;

public interface CoinPriceService {
    BigDecimal getUsd(String symbol);
}