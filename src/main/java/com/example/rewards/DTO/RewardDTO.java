package com.example.rewards.DTO;

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

    private Long customerId;
    private String customerName;
    private Map<String, Integer> monthlyRewards;
    private Integer totalRewards;
}