package com.example.rewards.service.ServiceImpl;

import com.example.rewards.DTO.RewardDTO;
import com.example.rewards.entity.Transaction;
import com.example.rewards.exception.RewardException;
import com.example.rewards.repository.RewardRepository;
import com.example.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of reward related business operations.
 * Responsible for transaction management and reward point calculations.
 */
@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    /**
     * Saves a customer transaction.
     *
     * @param transaction transaction details to be saved
     * @return saved transaction
     */
    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return rewardRepository.save(transaction);
    }

    /**
     * Calculates monthly and total reward points
     * for all customers based on transaction history.
     *
     * @return list of customer rewards
     */
    @Override
    public List<RewardDTO> getRewards() {
        List<Transaction> transactions = rewardRepository.findAll();

        if (transactions.isEmpty()) {
            throw new RewardException("No transactions found");
        }

        Map<Long, RewardDTO> rewardsMap = new HashMap<>();

        for (Transaction transaction : transactions) {

            if (transaction.getAmount() < 0) {
                throw new RewardException("Transaction amount cannot be negative");
            }
            Long customerId = transaction.getCustomerId();

            if (customerId == 0) {
                throw new RewardException("Customer ID cannot be empty");
            }
            if (transaction.getTransactionDate() == null) {
                throw new RewardException("Transaction date is required");
            }

            RewardDTO response = rewardsMap.get(customerId);

            if (response == null) {

                response = new RewardDTO();

                response.setCustomerId(customerId);
                response.setCustomerName(transaction.getCustomerName());
                response.setMonthlyRewards(new HashMap<>());
                response.setTotalRewards(0);

                rewardsMap.put(customerId, response);
            }

            int points = calculateRewardPoints(transaction.getAmount());

            String month = transaction.getTransactionDate()
                    .getMonth()
                    .toString();

            Map<String, Integer> monthlyRewards = response.getMonthlyRewards();

            monthlyRewards.put(month, monthlyRewards.getOrDefault(month, 0) + points);

            response.setTotalRewards(response.getTotalRewards() + points);
        }

        return new ArrayList<>(rewardsMap.values());
    }

    private int calculateRewardPoints(Double amount) {

        int points = 0;

        if (amount > 100) {

            points += 50;
            points += (amount.intValue() - 100) * 2;

        } else if (amount > 50) {

            points += amount.intValue() - 50;
        }
        return points;
    }
}
