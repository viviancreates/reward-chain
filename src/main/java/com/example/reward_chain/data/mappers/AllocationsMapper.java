package com.example.reward_chain.data.mappers;

import com.example.reward_chain.model.Allocations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AllocationsMapper implements RowMapper<Allocations> {

    @Override
    public Allocations mapRow(ResultSet rs, int rowNum) throws SQLException {
        Allocations allocations = new Allocations();

        allocations.setAllocationId(rs.getInt("AllocationID"));
        allocations.setUserId(rs.getInt("UserID"));
        allocations.setEthPercent(rs.getBigDecimal("EthPercent"));
        allocations.setUsdcPercent(rs.getBigDecimal("UsdcPercent"));

        return allocations;
    }
}