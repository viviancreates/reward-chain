package com.example.reward_chain.controller;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.dto.CreatePendingRewardRequest;
import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.service.RewardChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {
    private final RewardChainService service;

    public RewardsController(RewardChainService service){
        this.service = service;
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
    public ResponseEntity<List<Rewards>> getByUser(@PathVariable int userId)
            throws InternalErrorException {
        return ResponseEntity.ok(service.getUserRewards(userId));
    }

    // controller
    @GetMapping("/tx/{transactionId}")
    public ResponseEntity<List<Rewards>> byTx(@PathVariable int transactionId)
            throws InternalErrorException {
        return ResponseEntity.ok(service.getRewardsByTransactionId(transactionId));
    }

}
