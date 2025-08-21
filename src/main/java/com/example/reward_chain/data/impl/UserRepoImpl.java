package com.example.reward_chain.data.impl;

import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.data.mappers.UserMapper;
import com.example.reward_chain.model.User;
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
public class UserRepoImpl implements UserRepo {
    private final JdbcTemplate jdbc;
    private final UserMapper mapper;

    public UserRepoImpl(JdbcTemplate jdbc, UserMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public User getUserById(int id) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `UserID`, `FirstName`, `LastName`, `Email`, `Password`
            FROM `User` WHERE `UserID` = ?
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
    public User getUserByEmail(String email) throws RecordNotFoundException, InternalErrorException {
        String sql = """
            SELECT `UserID`, `FirstName`, `LastName`, `Email`, `Password`
            FROM `User` WHERE `Email` = ?
        """;
        try {
            return jdbc.queryForObject(sql, mapper, email);
        } catch (EmptyResultDataAccessException e) {
            throw new RecordNotFoundException();
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public List<User> getAllUsers() throws InternalErrorException {
        String sql = """
            SELECT `UserID`, `FirstName`, `LastName`, `Email`, `Password`
            FROM `User` ORDER BY `UserID`
        """;
        try {
            return jdbc.query(sql, mapper);
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public User addUser(User user) throws InternalErrorException {
        String sql = """
            INSERT INTO `User` (`FirstName`, `LastName`, `Email`, `Password`)
            VALUES (?, ?, ?, ?)
        """;
        KeyHolder kh = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
                return ps;
            }, kh);
            Number key = kh.getKey();
            if (key == null) throw new InternalErrorException(new Exception("No generated key"));
            user.setUserId(key.intValue());
            return user;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    public void updateUser(User user) throws InternalErrorException {
        String sql = """
            UPDATE `User`
            SET `FirstName` = ?, `LastName` = ?, `Email` = ?, `Password` = ?
            WHERE `UserID` = ?
        """;
        try {
            int rows = jdbc.update(sql,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getUserId());
            if (rows == 0) throw new InternalErrorException(new Exception("User not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Transactional
    @Override
    public User deleteUser(int id) throws InternalErrorException {
        String select = """
            SELECT `UserID`, `FirstName`, `LastName`, `Email`, `Password`
            FROM `User` WHERE `UserID` = ?
        """;
        String delete = "DELETE FROM `User` WHERE `UserID` = ?";
        try {
            User before = jdbc.queryForObject(select, mapper, id);
            int rows = jdbc.update(delete, id);
            if (rows == 0) throw new InternalErrorException(new Exception("Delete failed."));
            return before;
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException(new Exception("User not found."));
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }
}
