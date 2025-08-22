package com.example.reward_chain.controller;

import com.example.reward_chain.data.UserCategoryRuleRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.model.UserCategoryRule;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-category-rules")
public class UserCategoryRuleController {

    private final UserCategoryRuleRepo repo;

    public UserCategoryRuleController(UserCategoryRuleRepo repo) {
        this.repo = repo;
    }

    @GetMapping("/{userId}")
    public List<UserCategoryRule> getByUser(@PathVariable int userId)
            throws InternalErrorException {
        return repo.getByUser(userId);
    }

    /**
     * Replace all rules for a user. Body: [{categoryId, percent}, ...]
     * Each 'percent' is 0..100 (independent); no global sum required.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<?> replaceAll(@PathVariable int userId,
                                        @RequestBody List<Map<String,Object>> body)
            throws InternalErrorException {

        // Validate each percent 0..100
        for (Map<String,Object> row : body) {
            BigDecimal p = new BigDecimal(row.get("percent").toString());
            if (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(new BigDecimal("100.00")) > 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Each percent must be between 0.00 and 100.00"));
            }
        }

        List<UserCategoryRule> list = body.stream().map(row -> {
            int categoryId = Integer.parseInt(row.get("categoryId").toString());
            BigDecimal p = new BigDecimal(row.get("percent").toString()); // 0..100
            return new UserCategoryRule(userId, categoryId, p);
        }).toList();

        repo.replaceAllForUser(userId, list);
        return ResponseEntity.ok(repo.getByUser(userId));
    }

    /** Upsert a single rule. Body: {categoryId, percent} */
    @PostMapping("/{userId}")
    public ResponseEntity<?> upsertOne(@PathVariable int userId,
                                       @RequestBody Map<String,Object> body)
            throws InternalErrorException {
        int categoryId = Integer.parseInt(body.get("categoryId").toString());
        BigDecimal p = new BigDecimal(body.get("percent").toString());
        if (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(new BigDecimal("100.00")) > 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Percent must be between 0.00 and 100.00"));
        }
        UserCategoryRule saved = repo.upsert(new UserCategoryRule(userId, categoryId, p));
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }
}
