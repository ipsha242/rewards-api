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

    @NotNull
    private Long customerId;

    @NotBlank
    private String customerName;

    @NotNull
    private Map<String, Integer> monthlyRewards;

    @NotNull
    @PositiveOrZero
    private Integer totalRewards;
}