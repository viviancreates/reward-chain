package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.TransactionRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.TransactionMapper;
import com.example.reward_chain.model.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Profile("!mock")
public class TransactionRepoImpl implements TransactionRepo {
    private final JdbcTemplate jdbc;
    private final TransactionMapper mapper;

    public TransactionRepoImpl(JdbcTemplate jdbc, TransactionMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Transaction getTransactionById(int id) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `TransactionID`, `UserID`, `CategoryID`, `Merchant`, `Amount`, `TransactionDate`
            FROM `Transaction` WHERE `TransactionID` = ?
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
    public List<Transaction> getTransactionsByUserId(int userId) throws InternalErrorException {
        String sql = """
            SELECT `TransactionID`, `UserID`, `CategoryID`, `Merchant`, `Amount`, `TransactionDate`
            FROM `Transaction` WHERE `UserID` = ?
            ORDER BY `TransactionDate` DESC
        """;
        try {
            return jdbc.query(sql, mapper, userId);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() throws InternalErrorException {
        String sql = """
            SELECT `TransactionID`, `UserID`, `CategoryID`, `Merchant`, `Amount`, `TransactionDate`
            FROM `Transaction` ORDER BY `TransactionDate` DESC
        """;
        try {
            return jdbc.query(sql, mapper);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public Transaction addTransaction(Transaction t) throws InternalErrorException {
        String sql = """
            INSERT INTO `Transaction` (`UserID`, `CategoryID`, `Merchant`, `Amount`, `TransactionDate`)
            VALUES (?, ?, ?, ?, ?)
        """;
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, t.getUserId());
                ps.setInt(2, t.getCategoryId());
                ps.setString(3, t.getMerchant());
                ps.setBigDecimal(4, t.getAmount());
                ps.setTimestamp(5, t.getTransactionDate() != null ? Timestamp.valueOf(t.getTransactionDate()) : null);
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key == null) throw new InternalErrorException(new Exception("No generated key"));
            t.setTransactionId(key.intValue());
            return t;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public void updateTransaction(Transaction t) throws InternalErrorException {
        String sql = """
            UPDATE `Transaction`
            SET `UserID` = ?, `CategoryID` = ?, `Merchant` = ?, `Amount` = ?, `TransactionDate` = ?
            WHERE `TransactionID` = ?
        """;
        try {
            int rows = jdbc.update(sql,
                    t.getUserId(),
                    t.getCategoryId(),
                    t.getMerchant(),
                    t.getAmount(),
                    t.getTransactionDate() != null ? Timestamp.valueOf(t.getTransactionDate()) : null,
                    t.getTransactionId());
            if (rows == 0) throw new InternalErrorException(new Exception("Transaction not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Transactional
    @Override
    public Transaction deleteTransaction(int id) throws InternalErrorException {
        String select = """
            SELECT `TransactionID`, `UserID`, `CategoryID`, `Merchant`, `Amount`, `TransactionDate`
            FROM `Transaction` WHERE `TransactionID` = ?
        """;
        String delete = "DELETE FROM `Transaction` WHERE `TransactionID` = ?";
        try {
            Transaction before = jdbc.queryForObject(select, mapper, id);
            int rows = jdbc.update(delete, id);
            if (rows == 0) throw new InternalErrorException(new Exception("Delete failed."));
            return before;
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException(new Exception("Transaction not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }
}
