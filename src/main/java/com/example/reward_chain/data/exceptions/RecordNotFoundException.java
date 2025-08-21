package com.example.reward_chain.data.exceptions;

public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
        super();
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}