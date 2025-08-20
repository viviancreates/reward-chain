package com.example.reward_chain.controller;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.model.Category;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepo repo;
    public CategoryController(CategoryRepo repo){ this.repo = repo; }

    @GetMapping
    public List<Category> all() throws InternalErrorException { return repo.getAllCategories(); }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Map<String,Object> body)
            throws InternalErrorException {
        String name = (String) body.get("categoryName");
        BigDecimal pct = new BigDecimal(body.get("rewardPercentage").toString());
        Category created = repo.addCategory(new Category(name, pct));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
