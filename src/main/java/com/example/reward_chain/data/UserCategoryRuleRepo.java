package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.UserCategoryRule;

import java.util.List;

public interface UserCategoryRuleRepo {

    List<UserCategoryRule> getByUser(int userId) throws InternalErrorException;

    UserCategoryRule getForUserCategory(int userId, int categoryId)
            throws RecordNotFoundException, InternalErrorException;

    /** Insert or update (by unique userId+categoryId). Returns the upserted row. */
    UserCategoryRule upsert(UserCategoryRule rule) throws InternalErrorException;

    /** Replace all rows for a user (delete then insert provided list). */
    void replaceAllForUser(int userId, List<UserCategoryRule> rules) throws InternalErrorException;
}
