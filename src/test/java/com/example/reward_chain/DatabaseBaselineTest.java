// src/test/java/com/example/reward_chain/DatabaseBaselineTest.java
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
    @DisplayName("seed present: counts + first/last name separately")
    void counts_and_firstLastName() {
        // baseline counts from reset_db()
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM `User`", Integer.class));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM `Category`", Integer.class));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM `Transaction`", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM `Rewards`", Integer.class));

        // verify we’re on the expected schema
        String db = jdbc.queryForObject("SELECT DATABASE()", String.class);
        assertNotNull(db);

        // fetch first and last name separately (no concat)
        Map<String, Object> row = jdbc.queryForMap(
                "SELECT `FirstName`, `LastName` FROM `User` ORDER BY `UserID` LIMIT 1"
        );
        assertEquals("Alice", row.get("FirstName"));
        assertEquals("Admin", row.get("LastName"));
    }
}
