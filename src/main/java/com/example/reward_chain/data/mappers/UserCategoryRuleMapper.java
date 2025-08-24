package com.example.reward_chain.data.mappers;

import com.example.reward_chain.model.UserCategoryRule;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCategoryRuleMapper implements RowMapper<UserCategoryRule> {
    @Override
    public UserCategoryRule mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserCategoryRule r = new UserCategoryRule();
        r.setRuleId(rs.getInt("RuleID"));
        r.setUserId(rs.getInt("UserID"));
        r.setCategoryId(rs.getInt("CategoryID"));
        r.setPercent(rs.getBigDecimal("Percent")); // 0..100
        return r;
    }
}
