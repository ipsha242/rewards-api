package com.example.rewards.service.serviceImpl;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Customer;
import com.example.rewards.entity.Transaction;
import com.example.rewards.exception.ResourceNotFoundException;
import com.example.rewards.exception.RewardException;
import com.example.rewards.repository.CustomerRepository;
import com.example.rewards.repository.RewardRepository;
import com.example.rewards.service.RewardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * Implementation of reward related business operations.
 * Responsible for transaction management and reward point calculations.
 */
@Slf4j
@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private CustomerRepository customerRepository;


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

        log.info("Fetching transactions for default rolling 3-month period: {} to {}", startDate, endDate);
        List<Transaction> transactions = rewardRepository.findByTransactionDateBetween(startDate, endDate);

        log.debug("Found {} raw transactions for the default rolling period.", transactions.size());
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
            log.info("No date range provided. Falling back to fetching all historical transactions.");
            transactions = rewardRepository.findAll();
        }
        else if (startDate == null || endDate == null) {
            log.warn("Validation failure: Partial date range provided. startDate={}, endDate={}", startDate, endDate);
            throw new RewardException("Both start date and end date must be provided");
        }
        else {
            if (startDate.isAfter(endDate)) {
                log.warn("Validation failure: Start date {} is after end date {}", startDate, endDate);
                throw new RewardException("Start date cannot be after end date");
            }
            log.info("Fetching transactions for custom date range: {} to {}", startDate, endDate);
            transactions = rewardRepository.findByTransactionDateBetween(startDate, endDate);
        }
        log.debug("Found {} raw transactions matching range query parameters.", transactions.size());
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

        log.info("Processing target reward query for customerId: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.warn("Customer not found for customerId: {}", customerId);
                    return new ResourceNotFoundException("Customer not found");
                });
        List<Transaction> transactions;

        if (startDate == null && endDate == null) {
            log.debug("Fetching lifetime transactions for customerId: {}", customerId);
            transactions = rewardRepository.findByCustomer_Id(customerId);
        }
        else if (startDate == null || endDate == null) {
            log.warn("Validation failure for customer {} query: Partial date range provided.", customerId);
            throw new RewardException("Both start date and end date must be provided");
        }
        else {
            if (startDate.isAfter(endDate)) {
                log.warn("Validation failure for customer {} query: Start date {} is after end date {}", customerId, startDate, endDate);
                throw new RewardException("Start date cannot be after end date");
            }

            log.debug("Fetching transactions for customerId: {} within range: {} to {}", customerId, startDate, endDate);
            transactions = rewardRepository.findByCustomer_IdAndTransactionDateBetween(customerId, startDate, endDate);
        }
        if (transactions.isEmpty()) {
            log.info("Customer {} exists but has no transactions for the requested criteria.", customerId);

            RewardDTO response = new RewardDTO();

            response.setCustomerId(customerId);
            response.setCustomerName(customer.getName());
            response.setMonthlyRewards(new HashMap<>());
            response.setTotalRewards(0L);

            return response;
        }
        log.info("Compiling rewards data stream containing {} records for customer: '{}' (ID: {})",
                transactions.size(), transactions.get(0).getCustomer().getName(), customerId);
        RewardDTO response = new RewardDTO();

        response.setCustomerId(customerId);
        response.setCustomerName(transactions.get(0).getCustomer().getName());
        response.setMonthlyRewards(new HashMap<>());
        response.setTotalRewards(0L);

        for (Transaction transaction : transactions) {

            long points = calculateRewardPoints(transaction.getAmount());

            String month = YearMonth
                    .from(transaction.getTransactionDate())
                    .toString();

            Map<String, Long> monthlyRewards = response.getMonthlyRewards();

            monthlyRewards.put(month, monthlyRewards.getOrDefault(month, 0L) + points);

            response.setTotalRewards(response.getTotalRewards() + points);
        }
        log.info("Completed processing for customerId: {}. Final aggregated total points: {}", customerId, response.getTotalRewards());
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
            log.warn("No transactions found.");
            return Collections.emptyList();
        }

        log.info("Beginning batch aggregation pipeline for {} global transactions.", transactions.size());
        Map<Long, RewardDTO> rewardsMap = new HashMap<>();

        for (Transaction transaction : transactions) {

            if (transaction.getCustomer() == null) {
                log.error("Corrupted database row detected: Transaction ID {} has no linked Customer entity.", transaction.getId());
                throw new RewardException("Customer cannot be empty");
            }

            if (transaction.getCustomer().getId() == null || transaction.getCustomer().getId() == 0) {
                log.error("Corrupted database row detected: Transaction ID {} has an unassigned/zero Customer ID reference.", transaction.getId());
                throw new RewardException("Customer ID cannot be empty");
            }

            if (transaction.getTransactionDate() == null) {
                log.error("Corrupted database row detected: Transaction ID {} is missing a valid timestamp value.", transaction.getId());
                throw new RewardException("Transaction date is required");
            }

            if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                log.error("Corrupted database row detected: Transaction ID {} contains an invalid or negative amount: {}", transaction.getId(), transaction.getAmount());
                throw new RewardException("Transaction amount cannot be negative");
            }

            Long customerId = transaction.getCustomer().getId();

            RewardDTO response = rewardsMap.get(customerId);

            if (response == null) {
                response = new RewardDTO();

                response.setCustomerId(customerId);
                response.setCustomerName(transaction.getCustomer().getName());
                response.setMonthlyRewards(new HashMap<>());
                response.setTotalRewards(0L);

                rewardsMap.put(customerId, response);
            }

            long points = calculateRewardPoints(transaction.getAmount());

            String month = YearMonth
                    .from(transaction.getTransactionDate())
                    .toString();

            Map<String, Long> monthlyRewards =
                    response.getMonthlyRewards();

            monthlyRewards.put(month, monthlyRewards.getOrDefault(month, 0L) + points);

            response.setTotalRewards(response.getTotalRewards() + points);
        }
        log.info("Batch aggregation completed successfully. Grouped and compiled profiles for {} distinct customers.", rewardsMap.size());
        return new ArrayList<>(rewardsMap.values());
    }

    /**
     * Calculates reward points for a transaction.
     *
     * @param amount transaction amount
     * @return reward points earned
     */
    private long calculateRewardPoints(BigDecimal amount) {

        long points = 0L;

        if (amount.compareTo(BigDecimal.valueOf(100)) > 0) {

            points += 50;
            points += amount.subtract(BigDecimal.valueOf(100))
                    .multiply(BigDecimal.valueOf(2))
                    .longValue();

        } else if (amount.compareTo(BigDecimal.valueOf(50)) > 0) {

            points += amount.subtract(BigDecimal.valueOf(50))
                    .longValue();
        }
        return points;
    }
}
