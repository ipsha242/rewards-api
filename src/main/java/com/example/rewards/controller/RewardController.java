package com.example.rewards.controller;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Transaction;
import com.example.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * REST controller responsible for exposing reward related APIs.
 */
@RestController
@RequestMapping("/transaction")
public class RewardController {

    @Autowired
    private RewardService rewardService;


    /**
     * Creates a new transaction for a customer.
     *
     * @param transaction transaction details including customer information,
     *                    purchase amount and transaction date
     * @return the created transaction with HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        Transaction savedTransaction = rewardService.saveTransaction(transaction);
        return new ResponseEntity<>(savedTransaction, HttpStatus.CREATED);
    }

    /**
     * Retrieves monthly and total reward points
     * for all customers.
     *
     * @return list of customer reward summaries
     */
    @GetMapping("/rewards")
    public ResponseEntity<List<RewardDTO>> getRewards() {
        List<RewardDTO> rewards = rewardService.getRewards();
        return ResponseEntity.ok(rewards);
    }
}
