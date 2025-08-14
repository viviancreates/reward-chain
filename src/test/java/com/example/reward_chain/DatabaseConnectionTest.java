package com.example.reward_chain;

import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Spring context loads successfully")
    void contextLoads() {
        // This verifies that the Spring Boot application context starts without errors.
    }

    @Test
    @DisplayName("SQL connectivity and repository call should work")
    void sqlConnectivity() {
        // Basic SQL round-trip test
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(one, "SELECT 1 returned null");
        assertEquals(1, one, "SELECT 1 did not return 1");

        // Confirm current database is accessible
        String dbName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        assertNotNull(dbName, "DATABASE() returned null");

        // Repo call should not throw
        assertDoesNotThrow(() -> userRepo.getAllUsers(), "UserRepo call failed");
    }

    @Test
    @DisplayName("Fetch the first user's full name from the repository")
    void firstUserName_viaRepo() {
        // Retrieve all users, failing the test if an exception occurs
        List<User> users = assertDoesNotThrow(
                (ThrowingSupplier<List<User>>) () -> userRepo.getAllUsers(),
                "userRepo.getAllUsers threw unexpectedly"
        );

        // Validate that we have at least one user
        assertFalse(users.isEmpty(), "No users found");

        // Build the full name and verify it's not blank
        String fullName = (users.get(0).getFirstName() + " " + users.get(0).getLastName()).trim();
        assertFalse(fullName.isBlank(), "First user's name is blank or null");

        // Output for visibility in test logs
        System.out.println("First user name (via repo): " + fullName);
    }
}
