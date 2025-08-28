package com.example.reward_chain.controller;

import com.example.reward_chain.data.WalletRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.User;
import com.example.reward_chain.service.MnemonicGeneratorService;
import com.example.reward_chain.service.RewardChainService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.example.reward_chain.model.Wallet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RewardChainService service;
    private final WalletRepo walletRepo;
    private final MnemonicGeneratorService mnemonicGeneratorService;


    public UserController(RewardChainService service, WalletRepo walletRepo, MnemonicGeneratorService mnemonicGeneratorService) {
        this.service = service;
        this.walletRepo = walletRepo;
        this.mnemonicGeneratorService = mnemonicGeneratorService;

    }

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

        boolean autoCreate = (walletAddress == null || walletAddress.isBlank());

        User saved = service.registerUserWithWalletAndAllocation(
                user, walletAddress, network, ethPercent, usdcPercent
        );

        Wallet wallet;
        try {
            wallet = walletRepo.getWalletByUserId(saved.getUserId());
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }

        String oneTimeMnemonic = autoCreate ? mnemonicGeneratorService.generateMnemonicPhrase() : null;

        Map<String, Object> resp = new HashMap<>();
        resp.put("user", saved);
        resp.put("walletAddress", wallet.getWalletAddress());
        resp.put("network", wallet.getNetwork());
        if (oneTimeMnemonic != null) resp.put("oneTimeMnemonic", oneTimeMnemonic);

        return ResponseEntity.status(HttpStatus.CREATED).body(resp); // <-- return the map
    }

    @GetMapping("/{userId}/wallet")
    public ResponseEntity<Wallet> getWallet(@PathVariable int userId)
            throws InternalErrorException, RecordNotFoundException {
        return ResponseEntity.ok(service.getWalletByUserId(userId));
    }

}
