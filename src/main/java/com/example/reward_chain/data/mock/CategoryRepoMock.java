package com.example.reward_chain.data.mock;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Category;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Repository
@Profile("mock")
public class CategoryRepoMock implements CategoryRepo {
    private final Map<Integer, Category> categories = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public CategoryRepoMock() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        addCategory(new Category("Groceries", new BigDecimal("0.03")));
        addCategory(new Category("Dining", new BigDecimal("0.02")));
        addCategory(new Category("Gas", new BigDecimal("0.01")));
    }

    @Override
    public Category getCategoryById(int id) throws RecordNotFoundException {
        Category c = categories.get(id);
        if (c == null) throw new RecordNotFoundException();
        return c;
    }

    @Override
    public List<Category> getAllCategories() { return new ArrayList<>(categories.values()); }

    @Override
    public Category addCategory(Category category) {
        category.setCategoryId(idCounter.getAndIncrement());
        categories.put(category.getCategoryId(), category);
        return category;
    }

    @Override
    public void updateCategory(Category category) { categories.put(category.getCategoryId(), category); }

    @Override
    public Category deleteCategory(int id) { return categories.remove(id); }
}
