package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Allocations;

import java.util.List;

public interface AllocationsRepo {

    // Get allocation settings by ID
    Allocations getAllocationById(int id) throws RecordNotFoundException, InternalErrorException;

    // Get a user's allocation preferences (each user should have one)
    Allocations getAllocationByUserId(int userId) throws RecordNotFoundException, InternalErrorException;

    // Get all allocation settings in the system
    List<Allocations> getAllAllocations() throws InternalErrorException;

    // Add new allocation settings (when user registers)
    Allocations addAllocation(Allocations allocation) throws InternalErrorException;

    // Update user's allocation preferences (change from 60% ETH to 70% ETH, etc.)
    void updateAllocation(Allocations allocation) throws InternalErrorException;

    // Delete allocation settings by ID - returns the deleted allocation
    Allocations deleteAllocation(int id) throws InternalErrorException;
}