package com.example.reward_chain.data.mappers;

import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.model.RewardsStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RewardsMapper implements RowMapper<Rewards> {

    @Override
    public Rewards mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rewards rewards = new Rewards();

        rewards.setRewardId(rs.getInt("RewardID"));
        rewards.setTransactionId(rs.getInt("TransactionID"));
        rewards.setUserId(rs.getInt("UserID"));
        rewards.setCoinType(rs.getString("CoinType"));
        rewards.setRewardPercentage(rs.getBigDecimal("RewardPercentage"));
        rewards.setRewardAmountUsd(rs.getBigDecimal("RewardAmountUsd"));
        rewards.setRewardAmountCrypto(rs.getBigDecimal("RewardAmountCrypto"));
        rewards.setCoinPriceUsd(rs.getBigDecimal("CoinPriceUsd"));
        rewards.setWalletAddress(rs.getString("WalletAddress"));
        rewards.setTransactionHash(rs.getString("TransactionHash"));

        // Convert String to enum
        String statusString = rs.getString("Status");
        if (statusString != null) {
            rewards.setStatus(RewardsStatus.valueOf(statusString));
        }

        if (rs.getTimestamp("CreatedDate") != null) {
            rewards.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
        }

        return rewards;
    }
}