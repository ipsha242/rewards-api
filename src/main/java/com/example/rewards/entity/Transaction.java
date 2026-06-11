package com.example.rewards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a customer purchase transaction
 * used for reward point calculations.
 */
@Entity
@Table(name = "TRANSACTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "customer_id", nullable = false)
//    private Long customerId;
//
//    @Column(name = "customer_name", nullable = false)
//    private String customerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
}