package com.example.rewards.service;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Transaction;

import java.util.List;

/**
 * Service interface for customer reward calculations.
 */
public interface RewardService {

    Transaction saveTransaction(Transaction transaction);

    List<RewardDTO> getRewards();
}
