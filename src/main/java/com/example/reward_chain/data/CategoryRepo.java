package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Category;

import java.util.List;

public interface CategoryRepo {

    // Get a category by its ID
    Category getCategoryById(int id) throws RecordNotFoundException, InternalErrorException;

    // Get all available categories (like Groceries, Gas, Dining, etc.)
    List<Category> getAllCategories() throws InternalErrorException;

    // Add a new category (like "Travel" with 2% rewards)
    Category addCategory(Category category) throws InternalErrorException;

    // Update an existing category (change reward percentage, etc.)
    void updateCategory(Category category) throws InternalErrorException;

    // Delete a category by ID - returns the deleted category
    void deleteCategory(int id) throws InternalErrorException;
}