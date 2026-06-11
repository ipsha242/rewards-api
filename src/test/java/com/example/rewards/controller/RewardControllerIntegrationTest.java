package com.example.rewards.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

                .andExpect(jsonPath("$[?(@.customerId==1)].totalRewards").value(405))

                .andExpect(jsonPath("$[?(@.customerId==2)].totalRewards").value(210))

                .andExpect(jsonPath("$[?(@.customerId==3)].totalRewards").value(350))

                .andExpect(jsonPath("$[?(@.customerId==4)].totalRewards").value(190))

                .andExpect(jsonPath("$[?(@.customerId==5)].totalRewards").value(580));
    }
}