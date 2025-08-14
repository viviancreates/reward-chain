package com.example.reward_chain.data;

import com.example.reward_chain.data.exceptions.InternalErrorException;
import com.example.reward_chain.data.exceptions.RecordNotFoundException;
import com.example.reward_chain.model.Wallet;

import java.util.List;

public interface WalletRepo {

    // Get a wallet by its ID
    Wallet getWalletById(int id) throws RecordNotFoundException, InternalErrorException;

    // Get a user's wallet (each user should have one wallet)
    Wallet getWalletByUserId(int userId) throws RecordNotFoundException, InternalErrorException;

    // Get wallet by its address (for sending rewards)
    Wallet getWalletByAddress(String walletAddress) throws RecordNotFoundException, InternalErrorException;

    // Get all wallets in the system
    List<Wallet> getAllWallets() throws InternalErrorException;

    // Add a new wallet (when user registers)
    Wallet addWallet(Wallet wallet) throws InternalErrorException;

    // Update an existing wallet
    void updateWallet(Wallet wallet) throws InternalErrorException;

    // Delete a wallet by ID - returns the deleted wallet
    Wallet deleteWallet(int id) throws InternalErrorException;
}