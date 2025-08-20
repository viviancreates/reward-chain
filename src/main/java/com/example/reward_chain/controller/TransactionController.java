package com.example.reward_chain.controller;

import com.example.reward_chain.data.exceptions.*;
import com.example.reward_chain.model.Transaction;
import com.example.reward_chain.service.RewardChainService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final RewardChainService service;
    public TransactionController(RewardChainService service){ this.service = service; }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Map<String,Object> body)
            throws InternalErrorException, RecordNotFoundException {

        int userId = Integer.parseInt(body.get("userId").toString());
        int categoryId = Integer.parseInt(body.get("categoryId").toString());
        String merchant = body.get("merchant").toString();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        Transaction tx = service.recordTransaction(userId, categoryId, merchant, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }
}

