package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.User;

import java.util.List;

public interface UserRepo {

    // Get a user by their ID
    User getUserById(int id) throws RecordNotFoundException, InternalErrorException;

    // Get a user by their email (for login functionality)
    User getUserByEmail(String email) throws RecordNotFoundException, InternalErrorException;

    // Get all users in the system
    List<User> getAllUsers() throws InternalErrorException;

    // Add a new user (registration) - returns user with ID populated
    User addUser(User user) throws InternalErrorException;

    // Update existing user information
    void updateUser(User user) throws InternalErrorException;

    // Delete a user by ID - returns the deleted user
    User deleteUser(int id) throws InternalErrorException;
}