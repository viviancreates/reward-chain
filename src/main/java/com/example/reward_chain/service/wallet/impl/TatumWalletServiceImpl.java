package com.example.reward_chain.service.wallet.impl;

import com.example.reward_chain.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("prod")   // make sure you run with prod profile
public class TatumWalletServiceImpl implements WalletService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${tatum.api.key}")
    private String tatumApiKey;

    @Value("${tatum.base-url}")
    private String tatumBaseUrl;

    @Override
    public String createWallet(String network) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", tatumApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Step 1: create ETH wallet
        String url = tatumBaseUrl + "/ethereum/wallet";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        JsonNode walletData = objectMapper.readTree(response.getBody());
        String xpub = walletData.path("xpub").asText();

        // Step 2: derive first address
        String addrUrl = tatumBaseUrl + "/ethereum/address/" + xpub + "/0";
        ResponseEntity<String> addrResp = restTemplate.exchange(addrUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        JsonNode addressData = objectMapper.readTree(addrResp.getBody());

        return addressData.path("address").asText();
    }
}
