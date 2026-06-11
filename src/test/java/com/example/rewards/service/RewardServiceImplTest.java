package com.example.rewards.service;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Customer;
import com.example.rewards.entity.Transaction;
import com.example.rewards.exception.RewardException;
import com.example.rewards.repository.RewardRepository;
import com.example.rewards.service.serviceImpl.RewardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RewardServiceImpl.
 * Verifies reward calculation logic and validation scenarios.
 */
@ExtendWith(MockitoExtension.class)
public class RewardServiceImplTest {

    @Mock
    private RewardRepository rewardRepository;

    @InjectMocks
    private RewardServiceImpl transactionService;

    private Transaction transaction;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(BigDecimal.valueOf(120.0));
        transaction.setTransactionDate(LocalDate.of(2024, 3, 15));
    }

    @Test
    void getRewards_singleTransaction_above100_returnsCorrectPoints() {
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(1, result.size());
        RewardDTO dto = result.get(0);
        assertEquals(1L, dto.getCustomerId());
        assertEquals("Alice", dto.getCustomerName());
        assertEquals(90, dto.getTotalRewards());
        assertEquals(90, dto.getMonthlyRewards().get("MARCH"));
    }

    @Test
    void getRewards_amountBetween50And100_returnsCorrectPoints() {
        transaction.setAmount(BigDecimal.valueOf(75.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(25, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_amountExactly100_returnsCorrectPoints() {
        transaction.setAmount(BigDecimal.valueOf(100.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(50, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_amountExactly50_returnsZeroPoints() {
        transaction.setAmount(BigDecimal.valueOf(50.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_amountBelow50_returnsZeroPoints() {
        transaction.setAmount(BigDecimal.valueOf(30.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_multipleTransactionsSameCustomer_accumulatesPointsAndMonthlyBreakdown() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        Transaction jan = new Transaction();
        jan.setCustomer(customer);
        jan.setAmount(BigDecimal.valueOf(120.0));
        jan.setTransactionDate(LocalDate.of(2024, 1, 10));

        Transaction feb = new Transaction();
        feb.setCustomer(customer);
        feb.setAmount(BigDecimal.valueOf(75.0));
        feb.setTransactionDate(LocalDate.of(2024, 2, 20));

        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(jan, feb));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(1, result.size());
        RewardDTO dto = result.get(0);
        assertEquals(115, dto.getTotalRewards());
        assertEquals(90, dto.getMonthlyRewards().get("JANUARY"));
        assertEquals(25, dto.getMonthlyRewards().get("FEBRUARY"));
    }

    @Test
    void getRewards_multipleTransactionsSameCustomerSameMonth_accumulatesMonthlyPoints() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");

        Transaction t1 = new Transaction();
        t1.setCustomer(customer);
        t1.setAmount(BigDecimal.valueOf(120.0));
        t1.setTransactionDate(LocalDate.of(2024, 3, 1));

        Transaction t2 = new Transaction();
        t2.setCustomer(customer);
        t2.setAmount(BigDecimal.valueOf(60.0));
        t2.setTransactionDate(LocalDate.of(2024, 3, 20));

        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(t1, t2));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(100, result.get(0).getTotalRewards());
        assertEquals(100, result.get(0).getMonthlyRewards().get("MARCH"));
    }

    @Test
    void getRewards_multipleDistinctCustomers_returnsOneEntryEach() {
        customer = new Customer();
        customer.setId(2L);
        customer.setName("Bob");

        Transaction t2 = new Transaction();
        t2.setCustomer(customer);
        t2.setAmount(BigDecimal.valueOf(60.0));    // 10 pts
        t2.setTransactionDate(LocalDate.of(2024, 3, 5));

        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction, t2));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(2, result.size());
    }

    @Test
    void getRewards_noTransactions_throwsRewardException() {
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(Collections.emptyList());

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("No transactions found", ex.getMessage());
    }

    @Test
    void getRewards_negativeAmount_throwsRewardException() {
        transaction.setAmount(BigDecimal.valueOf(-10.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("Transaction amount cannot be negative", ex.getMessage());
    }

    @Test
    void getRewards_nullCustomer_throwsRewardException() {
        transaction.setCustomer(null);
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("Customer cannot be empty", ex.getMessage());
    }

    @Test
    void getRewards_customerIdZero_throwsRewardException() {
        Customer customer = new Customer();
        customer.setId(0L);
        customer.setName("Alice");

        transaction.setCustomer(customer);

        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(transaction));

        RewardException ex = assertThrows(
                RewardException.class,
                () -> transactionService.getRewards()
        );

        assertEquals("Customer ID cannot be empty", ex.getMessage());
    }

    @Test
    void getRewards_nullTransactionDate_throwsRewardException() {
        transaction.setTransactionDate(null);
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("Transaction date is required", ex.getMessage());
    }
}
