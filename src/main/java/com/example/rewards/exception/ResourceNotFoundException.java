package com.example.rewards.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception is typically used when a customer, transaction,
 * or rewards record does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new ResourceNotFoundException with the specified message.
     *
     * @param message detailed description of the missing resource
     */
    public ResourceNotFoundException(String message) {

        super(message);
    }
}