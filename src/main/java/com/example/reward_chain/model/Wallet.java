package com.example.reward_chain.model;

public class Wallet {
    private int walletId;
    private int userId;
    private String walletAddress;
    private String network;


    public Wallet() {}

    public Wallet(int userId, String walletAddress, String network) {
        this.userId = userId;
        this.walletAddress = walletAddress;
        this.network = network;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", walletAddress='" + walletAddress + '\'' +
                ", network='" + network + '\'' +
                '}';
    }
}