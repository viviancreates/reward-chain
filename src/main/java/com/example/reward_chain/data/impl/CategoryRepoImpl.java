package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.CategoryRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.CategoryMapper;
import com.example.reward_chain.model.Category;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryRepoImpl implements CategoryRepo {
    private final JdbcTemplate jdbc;
    private final CategoryMapper mapper;

    public CategoryRepoImpl(JdbcTemplate jdbc, CategoryMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Category getCategoryById(int id) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `CategoryID`, `CategoryName`, `RewardPercentage`
            FROM `Category` WHERE `CategoryID` = ?
        """;
        try {
            return jdbc.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException();
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Category> getAllCategories() throws InternalErrorException {
        String sql = """
            SELECT `CategoryID`, `CategoryName`, `RewardPercentage`
            FROM `Category` ORDER BY `CategoryName`
        """;
        try {
            return jdbc.query(sql, mapper);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public Category addCategory(Category category) throws InternalErrorException {
        String sql = """
            INSERT INTO `Category` (`CategoryName`, `RewardPercentage`)
            VALUES (?, ?)
        """;
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, category.getCategoryName());
                ps.setBigDecimal(2, category.getRewardPercentage());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key == null) throw new InternalErrorException(new Exception("No generated key"));
            category.setCategoryId(key.intValue());
            return category;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public void updateCategory(Category category) throws InternalErrorException {
        String sql = """
            UPDATE `Category`
            SET `CategoryName` = ?, `RewardPercentage` = ?
            WHERE `CategoryID` = ?
        """;
        try {
            int rows = jdbc.update(sql,
                    category.getCategoryName(),
                    category.getRewardPercentage(),
                    category.getCategoryId());
            if (rows == 0) throw new InternalErrorException(new Exception("Category not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Transactional
    @Override
    public Category deleteCategory(int id) throws InternalErrorException {
        String select = """
            SELECT `CategoryID`, `CategoryName`, `RewardPercentage`
            FROM `Category` WHERE `CategoryID` = ?
        """;
        String delete = "DELETE FROM `Category` WHERE `CategoryID` = ?";
        try {
            Category before = jdbc.queryForObject(select, mapper, id);
            int rows = jdbc.update(delete, id);
            if (rows == 0) throw new InternalErrorException(new Exception("Delete failed."));
            return before;
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException(new Exception("Category not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }
}
