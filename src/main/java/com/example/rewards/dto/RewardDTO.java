package com.example.rewards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO containing monthly and total reward points
 * earned by a customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDTO {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @NotBlank(message = "Customer Name cannot be blank")
    private String customerName;

    @NotNull
    private Map<String, Long> monthlyRewards;

    @NotNull(message = "Total Rewards cannot be null")
    @PositiveOrZero(message = "Total Rewards cannot be negative")
    private Long totalRewards;
}