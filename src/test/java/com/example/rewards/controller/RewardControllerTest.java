package com.example.rewards.controller;

import com.example.rewards.DTO.RewardDTO;
import com.example.rewards.entity.Transaction;
import com.example.rewards.exception.RewardException;
import com.example.rewards.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RewardController.
 * Verifies request handling and response generation.
 */
@WebMvcTest(RewardController.class)
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RewardService rewardService;

    private Transaction sampleTransaction;
    private RewardDTO sampleRewardDTO;


    @BeforeEach
    void setUp() {
        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setCustomerId(101L);
        sampleTransaction.setCustomerName("John Doe");
        sampleTransaction.setAmount(150.0);
        sampleTransaction.setTransactionDate(LocalDate.of(2026, 3, 15));

        sampleRewardDTO = new RewardDTO();
        sampleRewardDTO.setCustomerId(101L);
        sampleRewardDTO.setCustomerName("John Doe");
        sampleRewardDTO.setTotalRewards(150);

        Map<String, Integer> monthlyMap = new HashMap<>();
        monthlyMap.put("MARCH", 150);
        sampleRewardDTO.setMonthlyRewards(monthlyMap);
    }

    /**
     * Verifies that rewards are returned successfully
     * when reward data is available.
     */
    @Test
    void getRewards_ActiveDataPresent_ReturnsOkAndPopulatedList() throws Exception {
        when(rewardService.getRewards()).thenReturn(List.of(sampleRewardDTO));

        mockMvc.perform(get("/transaction/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerId").value(101))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[0].totalRewards").value(150))
                .andExpect(jsonPath("$[0].monthlyRewards.MARCH").value(150));

        verify(rewardService, times(1)).getRewards();
    }

    /**
     * Verifies that a bad request response is returned
     * when the service throws a RewardException.
     */
    @Test
    void getRewards_ServiceThrowsRewardException_ReturnsBadRequestStatus() throws Exception {
        when(rewardService.getRewards()).thenThrow(new RewardException("No transactions found"));

        mockMvc.perform(get("/transaction/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(rewardService, times(1)).getRewards();
    }
}