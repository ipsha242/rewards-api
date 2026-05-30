package com.example.rewards.exception;

import com.example.rewards.DTO.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 * Handles custom and generic exceptions and returns
 * a standardized error response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles reward-related exceptions.
     *
     * @param ex the thrown reward exception
     * @return error response with HTTP 400 status
     */
    @ExceptionHandler(RewardException.class)
    public ResponseEntity<ErrorResponse> handleRewardException(RewardException ex) {

        ErrorResponse error = new ErrorResponse(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unexpected application exceptions.
     *
     * @param ex the thrown exception
     * @return error response with HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        ErrorResponse error = new ErrorResponse("Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
