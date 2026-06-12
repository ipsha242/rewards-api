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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

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
        assertEquals(0, BigDecimal.valueOf(90).compareTo(dto.getTotalRewards()));
        assertEquals(0, BigDecimal.valueOf(90).compareTo(dto.getMonthlyRewards().get("2024-03")));
    }

    @Test
    void getRewards_amountBetween50And100_returnsCorrectPoints() {
        transaction.setAmount(BigDecimal.valueOf(75.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, BigDecimal.valueOf(25).compareTo(result.get(0).getTotalRewards()));
    }

    @Test
    void getRewards_amountExactly100_returnsCorrectPoints() {
        transaction.setAmount(BigDecimal.valueOf(100.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, BigDecimal.valueOf(50).compareTo(result.get(0).getTotalRewards()));
    }

    @Test
    void getRewards_amountExactly50_returnsZeroPoints() {
        transaction.setAmount(BigDecimal.valueOf(50.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, BigDecimal.ZERO.compareTo(result.get(0).getTotalRewards()));
    }

    @Test
    void getRewards_amountBelow50_returnsZeroPoints() {
        transaction.setAmount(BigDecimal.valueOf(30.0));
        when(rewardRepository.findByTransactionDateBetween(
                any(LocalDate.class),
                any(LocalDate.class))).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewards();

        assertEquals(0, BigDecimal.ZERO.compareTo(result.get(0).getTotalRewards()));
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
        assertEquals(0, BigDecimal.valueOf(115).compareTo(dto.getTotalRewards()));
        assertEquals(0, BigDecimal.valueOf(90).compareTo(dto.getMonthlyRewards().get("2024-01")));
        assertEquals(0, BigDecimal.valueOf(25).compareTo(dto.getMonthlyRewards().get("2024-02")));
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

        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.get(0).getTotalRewards()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(result.get(0).getMonthlyRewards().get("2024-03")));
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

    @Test
    void getRewardsByDateRange_NullDates_UsesFindAll() {

        when(rewardRepository.findAll()).thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewardsByDateRange(null, null);

        assertEquals(1, result.size());

        verify(rewardRepository, times(1)).findAll();
        verify(rewardRepository, never()).findByTransactionDateBetween(any(), any());
    }

    @Test
    void getRewardsByDateRange_StartDateAfterEndDate_ThrowsException() {

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByDateRange(
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 5, 1))
        );

        assertEquals("Start date cannot be after end date", ex.getMessage());
    }

    @Test
    void getRewardsByDateRange_ValidDates_ReturnsRewards() {

        when(rewardRepository.findByTransactionDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(transaction));

        List<RewardDTO> result = transactionService.getRewardsByDateRange(
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 5, 31));

        assertEquals(1, result.size());
        assertEquals(0, BigDecimal.valueOf(90).compareTo(result.get(0).getTotalRewards()));
    }

    @Test
    void getRewardsByDateRange_EndDateNull_ThrowsException() {

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByDateRange(
                        LocalDate.of(2026, 3, 1), null));

        assertEquals("Both start date and end date must be provided", ex.getMessage());
    }

    @Test
    void getRewardsByDateRange_StartDateNull_ThrowsException() {

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByDateRange(null,
                        LocalDate.of(2026, 5, 31)));

        assertEquals("Both start date and end date must be provided", ex.getMessage());
    }

    @Test
    void getRewardsByCustomerId_ValidCustomer_ReturnsRewards() {

        when(rewardRepository.findByCustomer_Id(1L)).thenReturn(List.of(transaction));

        RewardDTO result = transactionService.getRewardsByCustomerId(
                        1L,
                        null,
                        null);

        assertEquals(1L, result.getCustomerId());
        assertEquals("Alice", result.getCustomerName());
        assertEquals(0, BigDecimal.valueOf(90).compareTo(result.getTotalRewards()));

        verify(rewardRepository).findByCustomer_Id(1L);
    }

    @Test
    void getRewardsByCustomerId_ValidDateRange_ReturnsRewards() {

        when(rewardRepository
                .findByCustomer_IdAndTransactionDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(transaction));

        RewardDTO result = transactionService.getRewardsByCustomerId(
                        1L,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 12, 31));

        assertEquals(0, BigDecimal.valueOf(90).compareTo(result.getTotalRewards()));
    }

    @Test
    void getRewardsByCustomerId_CustomerNotFound_ThrowsException() {

        when(rewardRepository.findByCustomer_Id(1L)).thenReturn(Collections.emptyList());

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByCustomerId(
                        1L,
                        null,
                        null));

        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void getRewardsByCustomerId_StartDateNull_ThrowsException() {

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByCustomerId(
                        1L,
                        null,
                        LocalDate.now()));

        assertEquals("Both start date and end date must be provided", ex.getMessage());
    }

    @Test
    void getRewardsByCustomerId_EndDateNull_ThrowsException() {

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByCustomerId(
                        1L,
                        LocalDate.now(),
                        null));

        assertEquals("Both start date and end date must be provided", ex.getMessage());
    }

    @Test
    void getRewardsByCustomerId_StartDateAfterEndDate_ThrowsException() {

        RewardException ex = assertThrows(RewardException.class,
                () -> transactionService.getRewardsByCustomerId(
                        1L,
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 5, 1)));

        assertEquals("Start date cannot be after end date", ex.getMessage());
    }
}
