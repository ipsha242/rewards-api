package com.example.rewards.service.serviceImpl;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Transaction;
import com.example.rewards.exception.RewardException;
import com.example.rewards.repository.RewardRepository;
import com.example.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
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
     * Calculates monthly and total reward points
     * for all customers based on last 3 months transaction history.
     *
     * @return list of customer rewards
     */
    @Override
    public List<RewardDTO> getRewards() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);

        List<Transaction> transactions = rewardRepository.findByTransactionDateBetween(startDate, endDate);

        return processRewards(transactions);
    }


    /**
     * Calculates monthly and total reward points
     * for all customers based on the transaction history over a time range.
     *
     * @param startDate range start date
     * @param endDate range end date
     * @return customer reward summaries
     */
    @Override
    public List<RewardDTO> getRewardsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions;

        if (startDate == null && endDate == null) {
            transactions = rewardRepository.findAll();
        }
        else if (startDate == null || endDate == null) {
            throw new RewardException("Both start date and end date must be provided");
        }
        else {
            if (startDate.isAfter(endDate)) {
                throw new RewardException("Start date cannot be after end date");
            }

            transactions = rewardRepository.findByTransactionDateBetween(startDate, endDate);
        }

        return processRewards(transactions);
    }

    /**
     * Calculates monthly and total reward points
     * for each customer based on customerId over a time range.
     *
     * @param customerId customerId
     * @param startDate range start date
     * @param endDate range end date
     * @return customer reward details
     */
    @Override
    public RewardDTO getRewardsByCustomerId(Long customerId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions;

        if (startDate == null && endDate == null) {
            transactions = rewardRepository.findByCustomer_Id(customerId);
        }
        else if (startDate == null || endDate == null) {
            throw new RewardException("Both start date and end date must be provided");
        }
        else {
            if (startDate.isAfter(endDate)) {
                throw new RewardException("Start date cannot be after end date");
            }

            transactions = rewardRepository.findByCustomer_IdAndTransactionDateBetween(customerId, startDate, endDate);
        }
        if (transactions.isEmpty()) {
            throw new RewardException("Customer not found");
        }

        RewardDTO response = new RewardDTO();

        response.setCustomerId(customerId);
        response.setCustomerName(transactions.get(0).getCustomer().getName());
        response.setMonthlyRewards(new HashMap<>());
        response.setTotalRewards(BigDecimal.ZERO);

        for (Transaction transaction : transactions) {

            BigDecimal points = calculateRewardPoints(transaction.getAmount());

            String month = YearMonth
                    .from(transaction.getTransactionDate())
                    .toString();

            Map<String, BigDecimal> monthlyRewards = response.getMonthlyRewards();

            monthlyRewards.put(month, monthlyRewards.getOrDefault(month, BigDecimal.ZERO).add(points));

            response.setTotalRewards(response.getTotalRewards().add(points));
        }
        return response;
    }

    /**
     * Calculates monthly and total rewards
     * for transactions within the given date range.
     *
     * @param transactions range start date
     * @return customer reward summaries
     */
    private List<RewardDTO> processRewards(List<Transaction> transactions) {

        if (transactions.isEmpty()) {
            throw new RewardException("No transactions found");
        }

        Map<Long, RewardDTO> rewardsMap = new HashMap<>();

        for (Transaction transaction : transactions) {

            if (transaction.getCustomer() == null) {
                throw new RewardException("Customer cannot be empty");
            }

            if (transaction.getCustomer().getId() == 0) {
                throw new RewardException("Customer ID cannot be empty");
            }

            if (transaction.getTransactionDate() == null) {
                throw new RewardException("Transaction date is required");
            }

            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new RewardException("Transaction amount cannot be negative");
            }

            Long customerId = transaction.getCustomer().getId();

            RewardDTO response = rewardsMap.get(customerId);

            if (response == null) {
                response = new RewardDTO();

                response.setCustomerId(customerId);
                response.setCustomerName(transaction.getCustomer().getName());
                response.setMonthlyRewards(new HashMap<>());
                response.setTotalRewards(BigDecimal.ZERO);

                rewardsMap.put(customerId, response);
            }

            BigDecimal points = calculateRewardPoints(transaction.getAmount());

            String month = YearMonth
                    .from(transaction.getTransactionDate())
                    .toString();

            Map<String, BigDecimal> monthlyRewards =
                    response.getMonthlyRewards();

            monthlyRewards.put(month, monthlyRewards.getOrDefault(month, BigDecimal.ZERO).add(points));

            response.setTotalRewards(response.getTotalRewards().add(points));
        }
        return new ArrayList<>(rewardsMap.values());
    }

    /**
     * Calculates reward points for a transaction.
     *
     * @param amount transaction amount
     * @return reward points earned
     */
    private BigDecimal calculateRewardPoints(BigDecimal amount) {

        BigDecimal points = BigDecimal.ZERO;

        if (amount.compareTo(BigDecimal.valueOf(100)) > 0) {

            points = points.add(BigDecimal.valueOf(50));

            points = points.add(amount.subtract(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(2)));

        } else if (amount.compareTo(BigDecimal.valueOf(50)) > 0) {

            points = points.add(amount.subtract(BigDecimal.valueOf(50)));
        }
        return points;
    }
}
