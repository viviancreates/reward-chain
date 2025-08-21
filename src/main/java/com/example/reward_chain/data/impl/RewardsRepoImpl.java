package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.RewardsRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.RewardsMapper;
import com.example.reward_chain.model.Rewards;
import com.example.reward_chain.model.RewardsStatus;
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
public class RewardsRepoImpl implements RewardsRepo {
    private final JdbcTemplate jdbc;
    private final RewardsMapper mapper;

    public RewardsRepoImpl(JdbcTemplate jdbc, RewardsMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Rewards getRewardById(int id) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `RewardID`, `TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
                   `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`, `WalletAddress`,
                   `TransactionHash`, `Status`, `CreatedDate`
            FROM `Rewards` WHERE `RewardID` = ?
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
    public List<Rewards> getRewardsByUserId(int userId) throws InternalErrorException {
        String sql = """
            SELECT `RewardID`, `TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
                   `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`, `WalletAddress`,
                   `TransactionHash`, `Status`, `CreatedDate`
            FROM `Rewards` WHERE `UserID` = ?
            ORDER BY `CreatedDate` DESC
        """;
        try {
            return jdbc.query(sql, mapper, userId);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Rewards> getRewardsByTransactionId(int transactionId) throws InternalErrorException {
        String sql = """
            SELECT `RewardID`, `TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
                   `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`, `WalletAddress`,
                   `TransactionHash`, `Status`, `CreatedDate`
            FROM `Rewards` WHERE `TransactionID` = ?
            ORDER BY `CreatedDate` DESC
        """;
        try {
            return jdbc.query(sql, mapper, transactionId);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Rewards> getRewardsByStatus(RewardsStatus status) throws InternalErrorException {
        String sql = """
            SELECT `RewardID`, `TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
                   `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`, `WalletAddress`,
                   `TransactionHash`, `Status`, `CreatedDate`
            FROM `Rewards` WHERE `Status` = ?
            ORDER BY `CreatedDate` DESC
        """;
        try {
            return jdbc.query(sql, mapper, status.name());
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Rewards> getAllRewards() throws InternalErrorException {
        String sql = """
            SELECT `RewardID`, `TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
                   `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`, `WalletAddress`,
                   `TransactionHash`, `Status`, `CreatedDate`
            FROM `Rewards` ORDER BY `CreatedDate` DESC
        """;
        try {
            return jdbc.query(sql, mapper);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public Rewards addReward(Rewards reward) throws InternalErrorException {
        String sql = """
            INSERT INTO `Rewards`
            (`TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
             `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`,
             `WalletAddress`, `TransactionHash`, `Status`, `CreatedDate`)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, reward.getTransactionId());
                ps.setInt(2, reward.getUserId());
                ps.setString(3, reward.getCoinType());
                ps.setBigDecimal(4, reward.getRewardPercentage());
                ps.setBigDecimal(5, reward.getRewardAmountUsd());
                ps.setBigDecimal(6, reward.getRewardAmountCrypto());
                ps.setBigDecimal(7, reward.getCoinPriceUsd());
                ps.setString(8, reward.getWalletAddress());
                ps.setString(9, reward.getTransactionHash());
                ps.setString(10, reward.getStatus() != null ? reward.getStatus().name() : RewardsStatus.PENDING.name());
                ps.setTimestamp(11, reward.getCreatedDate() != null ? Timestamp.valueOf(reward.getCreatedDate()) : null);
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key == null) throw new InternalErrorException(new Exception("No generated key"));
            reward.setRewardId(key.intValue());
            return reward;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public void updateReward(Rewards reward) throws InternalErrorException {
        String sql = """
            UPDATE `Rewards`
            SET `TransactionID` = ?, `UserID` = ?, `CoinType` = ?,
                `RewardPercentage` = ?, `RewardAmountUsd` = ?, `RewardAmountCrypto` = ?,
                `CoinPriceUsd` = ?, `WalletAddress` = ?, `TransactionHash` = ?,
                `Status` = ?, `CreatedDate` = ?
            WHERE `RewardID` = ?
        """;
        try {
            int rows = jdbc.update(sql,
                    reward.getTransactionId(),
                    reward.getUserId(),
                    reward.getCoinType(),
                    reward.getRewardPercentage(),
                    reward.getRewardAmountUsd(),
                    reward.getRewardAmountCrypto(),
                    reward.getCoinPriceUsd(),
                    reward.getWalletAddress(),
                    reward.getTransactionHash(),
                    reward.getStatus() != null ? reward.getStatus().name() : RewardsStatus.PENDING.name(),
                    reward.getCreatedDate() != null ? Timestamp.valueOf(reward.getCreatedDate()) : null,
                    reward.getRewardId());
            if (rows == 0) throw new InternalErrorException(new Exception("Reward not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Transactional
    @Override
    public Rewards deleteReward(int id) throws InternalErrorException {
        String select = """
            SELECT `RewardID`, `TransactionID`, `UserID`, `CoinType`, `RewardPercentage`,
                   `RewardAmountUsd`, `RewardAmountCrypto`, `CoinPriceUsd`, `WalletAddress`,
                   `TransactionHash`, `Status`, `CreatedDate`
            FROM `Rewards` WHERE `RewardID` = ?
        """;
        String delete = "DELETE FROM `Rewards` WHERE `RewardID` = ?";
        try {
            Rewards before = jdbc.queryForObject(select, mapper, id);
            int rows = jdbc.update(delete, id);
            if (rows == 0) throw new InternalErrorException(new Exception("Delete failed."));
            return before;
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException(new Exception("Reward not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }
}
