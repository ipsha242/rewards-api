package com.example.rewards.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Integration tests for the reward controller.
 * Verifies end-to-end reward calculations using
 * the actual Spring Boot application context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies reward calculations and monthly reward
     * breakdown for all customers using test data.
     */
    @Test
    void getRewards_FullIntegration_ReturnsAccurateCalculations() throws Exception {
        mockMvc.perform(get("/transaction/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))

                .andExpect(jsonPath("$[?(@.customerId==1)].totalRewards").value(405.0))

                .andExpect(jsonPath("$[?(@.customerId==2)].totalRewards").value(210.0))

                .andExpect(jsonPath("$[?(@.customerId==3)].totalRewards").value(350.0))

                .andExpect(jsonPath("$[?(@.customerId==4)].totalRewards").value(190.0))

                .andExpect(jsonPath("$[?(@.customerId==5)].totalRewards").value(580.0));
    }

    @Test
    void getRewardsByDateRange_ValidRange_ReturnsOk() throws Exception {

        mockMvc.perform(get("/transaction/rewards/range")
                        .param("startDate", LocalDate.now()
                                        .minusMonths(3)
                                        .toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getRewardsByCustomerId_ReturnsOk() throws Exception {

        mockMvc.perform(get("/transaction/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId")
                                .value(1));
    }
}