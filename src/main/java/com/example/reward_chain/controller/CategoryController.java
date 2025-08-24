package com.example.reward_chain.controller;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
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

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> rename(@PathVariable int categoryId,
                                           @RequestBody Map<String,Object> body)
            throws InternalErrorException, RecordNotFoundException {
        String name = (String) body.get("categoryName");
        // if you keep rewardPercentage here too, handle it similarly
        Category cur = repo.getCategoryById(categoryId); // add method in repo if missing
        cur.setCategoryName(name);
        repo.updateCategory(cur); // add in repo impl
        return ResponseEntity.ok(cur);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable int categoryId)
            throws InternalErrorException {
        repo.deleteCategory(categoryId); // add in repo impl
        return ResponseEntity.noContent().build();
    }
}
