package com.example.reward_chain.controller;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.model.User;
import com.example.reward_chain.service.RewardChainService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RewardChainService service;
    public UserController(RewardChainService service){ this.service = service; }

    // Simple register that accepts raw JSON map (no DTOs)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,Object> body)
            throws InternalErrorException {

        User user = new User(
                (String) body.get("firstName"),
                (String) body.get("lastName"),
                (String) body.get("email"),
                (String) body.get("password")
        );

        String walletAddress = (String) body.get("walletAddress");
        String network = (String) body.get("network");
        BigDecimal ethPercent = new BigDecimal(body.get("ethPercent").toString());
        BigDecimal usdcPercent = new BigDecimal(body.get("usdcPercent").toString());

        User saved = service.registerUserWithWalletAndAllocation(
                user, walletAddress, network, ethPercent, usdcPercent
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
