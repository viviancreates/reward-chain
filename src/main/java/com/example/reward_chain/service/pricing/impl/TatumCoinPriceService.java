package com.example.reward_chain.service.pricing.impl;

import com.example.reward_chain.service.pricing.CoinPriceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TatumCoinPriceService implements CoinPriceService {

    private final WebClient web;
    private final long ttlMs;

    public TatumCoinPriceService(
            @Value("${tatum.base-url:https://api.tatum.io/v3}") String baseUrl,
            @Value("${tatum.api.key}") String apiKey,
            @Value("${tatum.price.cacheTtlMs:60000}") long ttlMs) {
        this.web = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
        this.ttlMs = ttlMs;
    }

    private static final class CacheItem {
        final BigDecimal value; final long ts;
        CacheItem(BigDecimal v, long t) { this.value = v; this.ts = t; }
    }
    private final Map<String, CacheItem> cache = new ConcurrentHashMap<>();

    @Override
    public BigDecimal getUsd(String symbol) {
        final String key = symbol.toUpperCase() + "_USD";
        final long now = System.currentTimeMillis();
        CacheItem hit = cache.get(key);
        if (hit != null && now - hit.ts <= ttlMs) return hit.value;

        try {
            // Tatum: GET /v3/tatum/rate/{currency}?basePair=USD  → { value: number, ... }
            RateResponse rr = web.get()
                    .uri(uri -> uri.path("/tatum/rate/{currency}")
                            .queryParam("basePair", "USD")
                            .build(symbol.toUpperCase()))
                    .retrieve()
                    .bodyToMono(RateResponse.class)
                    .block(); // simple sync call in service layer

            BigDecimal val = rr != null ? rr.getValue() : null;
            if (val != null) cache.put(key, new CacheItem(val, now));
            return val;
        } catch (Exception e) {
            return null; // caller handles fallback
        }
    }

    // minimal DTO
    private static final class RateResponse {
        private BigDecimal value;
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal v) { this.value = v; }
        @Override public String toString() { return "RateResponse{value=" + value + ", ts=" + Instant.now() + "}"; }
    }
}