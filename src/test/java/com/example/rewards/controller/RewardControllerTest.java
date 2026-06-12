package com.example.rewards.controller;

import com.example.rewards.dto.RewardDTO;
import com.example.rewards.entity.Customer;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private Customer customer;
    private RewardDTO sampleRewardDTO;


    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(101L);
        customer.setName("John Doe");

        sampleTransaction = new Transaction();
        sampleTransaction.setId(1L);
        sampleTransaction.setCustomer(customer);
        sampleTransaction.setAmount(BigDecimal.valueOf(150.0));
        sampleTransaction.setTransactionDate(LocalDate.of(2026, 3, 15));

        sampleRewardDTO = new RewardDTO();
        sampleRewardDTO.setCustomerId(101L);
        sampleRewardDTO.setCustomerName("John Doe");
        sampleRewardDTO.setTotalRewards(BigDecimal.valueOf(150));

        Map<String, BigDecimal> monthlyMap = new HashMap<>();
        monthlyMap.put("MARCH", BigDecimal.valueOf(150));
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

    @Test
    void getRewardsByDateRange_ValidDates_ReturnsOk() throws Exception {

        when(rewardService.getRewardsByDateRange(
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 5, 31))).thenReturn(List.of(sampleRewardDTO));

        mockMvc.perform(get("/transaction/rewards/range")
                        .param("startDate", "2026-03-01")
                        .param("endDate", "2026-05-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(101))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));

        verify(rewardService, times(1))
                .getRewardsByDateRange(
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 5, 31));
    }

    @Test
    void getRewardsByDateRange_NoDates_ReturnsOk() throws Exception {

        when(rewardService.getRewardsByDateRange(null, null)).thenReturn(List.of(sampleRewardDTO));

        mockMvc.perform(get("/transaction/rewards/range")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(rewardService, times(1)).getRewardsByDateRange(null, null);
    }

    @Test
    void getRewardsByDateRange_ReturnsBadRequest() throws Exception {

        when(rewardService.getRewardsByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RewardException("Start date cannot be after end date"));

        mockMvc.perform(get("/transaction/rewards/range")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-05-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRewardsByDateRange_endDateMissing_ReturnsBadRequest()
            throws Exception {

        when(rewardService.getRewardsByDateRange(any(), any()))
                .thenThrow(new RewardException("Both start date and end date must be provided"));

        mockMvc.perform(get("/transaction/rewards/range")
                        .param("startDate", "2026-03-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRewardsByDateRange_StartDateMissing_ReturnsBadRequest()
            throws Exception {

        when(rewardService.getRewardsByDateRange(any(), any()))
                .thenThrow(new RewardException("Both start date and end date must be provided"));

        mockMvc.perform(get("/transaction/rewards/range")
                        .param("endDate", "2026-05-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRewardsByCustomerId_ReturnsOk() throws Exception {

        when(rewardService.getRewardsByCustomerId(eq(1L), isNull(), isNull()))
                .thenReturn(sampleRewardDTO);

        mockMvc.perform(get("/transaction/rewards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(101));
    }

    @Test
    void getRewardsByCustomerId_WithDateRange_ReturnsOk() throws Exception {

        when(rewardService.getRewardsByCustomerId(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(sampleRewardDTO);

        mockMvc.perform(get("/transaction/rewards/1")
                                .param("startDate", "2026-01-01")
                                .param("endDate", "2026-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void getRewardsByCustomerId_ServiceThrowsException_ReturnsBadRequest()
            throws Exception {

        when(rewardService.getRewardsByCustomerId(anyLong(), any(), any()))
                .thenThrow(new RewardException("Customer not found"));

        mockMvc.perform(get("/transaction/rewards/999"))
                .andExpect(status().isBadRequest());
    }
}