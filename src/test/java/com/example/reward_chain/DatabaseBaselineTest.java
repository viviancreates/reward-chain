package com.example.reward_chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseBaselineTest extends BaseDbTest {

    @Autowired JdbcTemplate jdbc;

    @Test
    @DisplayName("baseline: table counts match seed")
    void counts_match_seed() {
        int expectedUsers = 8;
        int actualUsers = jdbc.queryForObject("SELECT COUNT(*) FROM `User`", Integer.class);
        assertEquals(expectedUsers, actualUsers, "User count: expected=" + expectedUsers + ", actual=" + actualUsers);

        int expectedCategories = 6;
        int actualCategories = jdbc.queryForObject("SELECT COUNT(*) FROM `Category`", Integer.class);
        assertEquals(expectedCategories, actualCategories, "Category count: expected=" + expectedCategories + ", actual=" + actualCategories);

        int expectedWallets = 8;
        int actualWallets = jdbc.queryForObject("SELECT COUNT(*) FROM `Wallet`", Integer.class);
        assertEquals(expectedWallets, actualWallets, "Wallet count: expected=" + expectedWallets + ", actual=" + actualWallets);

        int expectedAlloc = 8;
        int actualAlloc = jdbc.queryForObject("SELECT COUNT(*) FROM `Allocations`", Integer.class);
        assertEquals(expectedAlloc, actualAlloc, "Allocations count: expected=" + expectedAlloc + ", actual=" + actualAlloc);

        int expectedTx = 18;
        int actualTx = jdbc.queryForObject("SELECT COUNT(*) FROM `Transaction`", Integer.class);
        assertEquals(expectedTx, actualTx, "Transaction count: expected=" + expectedTx + ", actual=" + actualTx);

        int expectedRewards = 18;
        int actualRewards = jdbc.queryForObject("SELECT COUNT(*) FROM `Rewards`", Integer.class);
        assertEquals(expectedRewards, actualRewards, "Rewards count: expected=" + expectedRewards + ", actual=" + actualRewards);
    }

    @Test
    @DisplayName("baseline: first user is Alice Admin (ordered by UserID)")
    void first_user_is_alice() {
        Map<String,Object> row = jdbc.queryForMap(
                "SELECT `FirstName`,`LastName` FROM `User` ORDER BY `UserID` LIMIT 1"
        );
        String expectedFirst = "Alice";
        String actualFirst = (String) row.get("FirstName");
        assertEquals(expectedFirst, actualFirst, "FirstName: expected=" + expectedFirst + ", actual=" + actualFirst);

        String expectedLast = "Admin";
        String actualLast = (String) row.get("LastName");
        assertEquals(expectedLast, actualLast, "LastName: expected=" + expectedLast + ", actual=" + actualLast);
    }
}

