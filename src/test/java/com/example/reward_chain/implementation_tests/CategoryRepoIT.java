package com.example.reward_chain.implementation_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepoIT extends BaseDbTest {

    @Autowired CategoryRepo categoryRepo;

    @Test
    @DisplayName("getAllCategories size = 6 (seed)")
    void getAll_size6() throws Exception {
        int expected = 6;
        int actual = categoryRepo.getAllCategories().size();
        assertEquals(expected, actual, "categories size: expected=" + expected + ", actual=" + actual);
    }

    @Test
    @DisplayName("update changes reward percentage to 0.04")
    void update_changes_percentage() throws Exception {
        Category c = categoryRepo.getCategoryById(1); // Groceries
        BigDecimal expected = new BigDecimal("0.04"); // 2-decimal value
        c.setRewardPercentage(expected);
        categoryRepo.updateCategory(c);

        BigDecimal actual = categoryRepo.getCategoryById(1).getRewardPercentage();
        assertEquals(0, expected.compareTo(actual),
                "rewardPercentage: expected=" + expected + ", actual=" + actual);
    }

}
