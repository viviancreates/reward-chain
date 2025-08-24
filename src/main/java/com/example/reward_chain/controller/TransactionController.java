package com.example.reward_chain.controller;

import com.example.reward_chain.data.exceptions.*;
import com.example.reward_chain.model.Transaction;
import com.example.reward_chain.service.RewardChainService;
import com.example.reward_chain.service.transaction.TransactionService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService service;
    public TransactionController(TransactionService service){ this.service = service; }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Map<String,Object> body)
            throws InternalErrorException, RecordNotFoundException {

        int userId = Integer.parseInt(body.get("userId").toString());
        int categoryId = Integer.parseInt(body.get("categoryId").toString());
        String merchant = body.get("merchant").toString();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        Transaction tx = service.addManual(userId, categoryId, merchant, amount);

        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }

    // List by user (handy for your UI)
    @GetMapping("/user/{userId}")
    public List<Transaction> listByUser(@PathVariable int userId) throws InternalErrorException {
        return service.listByUser(userId);
    }

    // Import from Plaid (works in mock profile today)
    @PostMapping("/import/plaid")
    public List<Transaction> importFromPlaid(@RequestBody Map<String,Object> body)
            throws InternalErrorException, RecordNotFoundException {
        int userId = Integer.parseInt(body.get("userId").toString());
        String accessToken = (String) body.get("accessToken"); // later: retrieve from your store
        return service.importFromPlaid(userId, accessToken);
    }
}

