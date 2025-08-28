package com.example.reward_chain.controller;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.dto.CreatePendingRewardRequest;
import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.service.RewardChainService;
import com.example.reward_chain.service.pricing.CoinPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {
    private final RewardChainService service;
    private final CoinPriceService coinPriceService;

    public RewardsController(RewardChainService service, CoinPriceService coinPriceService){
        this.service = service;
        this.coinPriceService = coinPriceService;
    }

    @PostMapping("/pending")
    public ResponseEntity<Rewards> createPending(@RequestBody CreatePendingRewardRequest req)
            throws InternalErrorException, RecordNotFoundException {
        return ResponseEntity.status(201)
                .body(service.createPendingRewardForTransaction(req.transactionId(), req.coinType()));
    }

    @PostMapping("/{rewardId}/complete")
    public ResponseEntity<Rewards> complete(@PathVariable int rewardId)
            throws InternalErrorException, RecordNotFoundException {
        return ResponseEntity.ok(service.completeReward(rewardId));
    }

    // get a single reward by id
    @GetMapping("/{rewardId}")
    public ResponseEntity<Rewards> getById(@PathVariable int rewardId)
            throws InternalErrorException, RecordNotFoundException {
        // Use the repo via the service layer; add a passthrough in the service:
        return ResponseEntity.ok(service.getRewardById(rewardId));
    }

    // list rewards for a user (most recent first)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rewards>> getByUser(
            @PathVariable int userId,
            @RequestParam(name = "live", defaultValue = "false") boolean live
    ) throws InternalErrorException {
        var list = service.getUserRewards(userId);
        if (live) {
            for (var r : list) {
                var p = coinPriceService.getUsd(r.getCoinType());
                if (p != null) r.setCoinPriceUsd(p); // override for response only
            }
        }
        return ResponseEntity.ok(list);
    }

    // controller
    @GetMapping("/tx/{transactionId}")
    public ResponseEntity<List<Rewards>> byTx(@PathVariable int transactionId)
            throws InternalErrorException {
        return ResponseEntity.ok(service.getRewardsByTransactionId(transactionId));
    }

}
