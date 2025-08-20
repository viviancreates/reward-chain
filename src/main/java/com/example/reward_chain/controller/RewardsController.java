package com.example.reward_chain.controller;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
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
    public ResponseEntity<Rewards> createPending(@RequestBody java.util.Map<String,Object> body)
            throws InternalErrorException, RecordNotFoundException {
        int txId = Integer.parseInt(body.get("transactionId").toString());
        String coinType = body.get("coinType").toString();
        return ResponseEntity.status(201).body(service.createPendingRewardForTransaction(txId, coinType));
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
}
