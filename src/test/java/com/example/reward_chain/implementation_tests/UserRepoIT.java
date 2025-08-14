package com.example.reward_chain.implementation_tests;

import com.example.reward_chain.BaseDbTest;
import com.example.reward_chain.data.UserRepo;
import com.example.reward_chain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepoIT extends BaseDbTest {

    @Autowired UserRepo userRepo;

    @Test
    @DisplayName("getAllUsers size = 8 (seed)")
    void getAllUsers_size8() throws Exception {
        int expected = 8;
        List<User> users = userRepo.getAllUsers();
        int actual = users.size();
        assertEquals(expected, actual, "getAllUsers size: expected=" + expected + ", actual=" + actual);
    }

    @Test
    @DisplayName("add then update firstName")
    void add_then_update_firstName() throws Exception {
        User u = new User("New","User","new.user@example.com","pw");
        u = userRepo.addUser(u);
        int id = u.getUserId();
        assertTrue(id > 0, "assigned id: expected>0, actual=" + id);

        String expectedFirst = "Updated";
        u.setFirstName(expectedFirst);
        userRepo.updateUser(u);

        User fetched = userRepo.getUserById(id);
        String actualFirst = fetched.getFirstName();
        assertEquals(expectedFirst, actualFirst, "firstName: expected=" + expectedFirst + ", actual=" + actualFirst);
    }
}
