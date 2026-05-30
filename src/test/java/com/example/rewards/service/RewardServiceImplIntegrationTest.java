package com.example.rewards.service;

import com.example.rewards.DTO.RewardDTO;
import com.example.rewards.service.ServiceImpl.RewardServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals(405, john.getTotalRewards());
        assertEquals(90, john.getMonthlyRewards().get("MARCH"));
        assertEquals(25, john.getMonthlyRewards().get("APRIL"));
        assertEquals(290, john.getMonthlyRewards().get("MAY"));

        RewardDTO david = result.stream().filter(r -> r.getCustomerId() == 2L).findFirst().orElseThrow();
        assertEquals("David", david.getCustomerName());
        assertEquals(210, david.getTotalRewards());
        assertEquals(0, david.getMonthlyRewards().get("MARCH"));
        assertEquals(210, david.getMonthlyRewards().get("MAY"));

        RewardDTO lucas = result.stream().filter(r -> r.getCustomerId() == 3L).findFirst().orElseThrow();
        assertEquals("Lucas", lucas.getCustomerName());
        assertEquals(350, lucas.getTotalRewards());
        assertEquals(350, lucas.getMonthlyRewards().get("MAY"));

        RewardDTO sarah = result.stream().filter(r -> r.getCustomerId() == 4L).findFirst().orElseThrow();
        assertEquals("Sarah", sarah.getCustomerName());
        assertEquals(190, sarah.getTotalRewards());
        assertEquals(150, sarah.getMonthlyRewards().get("MARCH"));
        assertEquals(40, sarah.getMonthlyRewards().get("APRIL"));

        RewardDTO sharon = result.stream().filter(r -> r.getCustomerId() == 5L).findFirst().orElseThrow();
        assertEquals("Sharon", sharon.getCustomerName());
        assertEquals(580, sharon.getTotalRewards());
        assertEquals(110, sharon.getMonthlyRewards().get("MARCH"));
        assertEquals(20, sharon.getMonthlyRewards().get("APRIL"));
        assertEquals(450, sharon.getMonthlyRewards().get("MAY"));
    }
}