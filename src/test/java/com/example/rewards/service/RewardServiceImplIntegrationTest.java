package com.example.rewards.service;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.service.serviceImpl.RewardServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Integration tests for RewardServiceImpl.
 * Verifies reward calculations using the actual
 * Spring Boot application context and H2 database.
 */
@SpringBootTest
@Transactional
public class RewardServiceImplIntegrationTest {

    @Autowired
    private RewardServiceImpl rewardService;

    @Test
    void getRewards_DatabaseIntegration_CalculatesAccuratePointsForSeededData() {
        List<RewardDTO> result = rewardService.getRewards();

        assertNotNull(result);
        assertEquals(5, result.size(), "Should find exactly 5 unique customers from the dataset");

        RewardDTO john = result.stream().filter(r -> r.getCustomerId() == 1L).findFirst().orElseThrow();
        assertEquals("John", john.getCustomerName());
        assertEquals(405L, john.getTotalRewards());

        RewardDTO david = result.stream().filter(r -> r.getCustomerId() == 2L).findFirst().orElseThrow();
        assertEquals("David", david.getCustomerName());
        assertEquals(210L, david.getTotalRewards());

        RewardDTO lucas = result.stream().filter(r -> r.getCustomerId() == 3L).findFirst().orElseThrow();
        assertEquals("Lucas", lucas.getCustomerName());
        assertEquals(350L, lucas.getTotalRewards());

        RewardDTO sarah = result.stream().filter(r -> r.getCustomerId() == 4L).findFirst().orElseThrow();
        assertEquals("Sarah", sarah.getCustomerName());
        assertEquals(190L, sarah.getTotalRewards());

        RewardDTO sharon = result.stream().filter(r -> r.getCustomerId() == 5L).findFirst().orElseThrow();
        assertEquals("Sharon", sharon.getCustomerName());
        assertEquals(580L, sharon.getTotalRewards());
    }

    @Test
    void getRewardsByDateRange_ValidRange_ReturnsRewards() {

        List<RewardDTO> result = rewardService.getRewardsByDateRange(
                        LocalDate.now().minusMonths(3),
                        LocalDate.now());

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getRewardsByCustomerId_ReturnsCustomerRewards() {

        RewardDTO result = rewardService.getRewardsByCustomerId(1L, null, null);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
    }

    @Test
    void getRewardsByCustomerId_NoTransactionsForDateRange_ReturnsZeroRewards() {

        RewardDTO result = rewardService.getRewardsByCustomerId(
                1L,
                LocalDate.of(2030, 1, 1),
                LocalDate.of(2030, 12, 31));

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals(0L, result.getTotalRewards());
        assertTrue(result.getMonthlyRewards().isEmpty());
    }
}