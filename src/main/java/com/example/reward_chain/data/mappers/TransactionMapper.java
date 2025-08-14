package com.example.reward_chain.data.mappers;

import com.example.reward_chain.model.Transaction;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TransactionMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();

        transaction.setTransactionId(rs.getInt("TransactionID"));
        transaction.setUserId(rs.getInt("UserID"));
        transaction.setCategoryId(rs.getInt("CategoryID"));
        transaction.setMerchant(rs.getString("Merchant"));
        transaction.setAmount(rs.getBigDecimal("Amount"));

        if (rs.getTimestamp("TransactionDate") != null) {
            transaction.setTransactionDate(rs.getTimestamp("TransactionDate").toLocalDateTime());
        }

        return transaction;
    }
}