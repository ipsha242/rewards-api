package com.example.rewards.service;

import com.example.rewards.DTO.RewardDTO;
import com.example.rewards.entity.Transaction;
import com.example.rewards.exception.RewardException;
import com.example.rewards.repository.RewardRepository;
import com.example.rewards.service.ServiceImpl.RewardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setCustomerId(1L);
        transaction.setCustomerName("Alice");
        transaction.setAmount(120.0);
        transaction.setTransactionDate(LocalDate.of(2024, 3, 15));
    }

    @Test
    void getRewards_singleTransaction_above100_returnsCorrectPoints() {
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

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
        transaction.setAmount(75.0);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(25, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_amountExactly100_returnsCorrectPoints() {
        transaction.setAmount(100.0);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(50, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_amountExactly50_returnsZeroPoints() {
        transaction.setAmount(50.0);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_amountBelow50_returnsZeroPoints() {
        transaction.setAmount(30.0);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, result.get(0).getTotalRewards());
    }

    @Test
    void getRewards_multipleTransactionsSameCustomer_accumulatesPointsAndMonthlyBreakdown() {
        Transaction jan = new Transaction();
        jan.setCustomerId(1L);
        jan.setCustomerName("Alice");
        jan.setAmount(120.0);
        jan.setTransactionDate(LocalDate.of(2024, 1, 10));

        Transaction feb = new Transaction();
        feb.setCustomerId(1L);
        feb.setCustomerName("Alice");
        feb.setAmount(75.0);
        feb.setTransactionDate(LocalDate.of(2024, 2, 20));

        when(rewardRepository.findAll()).thenReturn(List.of(jan, feb));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(1, result.size());
        RewardDTO dto = result.get(0);
        assertEquals(115, dto.getTotalRewards());
        assertEquals(90, dto.getMonthlyRewards().get("JANUARY"));
        assertEquals(25, dto.getMonthlyRewards().get("FEBRUARY"));
    }

    @Test
    void getRewards_multipleTransactionsSameCustomerSameMonth_accumulatesMonthlyPoints() {
        Transaction t1 = new Transaction();
        t1.setCustomerId(1L);
        t1.setCustomerName("Alice");
        t1.setAmount(120.0);
        t1.setTransactionDate(LocalDate.of(2024, 3, 1));

        Transaction t2 = new Transaction();
        t2.setCustomerId(1L);
        t2.setCustomerName("Alice");
        t2.setAmount(60.0);
        t2.setTransactionDate(LocalDate.of(2024, 3, 20));

        when(rewardRepository.findAll()).thenReturn(List.of(t1, t2));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(100, result.get(0).getTotalRewards());
        assertEquals(100, result.get(0).getMonthlyRewards().get("MARCH"));
    }

    @Test
    void getRewards_multipleDistinctCustomers_returnsOneEntryEach() {
        Transaction t2 = new Transaction();
        t2.setCustomerId(2L);
        t2.setCustomerName("Bob");
        t2.setAmount(60.0);    // 10 pts
        t2.setTransactionDate(LocalDate.of(2024, 3, 5));

        when(rewardRepository.findAll()).thenReturn(List.of(transaction, t2));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(2, result.size());
    }

    @Test
    void getRewards_noTransactions_throwsRewardException() {
        when(rewardRepository.findAll()).thenReturn(Collections.emptyList());

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("No transactions found", ex.getMessage());
    }

    @Test
    void getRewards_negativeAmount_throwsRewardException() {
        transaction.setAmount(-10.0);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("Transaction amount cannot be negative", ex.getMessage());
    }

    @Test
    void getRewards_zeroCustomerId_throwsRewardException() {
        transaction.setCustomerId(0L);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("Customer ID cannot be empty", ex.getMessage());
    }

    @Test
    void getRewards_nullTransactionDate_throwsRewardException() {
        transaction.setTransactionDate(null);
        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewards());
        assertEquals("Transaction date is required", ex.getMessage());
    }
}
