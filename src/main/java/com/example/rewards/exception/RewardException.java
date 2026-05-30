package com.example.rewards.exception;

/**
 * Custom exception thrown when reward-related
 * business validation fails.
 */
public class RewardException extends RuntimeException{

    /**
     * Creates a new reward exception with the specified message.
     *
     * @param message exception message
     */
    public RewardException(String message) {
        super(message);
    }

}
