package com.example.rewards.controller;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Transaction;
import com.example.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
     * Retrieves monthly and total reward points
     * for all customers over past 3 months.
     *
     * @return list of customer reward summaries
     */
    @GetMapping("/rewards")
    public ResponseEntity<List<RewardDTO>> getRewards() {
        List<RewardDTO> rewards = rewardService.getRewards();
        return ResponseEntity.ok(rewards);
    }

    /**
     * Retrieves monthly and total reward points
     * for all customers between a time range.
     *
     * @return list of customer reward summaries
     */
    @GetMapping("/rewards/range")
    public ResponseEntity<List<RewardDTO>> getRewardsByDateRange(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        List<RewardDTO> rewards = rewardService.getRewardsByDateRange(startDate, endDate);
        return ResponseEntity.ok(rewards);
    }

    /**
     * Retrieves monthly and total reward points
     * for each customer based on customerId.
     *
     * @return customer reward details
     */
    @GetMapping("/rewards/{customerId}")
    public ResponseEntity<RewardDTO> getRewardsByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        RewardDTO reward = rewardService.getRewardsByCustomerId(customerId, startDate, endDate);
        return ResponseEntity.ok(reward);
    }
}
