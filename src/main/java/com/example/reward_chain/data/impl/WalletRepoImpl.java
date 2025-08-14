package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.WalletRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.WalletMapper;
import com.example.reward_chain.model.Wallet;
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
public class WalletRepoImpl implements WalletRepo {
    private final JdbcTemplate jdbc;
    private final WalletMapper mapper;

    public WalletRepoImpl(JdbcTemplate jdbc, WalletMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Wallet getWalletById(int id) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `WalletID`, `UserID`, `WalletAddress`, `Network`
            FROM `Wallet` WHERE `WalletID` = ?
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
    public Wallet getWalletByUserId(int userId) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `WalletID`, `UserID`, `WalletAddress`, `Network`
            FROM `Wallet` WHERE `UserID` = ?
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
    public Wallet getWalletByAddress(String walletAddress) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `WalletID`, `UserID`, `WalletAddress`, `Network`
            FROM `Wallet` WHERE `WalletAddress` = ?
        """;
        try {
            return jdbc.queryForObject(sql, mapper, walletAddress);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException();
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<Wallet> getAllWallets() throws InternalErrorException {
        String sql = """
            SELECT `WalletID`, `UserID`, `WalletAddress`, `Network`
            FROM `Wallet` ORDER BY `WalletID`
        """;
        try {
            return jdbc.query(sql, mapper);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public Wallet addWallet(Wallet wallet) throws InternalErrorException {
        String sql = """
            INSERT INTO `Wallet` (`UserID`, `WalletAddress`, `Network`)
            VALUES (?, ?, ?)
        """;
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, wallet.getUserId());
                ps.setString(2, wallet.getWalletAddress());
                ps.setString(3, wallet.getNetwork());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key == null) throw new InternalErrorException(new Exception("No generated key"));
            wallet.setWalletId(key.intValue());
            return wallet;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public void updateWallet(Wallet wallet) throws InternalErrorException {
        String sql = """
            UPDATE `Wallet`
            SET `UserID` = ?, `WalletAddress` = ?, `Network` = ?
            WHERE `WalletID` = ?
        """;
        try {
            int rows = jdbc.update(sql,
                    wallet.getUserId(),
                    wallet.getWalletAddress(),
                    wallet.getNetwork(),
                    wallet.getWalletId());
            if (rows == 0) throw new InternalErrorException(new Exception("Wallet not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Transactional
    @Override
    public Wallet deleteWallet(int id) throws InternalErrorException {
        String select = """
            SELECT `WalletID`, `UserID`, `WalletAddress`, `Network`
            FROM `Wallet` WHERE `WalletID` = ?
        """;
        String delete = "DELETE FROM `Wallet` WHERE `WalletID` = ?";
        try {
            Wallet before = jdbc.queryForObject(select, mapper, id);
            int rows = jdbc.update(delete, id);
            if (rows == 0) throw new InternalErrorException(new Exception("Delete failed."));
            return before;
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException(new Exception("Wallet not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }
}
