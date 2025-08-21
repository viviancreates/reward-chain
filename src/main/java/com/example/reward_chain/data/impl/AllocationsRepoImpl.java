package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.AllocationsRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.AllocationsMapper;
import com.example.reward_chain.model.Allocations;
import org.springframework.context.annotation.Profile;
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
@Profile("!mock")
public class AllocationsRepoImpl implements AllocationsRepo {
    private final JdbcTemplate jdbc;
    private final AllocationsMapper mapper;

    public AllocationsRepoImpl(JdbcTemplate jdbc, AllocationsMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Allocations getAllocationById(int id) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `AllocationID`, `UserID`, `EthPercent`, `UsdcPercent`
            FROM `Allocations` WHERE `AllocationID` = ?
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
    public Allocations getAllocationByUserId(int userId) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `AllocationID`, `UserID`, `EthPercent`, `UsdcPercent`
            FROM `Allocations` WHERE `UserID` = ?
        """;
        try {
            return jdbc.queryForObject(sql, mapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException();
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Allocations> getAllAllocations() throws InternalErrorException {
        String sql = """
            SELECT `AllocationID`, `UserID`, `EthPercent`, `UsdcPercent`
            FROM `Allocations` ORDER BY `AllocationID`
        """;
        try {
            return jdbc.query(sql, mapper);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public Allocations addAllocation(Allocations allocation) throws InternalErrorException {
        String sql = """
            INSERT INTO `Allocations` (`UserID`, `EthPercent`, `UsdcPercent`)
            VALUES (?, ?, ?)
        """;
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, allocation.getUserId());
                ps.setBigDecimal(2, allocation.getEthPercent());
                ps.setBigDecimal(3, allocation.getUsdcPercent());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key == null) throw new InternalErrorException(new Exception("No generated key"));
            allocation.setAllocationId(key.intValue());
            return allocation;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public void updateAllocation(Allocations allocation) throws InternalErrorException {
        String sql = """
            UPDATE `Allocations`
            SET `UserID` = ?, `EthPercent` = ?, `UsdcPercent` = ?
            WHERE `AllocationID` = ?
        """;
        try {
            int rows = jdbc.update(sql,
                    allocation.getUserId(),
                    allocation.getEthPercent(),
                    allocation.getUsdcPercent(),
                    allocation.getAllocationId());
            if (rows == 0) throw new InternalErrorException(new Exception("Allocation not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Transactional
    @Override
    public Allocations deleteAllocation(int id) throws InternalErrorException {
        String select = """
            SELECT `AllocationID`, `UserID`, `EthPercent`, `UsdcPercent`
            FROM `Allocations` WHERE `AllocationID` = ?
        """;
        String delete = "DELETE FROM `Allocations` WHERE `AllocationID` = ?";
        try {
            Allocations before = jdbc.queryForObject(select, mapper, id);
            int rows = jdbc.update(delete, id);
            if (rows == 0) throw new InternalErrorException(new Exception("Delete failed."));
            return before;
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException(new Exception("Allocation not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }
}
