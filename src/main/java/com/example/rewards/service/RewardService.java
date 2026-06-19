package com.example.rewards.service;

import com.example.rewards.dto.RewardDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for customer reward calculations.
 */
public interface RewardService {


    List<RewardDTO> getRewards();

    List<RewardDTO> getRewardsByDateRange(LocalDate startDate, LocalDate endDate);

    RewardDTO getRewardsByCustomerId(Long customerId, LocalDate starDate, LocalDate endDate);
}
