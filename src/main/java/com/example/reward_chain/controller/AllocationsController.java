package com.example.reward_chain.controller;

import com.example.reward_chain.data.AllocationsRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Allocations;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/allocations")
public class AllocationsController {

    private final AllocationsRepo repo;
    public AllocationsController(AllocationsRepo repo){ this.repo = repo; }

    @GetMapping("/user/{userId}")
    public Allocations byUser(@PathVariable int userId)
            throws RecordNotFoundException, InternalErrorException {
        return repo.getAllocationByUserId(userId);
    }

    // Create (only needed if you didn't create on registration)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String,Object> body)
            throws InternalErrorException {
        int userId = Integer.parseInt(body.get("userId").toString());
        BigDecimal eth = new BigDecimal(body.get("ethPercent").toString());
        BigDecimal usdc = new BigDecimal(body.get("usdcPercent").toString());
        if (eth.add(usdc).compareTo(new BigDecimal("1.00")) != 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("error","BAD_REQUEST","message","ethPercent + usdcPercent must equal 1.00")
            );
        }
        Allocations created = repo.addAllocation(new Allocations(userId, eth, usdc));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update by allocationId (fetch current, edit fields, save)
    @PutMapping("/{allocationId}")
    public ResponseEntity<?> update(@PathVariable int allocationId,
                                    @RequestBody Map<String,Object> body)
            throws InternalErrorException, RecordNotFoundException {
        Allocations current = repo.getAllocationById(allocationId);

        BigDecimal eth = body.containsKey("ethPercent")
                ? new BigDecimal(body.get("ethPercent").toString())
                : current.getEthPercent();
        BigDecimal usdc = body.containsKey("usdcPercent")
                ? new BigDecimal(body.get("usdcPercent").toString())
                : current.getUsdcPercent();

        if (eth.add(usdc).compareTo(new BigDecimal("1.00")) != 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("error","BAD_REQUEST","message","ethPercent + usdcPercent must equal 1.00")
            );
        }

        if (body.containsKey("userId")) {
            current.setUserId(Integer.parseInt(body.get("userId").toString()));
        }
        current.setEthPercent(eth);
        current.setUsdcPercent(usdc);

        repo.updateAllocation(current);
        return ResponseEntity.ok(repo.getAllocationById(allocationId));
    }

    @DeleteMapping("/{allocationId}")
    public ResponseEntity<Allocations> delete(@PathVariable int allocationId)
            throws InternalErrorException {
        return ResponseEntity.ok(repo.deleteAllocation(allocationId));
    }
}
