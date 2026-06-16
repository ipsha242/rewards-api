package com.example.rewards.controller;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.service.RewardService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;


import java.time.LocalDate;
import java.util.List;
/**
 * REST controller responsible for exposing reward related APIs.
 */
@Slf4j
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
        log.info("Received request to fetch global rewards overview for the past 3 months.");
        List<RewardDTO> rewards = rewardService.getRewards();

        log.debug("Successfully retrieved {} reward summary records.", rewards.size());
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
        log.info("Received request to fetch rewards by date range: startDate={}, endDate={}", startDate, endDate);
        List<RewardDTO> rewards = rewardService.getRewardsByDateRange(startDate, endDate);

        log.debug("Found {} records matching date range filter [{} to {}]", rewards.size(), startDate, endDate);
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
            @PathVariable @NotNull @Positive Long customerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        log.info("Received request to fetch rewards for customer ID: {} with range optional filters: startDate={}, endDate={}", customerId, startDate, endDate);
        RewardDTO reward = rewardService.getRewardsByCustomerId(customerId, startDate, endDate);

        log.debug("Successfully compiled rewards summary for customer ID: {}. Total points calculated: {}", customerId, reward.getTotalRewards());
        return ResponseEntity.ok(reward);
    }
}
