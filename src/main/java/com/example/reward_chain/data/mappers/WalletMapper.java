package com.example.reward_chain.data.mappers;

import com.example.reward_chain.model.Wallet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WalletMapper implements RowMapper<Wallet> {

    @Override
    public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Wallet wallet = new Wallet();

        wallet.setWalletId(rs.getInt("WalletID"));
        wallet.setUserId(rs.getInt("UserID"));
        wallet.setWalletAddress(rs.getString("WalletAddress"));
        wallet.setNetwork(rs.getString("Network"));

        return wallet;
    }
}