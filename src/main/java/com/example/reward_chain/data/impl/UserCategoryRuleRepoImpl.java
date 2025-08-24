package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.UserCategoryRuleRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.UserCategoryRuleMapper;
import com.example.reward_chain.model.UserCategoryRule;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("!mock")
public class UserCategoryRuleRepoImpl implements UserCategoryRuleRepo {

    private final JdbcTemplate jdbc;

    public UserCategoryRuleRepoImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<UserCategoryRule> getByUser(int userId) throws InternalErrorException {
        try {
            final String sql = """
                SELECT RuleID, UserID, CategoryID, Percent
                FROM UserCategoryRule
                WHERE UserID = ?
                ORDER BY CategoryID
                """;
            return jdbc.query(sql, new UserCategoryRuleMapper(), userId);
        } catch (DataAccessException ex) {
            throw new InternalErrorException(ex);
        }
    }

    @Override
    public UserCategoryRule getForUserCategory(int userId, int categoryId)
            throws RecordNotFoundException, InternalErrorException {
        try {
            final String sql = """
                SELECT RuleID, UserID, CategoryID, Percent
                FROM UserCategoryRule
                WHERE UserID = ? AND CategoryID = ?
                """;
            List<UserCategoryRule> list = jdbc.query(sql, new UserCategoryRuleMapper(), userId, categoryId);
            if (list.isEmpty()) {
                throw new RecordNotFoundException("No rule for userId=" + userId + ", categoryId=" + categoryId);
            }
            return list.get(0);
        } catch (DataAccessException ex) {
            throw new InternalErrorException(ex);
        }
    }

    @Override
    public UserCategoryRule upsert(UserCategoryRule rule) throws InternalErrorException {
        try {
            // Try update first
            final String update = """
                UPDATE UserCategoryRule
                SET Percent = ?
                WHERE UserID = ? AND CategoryID = ?
                """;
            int rows = jdbc.update(update, rule.getPercent(), rule.getUserId(), rule.getCategoryId());
            if (rows == 0) {
                // Insert
                final String insert = """
                    INSERT INTO UserCategoryRule (UserID, CategoryID, Percent)
                    VALUES (?,?,?)
                    """;
                jdbc.update(insert, rule.getUserId(), rule.getCategoryId(), rule.getPercent());
            }
            // Return current row
            return getForUserCategory(rule.getUserId(), rule.getCategoryId());
        } catch (RecordNotFoundException e) {
            // should not happen immediately after insert
            throw new InternalErrorException(e);
        } catch (DataAccessException ex) {
            throw new InternalErrorException(ex);
        }
    }

    @Override
    public void replaceAllForUser(int userId, List<UserCategoryRule> rules) throws InternalErrorException {
        try {
            jdbc.update("DELETE FROM UserCategoryRule WHERE UserID = ?", userId);
            if (rules == null || rules.isEmpty()) return;
            final String insert = """
                INSERT INTO UserCategoryRule (UserID, CategoryID, Percent)
                VALUES (?,?,?)
                """;
            for (UserCategoryRule r : rules) {
                jdbc.update(insert, userId, r.getCategoryId(), r.getPercent());
            }
        } catch (DataAccessException ex) {
            throw new InternalErrorException(ex);
        }
    }
}
